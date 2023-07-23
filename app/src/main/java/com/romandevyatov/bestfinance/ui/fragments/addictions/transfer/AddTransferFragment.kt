package com.romandevyatov.bestfinance.ui.fragments.addictions.transfer

import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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
import com.romandevyatov.bestfinance.db.entities.TransferHistory
import com.romandevyatov.bestfinance.db.entities.Wallet
import com.romandevyatov.bestfinance.ui.adapters.spinnerutils.SpinnerAdapter
import com.romandevyatov.bestfinance.ui.validators.EmptyValidator
import com.romandevyatov.bestfinance.ui.validators.IsDigitValidator
import com.romandevyatov.bestfinance.ui.validators.IsEqualValidator
import com.romandevyatov.bestfinance.ui.validators.base.BaseValidator
import com.romandevyatov.bestfinance.utils.Constants
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.AddTransferViewModel
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.TransferHistoryViewModel
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.WalletViewModel
import com.romandevyatov.bestfinance.viewmodels.shared.SharedViewModel
import com.romandevyatov.bestfinance.viewmodels.shared.models.TransferForm
import dagger.hilt.android.AndroidEntryPoint
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*


// TODO: validate if the same wallets are chosen

@AndroidEntryPoint
class AddTransferFragment : Fragment() {

    private var _binding: FragmentAddTransferBinding? = null
    private val binding get() = _binding!!

    private val walletViewModel: WalletViewModel by viewModels()
    private val transferHistoryViewModel: TransferHistoryViewModel by viewModels()
    private val addTransferViewModel: AddTransferViewModel by viewModels()

    private val args: AddTransferFragmentArgs by navArgs()

    private val sharedViewModel: SharedViewModel<TransferForm> by activityViewModels()

    private var selectedSpinnerFromItemPosition: Int? = null
    private var selectedSpinnerToItemPosition: Int? = null

    private var toWalletSpinnerAdapter: ArrayAdapter<String>? = null
    private var fromWalletSpinnerAdapter: ArrayAdapter<String>? = null

    private val archiveWalletListener =
        object : SpinnerAdapter.DeleteItemClickListener {

            @RequiresApi(Build.VERSION_CODES.O)
            override fun archive(name: String) {
                addTransferViewModel.archiveWallet(name)
            }
        }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback = object : OnBackPressedCallback(
            true
        ) {
            override fun handleOnBackPressed() {
                sharedViewModel.set(null)
                findNavController().navigate(R.id.action_add_new_transfer_fragment_to_navigation_home)
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
        setTransferButtonListener()
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
        walletViewModel.allWalletsNotArchivedLiveData.observe(viewLifecycleOwner) { walletList ->

            val spinnerItems = getWalletItemsForSpinner(walletList)

            val walletSpinnerAdapter = SpinnerAdapter(requireContext(), R.layout.item_with_del, spinnerItems,Constants.ADD_NEW_WALLET, archiveWalletListener)
            fromWalletSpinnerAdapter = walletSpinnerAdapter

            binding.fromWalletNameSpinner.setAdapter(walletSpinnerAdapter)

            restoreTransferForm()

            setSpinnerArgs(walletSpinnerAdapter)
        }
    }

    private fun setToWalletSpinnerAdapter() {
        walletViewModel.allWalletsNotArchivedLiveData.observe(viewLifecycleOwner) { walletList ->

            val spinnerItems = getWalletItemsForSpinner(walletList)

            val walletSpinnerAdapter = SpinnerAdapter(requireContext(), R.layout.item_with_del, spinnerItems, Constants.ADD_NEW_WALLET, archiveWalletListener)
            toWalletSpinnerAdapter = walletSpinnerAdapter

            binding.toWalletNameSpinner.setAdapter(walletSpinnerAdapter)

            restoreTransferForm()

            setSpinnerArgs(walletSpinnerAdapter)
        }
    }

    private fun getWalletItemsForSpinner(walletList: List<Wallet>?): ArrayList<String> {
        val spinnerItems = ArrayList<String>()

        walletList?.forEach { it ->
            spinnerItems.add(it.name)
        }
        spinnerItems.add(Constants.ADD_NEW_WALLET)

        return spinnerItems
    }

    private fun setFromSpinnerListener() {
        binding.fromWalletNameSpinner.setOnItemClickListener {
                parent, view, position, rowId ->
            selectedSpinnerFromItemPosition = position

            val selectedWalletNameFrom =
                binding.fromWalletNameSpinner.text.toString()

            if (selectedWalletNameFrom == Constants.ADD_NEW_WALLET) {
                val amountBinding = binding.amountEditText.text.toString().trim()
                val commentBinding = binding.commentEditText.text.toString().trim()
                val dateEditText = binding.dateEditText.text.toString().trim()

                val transferForm = TransferForm(
                    toWalletSpinnerPosition = selectedSpinnerToItemPosition,
                    amount = amountBinding,
                    comment = commentBinding,
                    date = dateEditText
                )
                sharedViewModel.set(transferForm)

                navigateToAddNewWallet(Constants.SPINNER_FROM)
            }
        }
    }

    private fun setToSpinnerListener() {
        binding.toWalletNameSpinner.setOnItemClickListener {
                parent, view, position, rowId ->
            selectedSpinnerToItemPosition = position

            val selectedWalletNameTo = binding.toWalletNameSpinner.text.toString()

            if (selectedWalletNameTo == Constants.ADD_NEW_WALLET) {
                val amountBinding = binding.amountEditText.text.toString().trim()
                val commentBinding = binding.commentEditText.text.toString().trim()
                val dateEditText = binding.dateEditText.text.toString().trim()

                val transferForm = TransferForm(
                    fromWalletSpinnerPosition = selectedSpinnerFromItemPosition,
                    amount = amountBinding,
                    comment = commentBinding,
                    date = dateEditText
                )
                sharedViewModel.set(transferForm)

                navigateToAddNewWallet(Constants.SPINNER_TO)
            }
        }
    }

    private fun setSpinnerArgs(spinnerAdapter: ArrayAdapter<String>) {
        val addedWalletName = args.walletName
        val spinnerType = args.spinnerType
        if (addedWalletName?.isNotBlank() == true && spinnerType?.isNotBlank() == true) {
            val spinnerPosition = spinnerAdapter.getPosition(args.walletName)
            if (spinnerType ==  Constants.SPINNER_FROM) {
                binding.fromWalletNameSpinner.setText(spinnerAdapter.getItem(spinnerPosition))
            } else if (spinnerType == Constants.SPINNER_TO) {
                binding.toWalletNameSpinner.setText(spinnerAdapter.getItem(spinnerPosition))
            }
        }
    }

    private fun restoreTransferForm() {
        sharedViewModel.modelForm.observe(viewLifecycleOwner) { transferForm ->
            if (transferForm != null) {
                transferForm.fromWalletSpinnerPosition?.let {
                    binding.fromWalletNameSpinner.setText(fromWalletSpinnerAdapter?.getItem(it))
                }
                transferForm.toWalletSpinnerPosition?.let {
                    binding.toWalletNameSpinner.setText(toWalletSpinnerAdapter?.getItem(it))
                }
                binding.amountEditText.setText(transferForm.amount)
                binding.commentEditText.setText(transferForm.comment)
                binding.dateEditText.setText(transferForm.date)
            }
        }
    }

    private fun navigateToAddNewWallet(spinnerType: String) {
        val action = AddTransferFragmentDirections.actionAddNewTransferFragmentToNavigationAddWallet()
        action.source = Constants.ADD_TRANSFER_HISTORY_FRAGMENT
        action.spinnerType = spinnerType
        findNavController().navigate(action)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setDateEditText() {
        val myCalendar = Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener() {
                view, year, month, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH,month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDate(myCalendar)
        }

        val dateET = binding.dateEditText
        dateET.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                datePicker,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        updateDate(myCalendar)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setTransferButtonListener() {
        binding.transferButton.setOnClickListener {
            sharedViewModel.set(null)
            walletViewModel.allWalletsNotArchivedLiveData.observe(viewLifecycleOwner) { wallets ->
                val amountBinding = binding.amountEditText.text.toString().trim()
                val dateBinding = binding.dateEditText.text.toString().trim()

                val walletFromNameBinding = binding.fromWalletNameSpinner.text.toString()
                val walletToNameBinding = binding.toWalletNameSpinner.text.toString()

                val isEqualSpinnerNamesValidation = IsEqualValidator(walletFromNameBinding, walletFromNameBinding).validate()
                binding.fromWalletNameSpinnerLayout.error = if (!isEqualSpinnerNamesValidation.isSuccess) getString(isEqualSpinnerNamesValidation.message) else null
                binding.toWalletNameSpinnerLayout.error = if (!isEqualSpinnerNamesValidation.isSuccess) getString(isEqualSpinnerNamesValidation.message) else null

                val amountValidation = BaseValidator.validate(EmptyValidator(amountBinding), IsDigitValidator(amountBinding))
                binding.amountEditText.error = if (!amountValidation.isSuccess) getString(amountValidation.message) else null

                val dateValidation = EmptyValidator(dateBinding).validate()
                binding.dateLayout.error = if (!dateValidation.isSuccess) getString(dateValidation.message) else null

                if (isEqualSpinnerNamesValidation.isSuccess
                    && amountValidation.isSuccess
                    && dateValidation.isSuccess
                ) {
                    val walletFrom = wallets.find { it.name == walletFromNameBinding }
                    updateWalletFrom(walletFrom!!, amountBinding.toDouble())

                    val walletTo = wallets.find { it.name == walletToNameBinding }
                    updateWalletTo(walletTo!!, amountBinding.toDouble())

                    val comment = binding.commentEditText.text.toString().trim()
                    insertTransferHistoryRecord(comment, walletFrom, walletTo, amountBinding.toDouble())

                    navigateToHome()
                }
            }


        }
    }

    private fun navigateToHome() {
        val action = AddTransferFragmentDirections.actionAddNewTransferFragmentToNavigationHome()
        findNavController().navigate(action)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun insertTransferHistoryRecord(
        comment: String,
        walletFrom: Wallet,
        walletTo: Wallet,
        amount: Double
    ) {
        val transferHistory = TransferHistory(
            amount = amount,
            fromWalletId = walletFrom.id!!,
            toWalletId = walletTo.id!!,
            comment = comment,
            createdDate = OffsetDateTime.now()
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

    private fun updateWalletFrom(
        walletFrom: Wallet,
        amount: Double) {
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateDate(calendar: Calendar) {
//        val dateFormat =  //"yyyy-MM-dd HH:mm:ss"
//        val sdf = SimpleDateFormat(dateFormat, Locale.US)
//        binding.dateEditText.setText(sdf.format(calendar.time))
        val iso8601DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        binding.dateEditText.setText(OffsetDateTime.now().format(iso8601DateTimeFormatter))
    }

}
