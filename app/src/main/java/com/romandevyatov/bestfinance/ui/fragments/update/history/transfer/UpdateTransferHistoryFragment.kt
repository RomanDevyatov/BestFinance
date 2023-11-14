package com.romandevyatov.bestfinance.ui.fragments.update.history.transfer

import com.romandevyatov.bestfinance.utils.numberpad.addGenericTextWatcher
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.TextInputEditText
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.data.entities.TransferHistory
import com.romandevyatov.bestfinance.data.entities.Wallet
import com.romandevyatov.bestfinance.data.entities.relations.TransferHistoryWithWallets
import com.romandevyatov.bestfinance.data.roomdb.converters.LocalDateTimeRoomTypeConverter
import com.romandevyatov.bestfinance.data.roomdb.converters.LocalDateTimeRoomTypeConverter.Companion.dateFormat
import com.romandevyatov.bestfinance.data.roomdb.converters.LocalDateTimeRoomTypeConverter.Companion.timeFormat
import com.romandevyatov.bestfinance.data.validation.EmptyValidator
import com.romandevyatov.bestfinance.data.validation.IsDigitValidator
import com.romandevyatov.bestfinance.data.validation.IsEqualValidator
import com.romandevyatov.bestfinance.data.validation.TwoDigitsAfterPoint
import com.romandevyatov.bestfinance.data.validation.base.BaseValidator
import com.romandevyatov.bestfinance.databinding.FragmentUpdateTransferHistoryBinding
import com.romandevyatov.bestfinance.ui.adapters.spinner.GroupSpinnerAdapter
import com.romandevyatov.bestfinance.ui.adapters.spinner.models.SpinnerItem
import com.romandevyatov.bestfinance.utils.BackStackLogger
import com.romandevyatov.bestfinance.utils.Constants.CLICK_DELAY_MS
import com.romandevyatov.bestfinance.utils.DateTimeUtils
import com.romandevyatov.bestfinance.utils.TextFormatter
import com.romandevyatov.bestfinance.utils.WindowUtil
import com.romandevyatov.bestfinance.utils.numberpad.processInput
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.UpdateTransferHistoryViewModel
import com.romandevyatov.bestfinance.viewmodels.shared.SharedInitialTabIndexViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.Calendar

@AndroidEntryPoint
class UpdateTransferHistoryFragment : Fragment() {

    private var _binding: FragmentUpdateTransferHistoryBinding? = null
    private val binding get() = _binding!!

    private val updateTransferHistoryViewModel: UpdateTransferHistoryViewModel by viewModels()

    private val sharedInitialTabIndexViewModel: SharedInitialTabIndexViewModel by activityViewModels()

    private lateinit var historyWithWalletsGlobal: TransferHistoryWithWallets

    private var walletSpinnerItemsGlobal: MutableList<SpinnerItem>? = null

    private var isButtonClickable = true
    private val args: UpdateTransferHistoryFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateTransferHistoryBinding.inflate(inflater, container, false)

        binding.reusable.transferButton.text = getString(R.string.update)

        BackStackLogger.logBackStack(findNavController())

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.reusable.amountEditText.addGenericTextWatcher()

        setOnBackPressedHandler()

        updateTransferHistoryViewModel.getTransferHistoryWithWalletsByIdLiveData(args.transferHistoryId)
            .observe(viewLifecycleOwner) { transferHistoryWithWallets ->
                transferHistoryWithWallets?.let {
                    historyWithWalletsGlobal = it.copy()

                    setupSpinnersValues(
                        it.walletFrom,
                        it.walletTo
                    )

                    setupSpinners()

                    setupDateTimeFiledValues()

                    val transferHistory = it.transferHistory
                    binding.reusable.commentEditText.setText(transferHistory.comment)

                    val formattedAmountText = TextFormatter.removeTrailingZeros(transferHistory.amount.toString())
                    binding.reusable.amountEditText.setText(formattedAmountText)

                    val formattedTargetAmountText = TextFormatter.removeTrailingZeros(transferHistory.amountTarget.toString())
                    binding.reusable.amountTargetEditText.setText(formattedTargetAmountText)
                }

                setButtonOnClickListener(view)
            }

//        binding.reusable.amountEditText.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//
//            override fun afterTextChanged(editable: Editable?) {
//                processAmount(this, editable, binding.reusable.amountEditText, binding.reusable.amountTargetEditText)
//            }
//        })
//
//        binding.reusable.amountTargetEditText.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//
//            override fun afterTextChanged(editable: Editable?) {
//                processAmount(this, editable, binding.reusable.amountTargetEditText, binding.reusable.amountEditText)
//            }
//        })
    }

    private fun processAmount(
        watcher: TextWatcher,
        editable: Editable?,
        currentAmountEditText: TextInputEditText,
        anotherAmountEditText: TextInputEditText) {
        val text = editable.toString()
        val updatedText = processInput(text)

        if (text != updatedText) {
            currentAmountEditText.removeTextChangedListener(watcher)
            currentAmountEditText.setText(updatedText)
            currentAmountEditText.setSelection(updatedText.length)
            currentAmountEditText.addTextChangedListener(watcher)
        }

        if (binding.reusable.toWalletNameSpinner.text.isNotEmpty() && binding.reusable.fromWalletNameSpinner.text.isNotEmpty()) {
            if (updatedText.toDoubleOrNull() != null) {
                updatedText.toDoubleOrNull()?.let { amount ->
                    val fromWalletName = binding.reusable.fromWalletNameSpinner.text.toString()
                    val toWalletName = binding.reusable.toWalletNameSpinner.text.toString()

                    val fromWalletId = walletSpinnerItemsGlobal?.find {
                        it.name == fromWalletName
                    }?.id

                    val toWalletId = walletSpinnerItemsGlobal?.find {
                        it.name == toWalletName
                    }?.id

                    lifecycleScope.launch {
                        val fromWallet = updateTransferHistoryViewModel.getWalletById(fromWalletId)
                        val toWallet = updateTransferHistoryViewModel.getWalletById(toWalletId)

                        val result = updateTransferHistoryViewModel.calculateTransferAmount(
                            amount,
                            fromWallet,
                            toWallet
                        )

                        anotherAmountEditText.setText(result.toString())
                    }
                } ?: run {
                    anotherAmountEditText.setText("")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun deleteRecord() {
        WindowUtil.showDeleteDialog(
            context = requireContext(),
            viewModel = updateTransferHistoryViewModel,
            message = getString(R.string.delete_confirmation_warning_message, "this transfer history"),
            itemId = args.transferHistoryId,
            isCountdown = false,
            rootView = binding.root
        ) { navigateToHistory() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupSpinnersValues(from: Wallet, to: Wallet) {
        binding.reusable.fromWalletNameSpinner.setText(from.name, false)
        binding.reusable.toWalletNameSpinner.setText(to.name, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupSpinners() {
        setWalletSpinnerAdapter()

        setWalletSpinnerOnItemClickListeners()
    }

    private fun setWalletSpinnerOnItemClickListeners() {
        setToWalletSpinnerOnItemClickListener()
        setFromWalletSpinnerOnItemClickListener()
    }

    private fun setWalletSpinnerAdapter() {
        updateTransferHistoryViewModel.allWalletsNotArchivedLiveData.observe(viewLifecycleOwner) { allWallets ->
            allWallets?.let { wallets ->
                val spinnerWalletItems = getWalletItemsForSpinner(wallets)

                val walletSpinnerAdapter =
                    GroupSpinnerAdapter(
                        requireContext(),
                        R.layout.item_with_del,
                        spinnerWalletItems,
                        null,
                        null
                    )

                binding.reusable.toWalletNameSpinner.setAdapter(walletSpinnerAdapter)
                binding.reusable.fromWalletNameSpinner.setAdapter(walletSpinnerAdapter)
            }
        }
    }

    private fun setToWalletSpinnerOnItemClickListener() {
        binding.reusable.toWalletNameSpinner.setOnItemClickListener {
                _, _, _, _ ->
        }
    }

    private fun setFromWalletSpinnerOnItemClickListener() {
        binding.reusable.fromWalletNameSpinner.setOnItemClickListener {
                _, _, _, _ ->
        }
    }

    private fun getWalletItemsForSpinner(walletList: List<Wallet>?): MutableList<SpinnerItem> {
        val spinnerItems: MutableList<SpinnerItem> = mutableListOf()

        walletList?.forEach { it ->
            spinnerItems.add(SpinnerItem(it.id, it.name))
        }

        walletSpinnerItemsGlobal = spinnerItems

        return spinnerItems
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupDateTimeFiledValues() {
        setDateEditText()
        setTimeEditText()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setDateEditText() {
        val selectedDate = Calendar.getInstance()
        selectedDate.timeInMillis =
            historyWithWalletsGlobal.transferHistory.date?.atZone(java.time.ZoneId.systemDefault())?.toInstant()?.toEpochMilli()!!

        DateTimeUtils.setupDatePicker(binding.reusable.dateEditText, dateFormat, selectedDate)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setTimeEditText() {
        val selectedTime = Calendar.getInstance()
        selectedTime.timeInMillis =
            historyWithWalletsGlobal.transferHistory.date?.atZone(java.time.ZoneId.systemDefault())?.toInstant()?.toEpochMilli()!!

        DateTimeUtils.setupTimePicker(binding.reusable.timeEditText, timeFormat, selectedTime)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setButtonOnClickListener(view: View) {
        binding.reusable.transferButton.setOnClickListener {
            if (!isButtonClickable) return@setOnClickListener
            isButtonClickable = false
            view.isEnabled = false

                val amountBinding = binding.reusable.amountEditText.text.toString().trim()
                val amountTargetBinding = binding.reusable.amountTargetEditText.text.toString().trim()
                val dateBinding = binding.reusable.dateEditText.text.toString().trim()
                val timeBinding = binding.reusable.timeEditText.text.toString().trim()
                val comment = binding.reusable.commentEditText.text.toString().trim()

                val walletFromNameBinding = binding.reusable.fromWalletNameSpinner.text.toString()
                val walletToNameBinding = binding.reusable.toWalletNameSpinner.text.toString()

                val isEqualSpinnerNamesValidation = IsEqualValidator(walletFromNameBinding, walletToNameBinding).validate()
                binding.reusable.fromWalletNameSpinnerLayout.error = if (!isEqualSpinnerNamesValidation.isSuccess) getString(isEqualSpinnerNamesValidation.message) else null
                binding.reusable.toWalletNameSpinnerLayout.error = if (!isEqualSpinnerNamesValidation.isSuccess) getString(isEqualSpinnerNamesValidation.message) else null

                val amountValidation = BaseValidator.validate(EmptyValidator(amountBinding), IsDigitValidator(amountBinding))
                binding.reusable.amountEditText.error = if (!amountValidation.isSuccess) getString(amountValidation.message) else null

                val amountTargetValidation = BaseValidator.validate(EmptyValidator(amountTargetBinding), IsDigitValidator(amountTargetBinding), TwoDigitsAfterPoint(amountTargetBinding))
                binding.reusable.amountEditText.error =
                if (!amountValidation.isSuccess) getString(amountValidation.message) else null

                val dateBindingValidation = EmptyValidator(dateBinding).validate()
                binding.reusable.dateLayout.error = if (!dateBindingValidation.isSuccess) getString(dateBindingValidation.message) else null

                val timeBindingValidation = EmptyValidator(timeBinding).validate()
                binding.reusable.timeLayout.error = if (!timeBindingValidation.isSuccess) getString(timeBindingValidation.message) else null

                if (isEqualSpinnerNamesValidation.isSuccess
                    && amountValidation.isSuccess
                    && amountTargetValidation.isSuccess
                    && dateBindingValidation.isSuccess
                    && timeBindingValidation.isSuccess
                ) {
                    updateOldWallets(
                        historyWithWalletsGlobal.walletFrom,
                        historyWithWalletsGlobal.walletTo,
                        historyWithWalletsGlobal.transferHistory.amount,
                        historyWithWalletsGlobal.transferHistory.amountTarget)

                    val walletFromId = walletSpinnerItemsGlobal?.find { it.name == walletFromNameBinding }?.id
                    val walletToId = walletSpinnerItemsGlobal?.find { it.name == walletToNameBinding }?.id

                    val fullDateTime = dateBinding.plus(" ").plus(timeBinding)
                    val parsedLocalDateTime = LocalDateTime.from(LocalDateTimeRoomTypeConverter.dateTimeFormatter.parse(fullDateTime))

                    val updatedTransferHistory = TransferHistory(
                        id = historyWithWalletsGlobal.transferHistory.id,
                        amount = amountBinding.toDouble(),
                        amountTarget = amountTargetBinding.toDouble(),
                        amountBase = amountBinding.toDouble(), // change
                        fromWalletId = walletFromId!!,
                        toWalletId = walletToId!!,
                        date = parsedLocalDateTime,
                        comment = comment,
                        createdDate = historyWithWalletsGlobal.transferHistory.createdDate
                    )
                    updateTransferHistoryViewModel.updateTransferHistoryAndWallets(updatedTransferHistory)

                    navigateToHistory()
            }

            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                isButtonClickable = true
                view.isEnabled = true
            }, CLICK_DELAY_MS)
        }
    }

    private fun navigateToHistory() {
        sharedInitialTabIndexViewModel.set(1)
        findNavController().popBackStack(R.id.history_fragment, false)
    }

    private fun updateOldWallets(
        walletFrom: Wallet,
        walletTo: Wallet,
        amount: Double,
        amountTarget: Double
    ) {
        val updatedBalanceFromOld = walletFrom.balance.plus(amount)
        val updatedOutputFromOld = walletFrom.output.minus(amount)
        updateOldWalletFrom(walletFrom, updatedBalanceFromOld, updatedOutputFromOld)

        val updatedBalanceToOld = walletTo.balance.minus(amountTarget)
        val updatedInputToOld = walletTo.input.minus(amountTarget)
        updateOldWalletTo(walletTo, updatedBalanceToOld, updatedInputToOld)
    }

    private fun updateOldWalletFrom(
        wallet: Wallet,
        updatedBalance: Double,
        updatedOutput: Double
    ) {
        updateTransferHistoryViewModel.updateWallet(
            Wallet(
                id = wallet.id,
                name = wallet.name,
                balance = updatedBalance,
                input = wallet.input,
                output = updatedOutput,
                description = wallet.description,
                archivedDate = wallet.archivedDate,
                currencyCode = wallet.currencyCode
            )
        )
    }

    private fun updateOldWalletTo(
        wallet: Wallet,
        updatedBalance: Double,
        updatedInput: Double
    ) {
        updateTransferHistoryViewModel.updateWallet(
            Wallet(
                id = wallet.id,
                name = wallet.name,
                balance = updatedBalance,
                input = updatedInput,
                output = wallet.output,
                description = wallet.description,
                archivedDate = wallet.archivedDate,
                currencyCode = wallet.currencyCode
            )
        )
    }

    private fun setOnBackPressedHandler() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateToHistory()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
}
