package com.romandevyatov.bestfinance.ui.fragments.adds.transfer

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.FragmentAddTransferBinding
import com.romandevyatov.bestfinance.data.entities.TransferHistory
import com.romandevyatov.bestfinance.data.entities.Wallet
import com.romandevyatov.bestfinance.data.roomdb.converters.LocalDateTimeRoomTypeConverter.Companion.dateFormat
import com.romandevyatov.bestfinance.data.roomdb.converters.LocalDateTimeRoomTypeConverter.Companion.dateTimeFormatter
import com.romandevyatov.bestfinance.data.roomdb.converters.LocalDateTimeRoomTypeConverter.Companion.timeFormat
import com.romandevyatov.bestfinance.ui.adapters.spinnerutils.SpinnerAdapter
import com.romandevyatov.bestfinance.data.validation.EmptyValidator
import com.romandevyatov.bestfinance.data.validation.IsDigitValidator
import com.romandevyatov.bestfinance.data.validation.IsEqualValidator
import com.romandevyatov.bestfinance.data.validation.base.BaseValidator
import com.romandevyatov.bestfinance.utils.Constants
import com.romandevyatov.bestfinance.utils.Constants.ADD_NEW_WALLET
import com.romandevyatov.bestfinance.utils.Constants.SPINNER_FROM
import com.romandevyatov.bestfinance.utils.Constants.SPINNER_TO
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.AddTransferViewModel
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.TransferHistoryViewModel
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.WalletViewModel
import com.romandevyatov.bestfinance.viewmodels.shared.SharedModifiedViewModel
import com.romandevyatov.bestfinance.viewmodels.shared.models.AddTransferForm
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.util.*

// TODO: validate if the same wallets are chosen

@AndroidEntryPoint
class AddTransferFragment : Fragment() {

    private var _binding: FragmentAddTransferBinding? = null
    private val binding get() = _binding!!

    private val walletViewModel: WalletViewModel by viewModels()
    private val transferHistoryViewModel: TransferHistoryViewModel by viewModels()
    private val addTransferViewModel: AddTransferViewModel by viewModels()

    private val sharedModViewModel: SharedModifiedViewModel<AddTransferForm> by activityViewModels()

    private var fromSpinnerValueGlobalBeforeAdd: String? = null
    private var toSpinnerValueGlobalBeforeAdd: String? = null

    private val args: AddTransferFragmentArgs by navArgs()

    private val archiveFromWalletListener =
        object : SpinnerAdapter.DeleteItemClickListener {

            @RequiresApi(Build.VERSION_CODES.O)
            override fun archive(name: String) {
                archiveWallet(name)

                sharedModViewModel.set(null)

                dismissAndDropdownSpinner(binding.fromWalletNameSpinner)
            }
        }

    private val archiveToWalletListener =
        object : SpinnerAdapter.DeleteItemClickListener {

            @RequiresApi(Build.VERSION_CODES.O)
            override fun archive(name: String) {
                archiveWallet(name)

                sharedModViewModel.set(null)

                dismissAndDropdownSpinner(binding.toWalletNameSpinner)
            }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun archiveWallet(name: String) {
        addTransferViewModel.archiveWallet(name)
        if (binding.toWalletNameSpinner.text.toString() == name) {
            binding.toWalletNameSpinner.text = null
            toSpinnerValueGlobalBeforeAdd = null
        }
        if (binding.fromWalletNameSpinner.text.toString() == name) {
            binding.fromWalletNameSpinner.text = null
            fromSpinnerValueGlobalBeforeAdd = null
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback = object : OnBackPressedCallback(
            true
        ) {
            override fun handleOnBackPressed() {
                sharedModViewModel.set(null)
                findNavController().navigate(R.id.action_add_transfer_fragment_to_navigation_home)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTransferBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSpinners()

        setDateEditText()
        setTimeEditText()

        setButtonOnClickListener()

        restoreAmountDateCommentValues()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        walletViewModel.allWalletsNotArchivedLiveData.removeObservers(viewLifecycleOwner)
    }

    private fun setSpinners() {
        setFromWalletSpinnerAdapter()
        setFromSpinnerListener()

        setToWalletSpinnerAdapter()
        setToSpinnerListener()
    }

    private fun setFromWalletSpinnerAdapter() {
        walletViewModel.allWalletsNotArchivedLiveData.observe(viewLifecycleOwner) { wallets ->

            val spinnerItems = getWalletItemsSpinner(wallets)

            val walletSpinnerAdapter =
                SpinnerAdapter(requireContext(), R.layout.item_with_del, spinnerItems,ADD_NEW_WALLET, archiveFromWalletListener)

            binding.fromWalletNameSpinner.setAdapter(walletSpinnerAdapter)

            setIfAvailableFromWalletSpinnersValue(walletSpinnerAdapter)
        }
    }

    private fun setToWalletSpinnerAdapter() {
        walletViewModel.allWalletsNotArchivedLiveData.observe(viewLifecycleOwner) { wallets ->

            val spinnerItems = getWalletItemsSpinner(wallets)

            val walletSpinnerAdapter = SpinnerAdapter(requireContext(), R.layout.item_with_del, spinnerItems, ADD_NEW_WALLET, archiveToWalletListener)

            binding.toWalletNameSpinner.setAdapter(walletSpinnerAdapter)

            setIfAvailableToWalletSpinnersValue(walletSpinnerAdapter)
        }
    }

    private fun getWalletItemsSpinner(walletList: List<Wallet>?): ArrayList<String> {
        val spinnerItems = ArrayList<String>()

        walletList?.forEach { it ->
            spinnerItems.add(it.name)
        }
        spinnerItems.add(ADD_NEW_WALLET)

        return spinnerItems
    }

    private fun setFromSpinnerListener() {
        binding.fromWalletNameSpinner.setOnItemClickListener {
                _, _, _, _ ->

            val selectedWalletNameFrom =
                binding.fromWalletNameSpinner.text.toString()

            if (selectedWalletNameFrom == ADD_NEW_WALLET) {
                binding.fromWalletNameSpinner.setText(fromSpinnerValueGlobalBeforeAdd, false)

                saveAddTransfer()

                navigateAddNewWallet(SPINNER_FROM)
            } else {
                fromSpinnerValueGlobalBeforeAdd = selectedWalletNameFrom
            }
        }
    }

    private fun setToSpinnerListener() {
        binding.toWalletNameSpinner.setOnItemClickListener {
                _, _, _, _ ->

            val selectedWalletNameTo =
                binding.toWalletNameSpinner.text.toString()

            if (selectedWalletNameTo == ADD_NEW_WALLET) {
                binding.fromWalletNameSpinner.setText(toSpinnerValueGlobalBeforeAdd, false)

                saveAddTransfer()

                navigateAddNewWallet(SPINNER_TO)
            } else {
                toSpinnerValueGlobalBeforeAdd = selectedWalletNameTo
            }
        }
    }

    private fun setIfAvailableFromWalletSpinnersValue(walletSpinnerAdapter: SpinnerAdapter) {
        val savedWalletName = args.walletName ?: sharedModViewModel.modelForm?.fromWalletSpinnerValue
        val spinnerTypeArg = args.spinnerType

        if (savedWalletName?.isNotBlank() == true && spinnerTypeArg == SPINNER_FROM && isNameInAdapter(walletSpinnerAdapter, savedWalletName)) {
            fromSpinnerValueGlobalBeforeAdd = savedWalletName

            binding.fromWalletNameSpinner.setText(savedWalletName, false)
        }
    }

    private fun setIfAvailableToWalletSpinnersValue(walletSpinnerAdapter: SpinnerAdapter) {
        val savedWalletName = args.walletName ?: sharedModViewModel.modelForm?.fromWalletSpinnerValue
        val spinnerTypeArg = args.spinnerType

        if (savedWalletName?.isNotBlank() == true && spinnerTypeArg == SPINNER_TO && isNameInAdapter(walletSpinnerAdapter, savedWalletName)) {
            toSpinnerValueGlobalBeforeAdd = savedWalletName

            binding.toWalletNameSpinner.setText(savedWalletName, false)
        }
    }

    private fun isNameInAdapter(subGroupSpinnerAdapter: SpinnerAdapter, savedSubGroupName: String?): Boolean {
        return subGroupSpinnerAdapter.getPosition(savedSubGroupName) > -1
    }


    private fun restoreAmountDateCommentValues() {
        val mod = sharedModViewModel.modelForm

        if (mod?.amount != null) {
            binding.amountEditText.setText(mod.amount)
        }

        if (mod?.date != null) {
            binding.dateEditText.setText(mod.date)
        }

        if (mod?.time != null) {
            binding.timeEditText.setText(mod.time)
        }

        if (mod?.comment != null) {
            binding.commentEditText.setText(mod.comment)
        }
    }

    private fun navigateAddNewWallet(spinnerType: String) {
        val action = AddTransferFragmentDirections.actionAddTransferFragmentToNavigationAddWallet()
        action.source = Constants.ADD_TRANSFER_HISTORY_FRAGMENT
        action.spinnerType = spinnerType
        findNavController().navigate(action)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setDateEditText() {
        val selectedDate = Calendar.getInstance()
        val datePickerListener = DatePickerDialog.OnDateSetListener() {
                _, year, month, dayOfMonth ->
            selectedDate.set(Calendar.YEAR, year)
            selectedDate.set(Calendar.MONTH, month)
            selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            binding.dateEditText.setText(dateFormat.format(selectedDate.time))
        }

        binding.dateEditText.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                datePickerListener,
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.dateEditText.setText(dateFormat.format(selectedDate.time))
    }

    private fun setTimeEditText() {
        val selectedTime = Calendar.getInstance()

        val timePickerListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
            selectedTime.set(Calendar.MINUTE, minute)
            binding.timeEditText.setText(timeFormat.format(selectedTime.time))
        }

        binding.timeEditText.setOnClickListener {
            TimePickerDialog(
                requireContext(),
                timePickerListener,
                selectedTime.get(Calendar.HOUR_OF_DAY),
                selectedTime.get(Calendar.MINUTE),
                false
            ).show()
        }

        binding.timeEditText.setText(timeFormat.format(selectedTime.time))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setButtonOnClickListener() {
        binding.transferButton.setOnClickListener {
            walletViewModel.allWalletsNotArchivedLiveData.observe(viewLifecycleOwner) { wallets ->
                val amountBinding = binding.amountEditText.text.toString().trim()
                val dateBinding = binding.dateEditText.text.toString().trim()
                val timeBinding = binding.timeEditText.text.toString().trim()
                val comment = binding.commentEditText.text.toString().trim()

                val walletFromNameBinding = binding.fromWalletNameSpinner.text.toString()
                val walletToNameBinding = binding.toWalletNameSpinner.text.toString()

                val isEqualSpinnerNamesValidation = IsEqualValidator(walletFromNameBinding, walletToNameBinding).validate()
                binding.fromWalletNameSpinnerLayout.error = if (!isEqualSpinnerNamesValidation.isSuccess) getString(isEqualSpinnerNamesValidation.message) else null
                binding.toWalletNameSpinnerLayout.error = if (!isEqualSpinnerNamesValidation.isSuccess) getString(isEqualSpinnerNamesValidation.message) else null

                val amountValidation = BaseValidator.validate(EmptyValidator(amountBinding), IsDigitValidator(amountBinding))
                binding.amountEditText.error = if (!amountValidation.isSuccess) getString(amountValidation.message) else null

                val dateBindingValidation = EmptyValidator(dateBinding).validate()
                binding.dateLayout.error = if (!dateBindingValidation.isSuccess) getString(dateBindingValidation.message) else null

                val timeBindingValidation = EmptyValidator(timeBinding).validate()
                binding.timeLayout.error = if (!timeBindingValidation.isSuccess) getString(timeBindingValidation.message) else null

                if (isEqualSpinnerNamesValidation.isSuccess
                    && amountValidation.isSuccess
                    && dateBindingValidation.isSuccess
                    && timeBindingValidation.isSuccess
                ) {
                    val walletFrom = wallets.find { it.name == walletFromNameBinding }
                    updateWalletFrom(walletFrom!!, amountBinding.toDouble())

                    val walletTo = wallets.find { it.name == walletToNameBinding }
                    updateWalletTo(walletTo!!, amountBinding.toDouble())

                    val fullDateTime = dateBinding.plus(" ").plus(timeBinding)
                    val parsedLocalDateTime = LocalDateTime.from(dateTimeFormatter.parse(fullDateTime))

                    insertTransferHistoryRecord(comment, walletFrom, walletTo, amountBinding.toDouble(), parsedLocalDateTime)

                    sharedModViewModel.set(null)
                    val action = AddTransferFragmentDirections.actionAddTransferFragmentToNavigationHome()
                    findNavController().navigate(action)
                }
            }
        }
    }

    private fun saveAddTransfer() {
        val amountBinding = binding.amountEditText.text.toString().trim()
        val dateBinding = binding.dateEditText.text.toString().trim()
        val timeBinding = binding.timeEditText.text.toString().trim()
        val commentBinding = binding.commentEditText.text.toString().trim()

        var isSend = true
        if (amountBinding.isNotBlank()) {
            val amountBindingValidation = IsDigitValidator(amountBinding).validate()
            binding.amountEditTextLayout.error =
                if (!amountBindingValidation.isSuccess) getString(amountBindingValidation.message) else null

            if (!amountBindingValidation.isSuccess) {
                isSend = false
            }
        }

        if (isSend) {
            val addTransactionForm = AddTransferForm(
                fromWalletSpinnerValue = fromSpinnerValueGlobalBeforeAdd,
                toWalletSpinnerValue = toSpinnerValueGlobalBeforeAdd,
                amount = amountBinding,
                comment = commentBinding,
                date = dateBinding,
                time = timeBinding
            )
            sharedModViewModel.set(addTransactionForm)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun insertTransferHistoryRecord(comment: String, walletFrom: Wallet, walletTo: Wallet, amount: Double, parsedLocalDateTime: LocalDateTime) {
        val transferHistory = TransferHistory(
            amount = amount,
            fromWalletId = walletFrom.id!!,
            toWalletId = walletTo.id!!,
            date = parsedLocalDateTime,
            comment = comment,
            createdDate = LocalDateTime.now()
        )
        transferHistoryViewModel.insertTransferHistory(transferHistory)
    }

    private fun updateWalletTo(walletTo: Wallet, amount: Double) {
        val updatedWalletToInput = walletTo.input.plus(amount)
        val updatedWalletToBalance = walletTo.balance.plus(amount)

        val updatedWalletTo = Wallet(
            id = walletTo.id,
            name = walletTo.name,
            balance = updatedWalletToBalance,
            input = updatedWalletToInput,
            output = walletTo.output,
            description = walletTo.description,
            archivedDate = walletTo.archivedDate
        )
        walletViewModel.updateWallet(updatedWalletTo)
    }

    private fun updateWalletFrom(walletFrom: Wallet, amount: Double) {
        val updatedWalletFromOutput = walletFrom.output.plus(amount)
        val updatedWalletFromBalance = walletFrom.balance.minus(amount)

        val updatedWalletFrom = Wallet(
            id = walletFrom.id,
            name = walletFrom.name,
            balance = updatedWalletFromBalance,
            input = walletFrom.input,
            output = updatedWalletFromOutput,
            description = walletFrom.description,
            archivedDate = walletFrom.archivedDate
        )

        walletViewModel.updateWallet(updatedWalletFrom)
    }

    private fun dismissAndDropdownSpinner(spinner: AutoCompleteTextView) {
        spinner.dismissDropDown()
        spinner.postDelayed({
            spinner.showDropDown()
        }, 30)
    }

}
