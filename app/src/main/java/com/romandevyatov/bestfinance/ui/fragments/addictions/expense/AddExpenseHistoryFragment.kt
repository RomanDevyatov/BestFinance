package com.romandevyatov.bestfinance.ui.fragments.addictions.expense

import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.FragmentAddExpenseHistoryBinding
import com.romandevyatov.bestfinance.db.entities.ExpenseGroup
import com.romandevyatov.bestfinance.db.entities.Wallet
import com.romandevyatov.bestfinance.db.entities.relations.ExpenseGroupWithExpenseSubGroups
import com.romandevyatov.bestfinance.ui.adapters.spinnerutils.SpinnerAdapter
import com.romandevyatov.bestfinance.ui.validators.EmptyValidator
import com.romandevyatov.bestfinance.ui.validators.IsDigitValidator
import com.romandevyatov.bestfinance.ui.validators.base.BaseValidator
import com.romandevyatov.bestfinance.utils.Constants
import com.romandevyatov.bestfinance.viewmodels.*
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.AddExpenseHistoryViewModel
import com.romandevyatov.bestfinance.viewmodels.shared.SharedViewModel
import com.romandevyatov.bestfinance.viewmodels.shared.models.AddTransactionForm
import dagger.hilt.android.AndroidEntryPoint
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@AndroidEntryPoint
class AddExpenseHistoryFragment : Fragment() {


    private var _binding: FragmentAddExpenseHistoryBinding? = null
    private val binding get() = _binding!!

    private val addExpenseHistoryViewModel: AddExpenseHistoryViewModel by viewModels()

    private val sharedViewModel: SharedViewModel<AddTransactionForm> by activityViewModels()

    private var expenseGroupSpinnerPosition = 0
    private var expenseSubGroupSpinnerPosition = 0
    private var walletSpinnerPosition = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddExpenseHistoryBinding.inflate(inflater, container, false)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initExpenseGroupAndSubGroupSpinners()
        initWalletSpinner()

        val myCalendar = Calendar.getInstance()

        val datePicker = DatePickerDialog.OnDateSetListener() { view, year, month, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
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

        binding.addExpenseHistoryButton.setOnClickListener {
            val expenseSubGroupNameBinding = binding.expenseSubGroupSpinner.text.toString()
            val amountBinding = binding.amountEditText.text.toString()
            val commentBinding = binding.commentEditText.text.toString()
            val dateBinding = binding.dateEditText.text.toString()
            val walletNameBinding = binding.walletSpinner.text.toString()

            val expenseSubGroupNameBindingValidation = EmptyValidator(expenseSubGroupNameBinding).validate()
            binding.expenseSubGroupSpinnerLayout.error = if (!expenseSubGroupNameBindingValidation.isSuccess) getString(expenseSubGroupNameBindingValidation.message) else null

            val amountBindingValidation = BaseValidator.validate(EmptyValidator(amountBinding), IsDigitValidator(amountBinding))
            binding.amountTextInputLayout.error = if (!amountBindingValidation.isSuccess) getString(amountBindingValidation.message) else null

            val walletNameBindingValidation = EmptyValidator(walletNameBinding).validate()
            binding.walletSpinnerLayout.error = if (!walletNameBindingValidation.isSuccess) getString(walletNameBindingValidation.message) else null

            if (expenseSubGroupNameBindingValidation.isSuccess
                && amountBindingValidation.isSuccess
                && walletNameBindingValidation.isSuccess) {
                addExpenseHistoryViewModel.addExpenseHistory(
                    expenseSubGroupNameBinding,
                    amountBinding.toDouble(),
                    commentBinding,
                    dateBinding,
                    walletNameBinding
                )

                sharedViewModel.set(null)
                findNavController().navigate(R.id.action_navigation_add_expense_to_navigation_home)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback = object : OnBackPressedCallback(
            true
        ) {
            override fun handleOnBackPressed() {
                sharedViewModel.set(null)
                findNavController().navigate(R.id.action_navigation_add_expense_to_navigation_home)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    private val args: AddExpenseHistoryFragmentArgs by navArgs()

    private fun initExpenseGroupAndSubGroupSpinners() {
        var subGroupItems = ArrayList<String>()
        subGroupItems.add(Constants.EXPENSE_SUB_GROUP)

        val archiveExpenseSubGroupListener =
            object : SpinnerAdapter.DeleteItemClickListener {

                @RequiresApi(Build.VERSION_CODES.O)
                override fun archive(name: String) {
                    addExpenseHistoryViewModel.archiveExpenseSubGroup(name)
                }
            }

        var expenseSubGroupSpinnerAdapter = SpinnerAdapter(requireContext(), R.layout.item_with_del, subGroupItems, Constants.ADD_NEW_INCOME_GROUP, archiveExpenseSubGroupListener)

        addExpenseHistoryViewModel.getAllExpenseGroupNotArchivedLiveData().observe(viewLifecycleOwner) { expenseGroupList ->
            val spinnerItems = getIncomeGroupItemsForSpinner(expenseGroupList)

            val archiveExpenseGroupListener =
                object : SpinnerAdapter.DeleteItemClickListener {

                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun archive(name: String) {
                        addExpenseHistoryViewModel.archiveExpenseGroup(name)
                    }
                }

            val expenseGroupSpinnerAdapter = SpinnerAdapter(requireContext(), R.layout.item_with_del, spinnerItems, Constants.ADD_NEW_EXPENSE_GROUP, archiveExpenseGroupListener)
            binding.expenseGroupSpinner.setAdapter(expenseGroupSpinnerAdapter)

            restoreAddingExpenseForm()

            if (args.expenseGroupName != null && args.expenseGroupName!!.isNotBlank()) {
                val spinnerPosition = expenseGroupSpinnerAdapter.getPosition(args.expenseGroupName)

                binding.expenseGroupSpinner.setSelection(spinnerPosition)
            }

            if (args.expenseSubGroupName != null && args.expenseSubGroupName!!.isNotBlank()) {
                val selectedExpenseGroupName =
                    binding.expenseGroupSpinner.text.toString()
                addExpenseHistoryViewModel.getExpenseGroupNotArchivedWithExpenseSubGroupsNotArchivedByExpenseGroupNameLiveData(selectedExpenseGroupName)
                    .observe(viewLifecycleOwner) { incomeGroupWithIncomeSubGroups ->
                        subGroupItems = getSpinnerSubItems(incomeGroupWithIncomeSubGroups)
                        expenseSubGroupSpinnerAdapter = SpinnerAdapter(requireContext(), R.layout.item_with_del, subGroupItems, Constants.ADD_NEW_EXPENSE_SUB_GROUP, archiveExpenseSubGroupListener)

                        val spinnerPosition = expenseSubGroupSpinnerAdapter.getPosition(args.expenseSubGroupName)

                        binding.expenseSubGroupSpinner.setAdapter(expenseSubGroupSpinnerAdapter)
                        binding.expenseSubGroupSpinner.setText(expenseSubGroupSpinnerAdapter.getItem(spinnerPosition))
                    }
            }

            binding.expenseGroupSpinner.setOnItemClickListener {
                    parent, view, position, rowId ->
                    expenseGroupSpinnerPosition = position

                    binding.expenseSubGroupSpinner.isVisible = true

                    val selectedExpenseGroupName =
                        binding.expenseGroupSpinner.text.toString()

                    if (selectedExpenseGroupName == Constants.ADD_NEW_EXPENSE_GROUP) {
                        setAddExpenseFormBeforeAddingExpenseGroup()

                        val action =
                            AddExpenseHistoryFragmentDirections.actionNavigationAddExpenseToNavigationAddNewExpenseGroup()
                        findNavController().navigate(action)
                    }
                    // TODO: doesn't work as expected
                    addExpenseHistoryViewModel.getExpenseGroupNotArchivedWithExpenseSubGroupsNotArchivedByExpenseGroupNameLiveData(selectedExpenseGroupName)
                        .observe(viewLifecycleOwner) { expenseGroupWithExpenseSubGroups ->
                            subGroupItems = getSpinnerSubItems(expenseGroupWithExpenseSubGroups)
                            expenseSubGroupSpinnerAdapter = SpinnerAdapter(requireContext(), R.layout.item_with_del, subGroupItems, Constants.ADD_NEW_EXPENSE_SUB_GROUP, archiveExpenseSubGroupListener)
                            binding.expenseSubGroupSpinner.setAdapter(expenseSubGroupSpinnerAdapter)

                            if (args.expenseSubGroupName != null && args.expenseSubGroupName!!.isNotBlank()) {
                                val spinnerPosition = expenseSubGroupSpinnerAdapter.getPosition(args.expenseSubGroupName)
                                binding.expenseSubGroupSpinner.setSelection(spinnerPosition)
                            }
                        }
            }
        }

        binding.expenseSubGroupSpinner.setAdapter(expenseSubGroupSpinnerAdapter)
        binding.expenseSubGroupSpinner.setOnItemClickListener {
                parent, view, position, rowId ->
                expenseSubGroupSpinnerPosition = position

                val selectedExpenseSubGroupName = binding.expenseSubGroupSpinner.text.toString()

                if (selectedExpenseSubGroupName == Constants.ADD_NEW_EXPENSE_SUB_GROUP) {
                    setAddExpenseFormBeforeAddingExpenseSubGroup()

                    val action = AddExpenseHistoryFragmentDirections.actionNavigationAddExpenseToNavigationAddNewExpenseSubGroup()

                    val selectedExpenseGroupName = binding.expenseGroupSpinner.text.toString()
                    action.expenseGroupName = selectedExpenseGroupName
                    findNavController().navigate(action)
                }
            }
        }

    private fun getIncomeGroupItemsForSpinner(expenseGroupList: List<ExpenseGroup>?): ArrayList<String> {
        val spinnerItems = ArrayList<String>()
        spinnerItems.add(Constants.EXPENSE_GROUP)
        expenseGroupList?.forEach { it ->
            spinnerItems.add(it.name)
        }
        spinnerItems.add(Constants.ADD_NEW_EXPENSE_GROUP)

        return spinnerItems
    }

    private fun getSpinnerSubItems(expenseGroupWithExpenseSubGroups: ExpenseGroupWithExpenseSubGroups?): ArrayList<String> {
        val spinnerSubItems = ArrayList<String>()
        spinnerSubItems.add(Constants.EXPENSE_SUB_GROUP)

        expenseGroupWithExpenseSubGroups?.expenseSubGroups?.forEach {
            if (it.archivedDate == null) {
                spinnerSubItems.add(it.name)
            }
        }
        spinnerSubItems.add(Constants.ADD_NEW_EXPENSE_SUB_GROUP)

        return spinnerSubItems
    }

    private fun initWalletSpinner() {
        addExpenseHistoryViewModel.walletsNotArchivedLiveData.observe(viewLifecycleOwner) { walletList ->

            val spinnerWalletItems = getWalletItemsForSpinner(walletList)

            val archiveWalletListener =
                object : SpinnerAdapter.DeleteItemClickListener {

                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun archive(name: String) {
                        addExpenseHistoryViewModel.archiveWallet(name)
                    }
                }

            val walletSpinnerAdapter = SpinnerAdapter(requireContext(), R.layout.item_with_del, spinnerWalletItems, Constants.ADD_NEW_WALLET, archiveWalletListener)

            binding.walletSpinner.setAdapter(walletSpinnerAdapter)

            if (args.walletName != null && args.walletName!!.isNotBlank()) {
                val spinnerPosition = walletSpinnerAdapter.getPosition(args.walletName)

                binding.walletSpinner.setText(walletSpinnerAdapter.getItem(spinnerPosition))
            }
        }

        binding.walletSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                walletSpinnerPosition = position

                val selectedExpenseSubGroupName = binding.walletSpinner.text.toString()

                if (selectedExpenseSubGroupName == Constants.ADD_NEW_WALLET) {
                    setAddIncomeFormBeforeAddingWallet()

                    val action = AddExpenseHistoryFragmentDirections.actionNavigationAddExpenseToNavigationAddWallet()
                    action.source = Constants.ADD_INCOME_HISTORY_FRAGMENT
                    findNavController().navigate(action)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

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

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateDate(calendar: Calendar) {
//        val dateFormat = "yyyy-MM-dd HH:mm:ss"
//        val sdf = SimpleDateFormat(dateFormat, Locale.US)
//        binding.dateEditText.setText(sdf.format(calendar.time))
        val iso8601DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        binding.dateEditText.setText(OffsetDateTime.now().format(iso8601DateTimeFormatter))
    }

    private fun setAddExpenseFormBeforeAddingExpenseGroup() {
        val amountBinding = binding.amountEditText.text.toString().trim()
        val dateEditText = binding.dateEditText.text.toString().trim()
        val commentBinding = binding.commentEditText.text.toString().trim()

        val addTransactionForm = AddTransactionForm(
            walletSpinnerPosition = walletSpinnerPosition,
            amount = amountBinding,
            date = dateEditText,
            comment = commentBinding
        )
        sharedViewModel.set(addTransactionForm)
    }

    private fun setAddExpenseFormBeforeAddingExpenseSubGroup() {
        val amountBinding = binding.amountEditText.text.toString().trim()
        val dateEditText = binding.dateEditText.text.toString().trim()
        val commentBinding = binding.commentEditText.text.toString().trim()

        val addTransactionForm = AddTransactionForm(
            groupSpinnerPosition = expenseGroupSpinnerPosition,
            walletSpinnerPosition = walletSpinnerPosition,
            amount = amountBinding,
            date = dateEditText,
            comment = commentBinding
        )
        sharedViewModel.set(addTransactionForm)
    }

    private fun setAddIncomeFormBeforeAddingWallet() {
        val amountBinding = binding.amountEditText.text.toString().trim()
        val commentBinding = binding.commentEditText.text.toString().trim()
        val dateEditText = binding.dateEditText.text.toString().trim()

        val addTransactionForm = AddTransactionForm(
            groupSpinnerPosition = expenseGroupSpinnerPosition,
            subGroupSpinnerPosition = expenseSubGroupSpinnerPosition,
            amount = amountBinding,
            comment = commentBinding,
            date = dateEditText
        )
        sharedViewModel.set(addTransactionForm)
    }

    private fun restoreAddingExpenseForm() {
        sharedViewModel.modelForm.observe(viewLifecycleOwner) { transferForm ->
            if (transferForm != null) {
                binding.expenseGroupSpinner.setSelection(transferForm.groupSpinnerPosition)
                binding.expenseSubGroupSpinner.setSelection(transferForm.subGroupSpinnerPosition)
                binding.walletSpinner.setSelection(transferForm.walletSpinnerPosition)
                binding.amountEditText.setText(transferForm.amount)
                binding.dateEditText.setText(transferForm.date)
                binding.commentEditText.setText(transferForm.comment)
            }
        }
    }

}
