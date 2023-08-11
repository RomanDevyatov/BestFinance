package com.romandevyatov.bestfinance.ui.fragments.update.history

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.data.entities.TransferHistory
import com.romandevyatov.bestfinance.data.entities.Wallet
import com.romandevyatov.bestfinance.data.entities.relations.TransferHistoryWithWallets
import com.romandevyatov.bestfinance.data.roomdb.converters.LocalDateTimeRoomTypeConverter
import com.romandevyatov.bestfinance.data.validation.EmptyValidator
import com.romandevyatov.bestfinance.data.validation.IsDigitValidator
import com.romandevyatov.bestfinance.data.validation.IsEqualValidator
import com.romandevyatov.bestfinance.data.validation.base.BaseValidator
import com.romandevyatov.bestfinance.databinding.FragmentUpdateTransferHistoryBinding
import com.romandevyatov.bestfinance.ui.adapters.spinner.GroupSpinnerAdapter
import com.romandevyatov.bestfinance.ui.adapters.spinner.SpinnerItem
import com.romandevyatov.bestfinance.utils.Constants.clickDelayMs
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.UpdateTransferHistoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.util.*

@AndroidEntryPoint
class UpdateTransferHistoryFragment : Fragment() {

    private var _binding: FragmentUpdateTransferHistoryBinding? = null
    private val binding get() = _binding!!

    private val updateTransferHistoryViewModel: UpdateTransferHistoryViewModel by viewModels()

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

        binding.reusable.transferButton.text = "Update"

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOnBackPressedHandler()

        updateTransferHistoryViewModel.getTransferHistoryWithWalletsByIdLiveData(args.transferHistoryId)
            .observe(viewLifecycleOwner) { transferHistoryWithWallets ->
                historyWithWalletsGlobal = transferHistoryWithWallets.copy()

                setupSpinnersValues(transferHistoryWithWallets.walletFrom,
                    transferHistoryWithWallets.walletTo
                )

                setupSpinners()

                setupDateTimeFiledValues()

                val transferHistory = transferHistoryWithWallets.transferHistory
                binding.reusable.commentEditText.setText(transferHistory.comment)
                binding.reusable.amountEditText.setText(transferHistory.amount.toString())

                setButtonOnClickListener(view)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
        updateTransferHistoryViewModel.allWalletsNotArchivedLiveData.observe(viewLifecycleOwner) { wallets ->

            val spinnerWalletItems = getWalletItemsForSpinner(wallets)

            val walletSpinnerAdapter =
                GroupSpinnerAdapter(
                    requireContext(),
                    R.layout.item_with_del,
                    spinnerWalletItems,
                    null,
                    null)

            binding.reusable.toWalletNameSpinner.setAdapter(walletSpinnerAdapter)
            binding.reusable.fromWalletNameSpinner.setAdapter(walletSpinnerAdapter)
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
        val datePickerListener = DatePickerDialog.OnDateSetListener() {
                _, year, month, dayOfMonth ->
            selectedDate.set(Calendar.YEAR, year)
            selectedDate.set(Calendar.MONTH, month)
            selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            binding.reusable.dateEditText.setText(LocalDateTimeRoomTypeConverter.dateFormat.format(selectedDate.time))
        }

        binding.reusable.dateEditText.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                datePickerListener,
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.reusable.dateEditText.setText(LocalDateTimeRoomTypeConverter.dateFormat.format(selectedDate.time))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setTimeEditText() {
        val selectedTime = Calendar.getInstance()
        selectedTime.timeInMillis =
            historyWithWalletsGlobal.transferHistory.date?.atZone(java.time.ZoneId.systemDefault())?.toInstant()?.toEpochMilli()!!

        val timePickerListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
            selectedTime.set(Calendar.MINUTE, minute)
            binding.reusable.timeEditText.setText(LocalDateTimeRoomTypeConverter.timeFormat.format(selectedTime.time))
        }

        binding.reusable.timeEditText.setOnClickListener {
            TimePickerDialog(
                requireContext(),
                timePickerListener,
                selectedTime.get(Calendar.HOUR_OF_DAY),
                selectedTime.get(Calendar.MINUTE),
                false
            ).show()
        }

        binding.reusable.timeEditText.setText(LocalDateTimeRoomTypeConverter.timeFormat.format(selectedTime.time))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setButtonOnClickListener(view: View) {
        binding.reusable.transferButton.setOnClickListener {
            if (!isButtonClickable) return@setOnClickListener
            isButtonClickable = false
            view.isEnabled = false

                val amountBinding = binding.reusable.amountEditText.text.toString().trim()
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

                val dateBindingValidation = EmptyValidator(dateBinding).validate()
                binding.reusable.dateLayout.error = if (!dateBindingValidation.isSuccess) getString(dateBindingValidation.message) else null

                val timeBindingValidation = EmptyValidator(timeBinding).validate()
                binding.reusable.timeLayout.error = if (!timeBindingValidation.isSuccess) getString(timeBindingValidation.message) else null

                if (isEqualSpinnerNamesValidation.isSuccess
                    && amountValidation.isSuccess
                    && dateBindingValidation.isSuccess
                    && timeBindingValidation.isSuccess
                ) {
                    updateOldWallets(
                        historyWithWalletsGlobal.walletFrom,
                        historyWithWalletsGlobal.walletTo,
                        historyWithWalletsGlobal.transferHistory.amount)

                    val walletFromId = walletSpinnerItemsGlobal?.find { it.name == walletFromNameBinding }?.id
                    val walletToId = walletSpinnerItemsGlobal?.find { it.name == walletToNameBinding }?.id

                    val fullDateTime = dateBinding.plus(" ").plus(timeBinding)
                    val parsedLocalDateTime = LocalDateTime.from(LocalDateTimeRoomTypeConverter.dateTimeFormatter.parse(fullDateTime))

                    val updatedTransferHistory = TransferHistory(
                        id = historyWithWalletsGlobal.transferHistory.id,
                        amount = amountBinding.toDouble(),
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
            }, clickDelayMs.toLong())
        }
    }

    private fun navigateToHistory() {
        val action = UpdateTransferHistoryFragmentDirections.actionUpdateTransferHistoryFragmentToHistoryFragment()
        action.initialTabIndex = 1
        findNavController().navigate(action)
    }

    private fun updateOldWallets(walletFrom: Wallet, walletTo: Wallet, amount: Double) {
        val updatedBalanceFromOld = walletFrom.balance.plus(amount)
        val updatedOutputFromOld = walletFrom.output.minus(amount)
        updateOldWalletFrom(walletFrom, updatedBalanceFromOld, updatedOutputFromOld)

        val updatedBalanceToOld = walletTo.balance.minus(amount)
        val updatedInputToOld = walletTo.input.minus(amount)
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
                archivedDate = wallet.archivedDate
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
                archivedDate = wallet.archivedDate
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
