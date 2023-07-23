package com.romandevyatov.bestfinance.ui.fragments.addictions.expense

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
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
import com.romandevyatov.bestfinance.ui.fragments.addictions.expense.AddExpenseHistoryFragmentArgs
import com.romandevyatov.bestfinance.ui.fragments.addictions.expense.AddExpenseHistoryFragmentDirections
import com.romandevyatov.bestfinance.ui.validators.EmptyValidator
import com.romandevyatov.bestfinance.ui.validators.IsDigitValidator
import com.romandevyatov.bestfinance.ui.validators.base.BaseValidator
import com.romandevyatov.bestfinance.utils.Constants
import com.romandevyatov.bestfinance.viewmodels.*
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.AddExpenseHistoryViewModel
import com.romandevyatov.bestfinance.viewmodels.shared.SharedModifiedViewModel
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

    private val sharedModViewModel: SharedModifiedViewModel<AddTransactionForm> by activityViewModels()

    private var groupSpinnerPositionGlobal: Int? = null
    private var subGroupSpinnerPositionGlobal: Int? = null
    private var walletSpinnerPositionGlobal: Int? = null

    private var subGroupSpinnerAdapterGlobal: SpinnerAdapter? = null
    private var groupSpinnerAdapterGlobal: SpinnerAdapter? = null
    private var walletSpinnerAdapterGlobal: SpinnerAdapter? = null

    private val args: AddExpenseHistoryFragmentArgs by navArgs()

    private val archiveExpenseGroupListener =
        object : SpinnerAdapter.DeleteItemClickListener {

            @RequiresApi(Build.VERSION_CODES.O)
            override fun archive(name: String) {
                addExpenseHistoryViewModel.archiveExpenseGroup(name)
            }
        }

    private val archiveExpenseSubGroupListener =
        object : SpinnerAdapter.DeleteItemClickListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun archive(name: String) {
                addExpenseHistoryViewModel.archiveExpenseSubGroup(name)
            }
        }

    private val archiveWalletListener =
        object : SpinnerAdapter.DeleteItemClickListener {

            @RequiresApi(Build.VERSION_CODES.O)
            override fun archive(name: String) {
                addExpenseHistoryViewModel.archiveWallet(name)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddExpenseHistoryBinding.inflate(inflater, container, false)
//        markButtonDisable(binding.addExpenseHistoryButton)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSpinners()

        setDateEditText()

        setButtonOnClickListener()
    }

    private fun setSpinners() {
        setGroupAndSubGroupSpinnerAdapter()
        setExpenseGroupSpinnerOnClickListener()
        setExpenseSubGroupSpinnerListener()

        setWalletSpinnerAdapter()
        setWalletSpinnerOnItemClickListener()
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
                sharedModViewModel.set(null)
                findNavController().navigate(R.id.action_navigation_add_expense_to_navigation_home)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setButtonOnClickListener() {
        binding.addExpenseHistoryButton.setOnClickListener {
            val expenseSubGroupNameBinding = binding.expenseSubGroupSpinner.text.toString()
            val amountBinding = binding.amountEditText.text.toString()
            val commentBinding = binding.commentEditText.text.toString()
            val dateBinding = binding.dateEditText.text.toString()
            val walletNameBinding = binding.walletSpinner.text.toString()

            val expenseSubGroupNameBindingValidation = EmptyValidator(expenseSubGroupNameBinding).validate()
            binding.expenseSubGroupSpinnerLayout.error = if (!expenseSubGroupNameBindingValidation.isSuccess) getString(expenseSubGroupNameBindingValidation.message) else null

            val amountBindingValidation = EmptyValidator(amountBinding).validate()
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

                sharedModViewModel.set(null)
                findNavController().navigate(R.id.action_navigation_add_expense_to_navigation_home)
            }
        }
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

        restoreAmountDateCommentValues()
    }

    private fun getExpenseGroupItemsForSpinner(expenseGroupList: List<ExpenseGroup>?): ArrayList<String> {
        val spinnerItems = ArrayList<String>()

        expenseGroupList?.forEach { it ->
            spinnerItems.add(it.name)
        }
        spinnerItems.add(Constants.ADD_NEW_INCOME_GROUP)

        return spinnerItems
    }

    private fun setGroupAndSubGroupSpinnerAdapter() {
        addExpenseHistoryViewModel.getAllExpenseGroupNotArchivedLiveData().observe(viewLifecycleOwner) { expenseGroups ->
            val spinnerGroupItems = getExpenseGroupItemsForSpinner(expenseGroups)

            val groupSpinnerAdapter = SpinnerAdapter(requireContext(), R.layout.item_with_del, spinnerGroupItems, Constants.ADD_NEW_INCOME_GROUP, archiveExpenseGroupListener)
            groupSpinnerAdapterGlobal = groupSpinnerAdapter

            binding.expenseGroupSpinner.setAdapter(groupSpinnerAdapter)

            setIfAvailableGroupSpinnersValue(groupSpinnerAdapter)

            setSubGroupSpinnerAdapter()
        }
    }

    private fun setSubGroupSpinnerAdapter() {
        val subGroupSpinnerAdapter = getEmptySubGroupSpinnerAdapter()

        binding.expenseSubGroupSpinner.setAdapter(subGroupSpinnerAdapter)

        setIfAvailableSubGroupSpinnersValue(subGroupSpinnerAdapter)
    }

    private fun getEmptySubGroupSpinnerAdapter(): SpinnerAdapter {
        val subGroupSpinnerItems = ArrayList<String>()
        subGroupSpinnerItems.add(Constants.ADD_NEW_INCOME_SUB_GROUP)

        val subGroupAdapter = SpinnerAdapter(requireContext(), R.layout.item_with_del, subGroupSpinnerItems, Constants.ADD_NEW_INCOME_GROUP, archiveExpenseSubGroupListener)
        subGroupSpinnerAdapterGlobal = subGroupAdapter

        return subGroupAdapter
    }

    private fun setExpenseGroupSpinnerOnClickListener() {
        binding.expenseGroupSpinner.setOnItemClickListener {
                parent, view, position, rowId ->

            resetSubGroupSpinner()

            groupSpinnerPositionGlobal = position

            val selectedGroupName =
                binding.expenseGroupSpinner.text.toString()

            if (selectedGroupName == Constants.ADD_NEW_INCOME_GROUP) {
                setAddExpenseFormBeforeAddExpenseGroup()

                val action =
                    AddExpenseHistoryFragmentDirections.actionNavigationAddExpenseToNavigationAddNewExpenseGroup()
                findNavController().navigate(action)
            }

            // TODO: getExpenseGroupNotArchivedWithExpenseSubGroupsNotArchivedByExpenseGroupNameLiveData doesn't work
            addExpenseHistoryViewModel.getExpenseGroupNotArchivedWithExpenseSubGroupsNotArchivedByExpenseGroupNameLiveData(selectedGroupName)
                .observe(viewLifecycleOwner) { expenseGroupWithExpenseSubGroups ->
                    val spinnerSubItems = getSpinnerSubItemsNotArchived(expenseGroupWithExpenseSubGroups)
                    val adapter = SpinnerAdapter(requireContext(), R.layout.item_with_del, spinnerSubItems, Constants.ADD_NEW_INCOME_SUB_GROUP, archiveExpenseSubGroupListener)

                    subGroupSpinnerAdapterGlobal = adapter
                    binding.expenseSubGroupSpinner.setAdapter(adapter)
                }
        }
    }

    private fun setExpenseSubGroupSpinnerListener() {
        binding.expenseSubGroupSpinner.setOnItemClickListener {
                parent, view, position, rowId ->
            subGroupSpinnerPositionGlobal = position
            val selectedExpenseSubGroupName = binding.expenseSubGroupSpinner.text.toString()

            if (selectedExpenseSubGroupName == Constants.ADD_NEW_INCOME_SUB_GROUP) {
                setAddExpenseFormBeforeAddingExpenseSubGroup()

                val action = AddExpenseHistoryFragmentDirections.actionNavigationAddExpenseToNavigationAddNewExpenseSubGroup()
                action.expenseGroupName = binding.expenseGroupSpinner.text.toString()
                findNavController().navigate(action)
            }
        }
    }

    private fun setIfAvailableGroupSpinnersValue(
        groupSpinnerAdapter: SpinnerAdapter
    ) {
        val groupNameArg = args.expenseGroupName
        if (groupNameArg != null && groupNameArg.isNotBlank()) {
            val spinnerPosition = groupSpinnerAdapter.getPosition(groupNameArg)

            val groupName = groupSpinnerAdapter.getItem(spinnerPosition)

            binding.expenseGroupSpinner.setText(groupName)
        } else {
            restoreGroupSpinnerValue(groupSpinnerAdapter)
        }
    }

    private fun restoreGroupSpinnerValue( // need to save before moving to retrieve value here
        groupSpinnerAdapter: SpinnerAdapter?
    ) {
        val mod = sharedModViewModel.modelForm
        binding.expenseGroupSpinner.setText(mod?.groupSpinnerPosition?.let {
            groupSpinnerAdapter?.getItem(
                it
            )
        })
    }

    private fun setIfAvailableSubGroupSpinnersValue(subGroupSpinnerAdapter: SpinnerAdapter?) {
        val groupName = args.expenseGroupName
        val subGroupName = args.expenseSubGroupName

        if (groupName != null
            && groupName.isNotBlank()
            && subGroupName != null
            && subGroupName.isNotBlank()) {
            addExpenseHistoryViewModel.getExpenseGroupNotArchivedWithExpenseSubGroupsNotArchivedByExpenseGroupNameLiveData(groupName)
                .observe(viewLifecycleOwner) { expenseGroupWithExpenseSubGroups ->
                    val spinnerSubItems = getSpinnerSubItemsNotArchived(expenseGroupWithExpenseSubGroups)

                    val subGroupAdapter = SpinnerAdapter(requireContext(), R.layout.item_with_del, spinnerSubItems, Constants.ADD_NEW_INCOME_SUB_GROUP, archiveExpenseSubGroupListener)
                    subGroupSpinnerAdapterGlobal = subGroupAdapter

                    val subGroupSpinnerPosition = subGroupAdapter.getPosition(subGroupName)
                    subGroupSpinnerPositionGlobal = subGroupSpinnerPosition

                    binding.expenseSubGroupSpinner.setAdapter(subGroupAdapter)
                    binding.expenseSubGroupSpinner.setText(subGroupAdapter.getItem(subGroupSpinnerPosition))
                }
        } else {
            restoreSubGroupSpinnerValue(subGroupSpinnerAdapter)
        }
    }

    private fun restoreSubGroupSpinnerValue(subGroupSpinnerAdapter: SpinnerAdapter?) {
        val mod = sharedModViewModel.modelForm
        binding.expenseSubGroupSpinner.setText(mod?.subGroupSpinnerPosition?.let {
            subGroupSpinnerAdapter?.getItem(
                it
            )
        })
    }

    private fun setIfAvailableWalletSpinnersValue(walletSpinnerAdapter: SpinnerAdapter) {
        if (args.walletName != null && args.walletName!!.isNotBlank()) {
            val spinnerPosition = walletSpinnerAdapter.getPosition(args.walletName)

            val walletName = walletSpinnerAdapter.getItem(spinnerPosition)

            binding.walletSpinner.setText(walletName)
        } else {
            restoreWalletSpinnerValue(walletSpinnerAdapter)
        }
    }

    private fun restoreWalletSpinnerValue(
        walletSpinnerAdapter: SpinnerAdapter?
    ) {
        val mod = sharedModViewModel.modelForm
        binding.walletSpinner.setText(mod?.walletSpinnerPosition?.let {
            walletSpinnerAdapter?.getItem(
                it
            )
        })
    }

    private fun restoreAmountDateCommentValues() {
        val mod = sharedModViewModel.modelForm
        if (mod?.amount != null) {
            binding.amountEditText.setText(mod.amount)
        }

        if (mod?.date != null) {
            binding.dateEditText.setText(mod.date)
        }

        if (mod?.comment != null) {
            binding.commentEditText.setText(mod.comment)
        }
    }

    private fun getSpinnerSubItemsNotArchived(expenseGroupWithExpenseSubGroups: ExpenseGroupWithExpenseSubGroups?): ArrayList<String> {
        val spinnerSubItems = ArrayList<String>()

        expenseGroupWithExpenseSubGroups?.expenseSubGroups?.forEach {
            if (it.archivedDate == null) {
                spinnerSubItems.add(it.name)
            }
        }

        spinnerSubItems.add(Constants.ADD_NEW_INCOME_SUB_GROUP)

        return spinnerSubItems
    }

    private fun setWalletSpinnerAdapter() {
        addExpenseHistoryViewModel.walletsNotArchivedLiveData.observe(viewLifecycleOwner) { walletList ->

            val spinnerWalletItems = getWalletItemsForSpinner(walletList)

            val walletSpinnerAdapter = SpinnerAdapter(requireContext(), R.layout.item_with_del, spinnerWalletItems, Constants.ADD_NEW_WALLET, archiveWalletListener)
            walletSpinnerAdapterGlobal = walletSpinnerAdapter

            binding.walletSpinner.setAdapter(walletSpinnerAdapter)

            setIfAvailableWalletSpinnersValue(walletSpinnerAdapter)
        }
    }

    private fun setWalletSpinnerOnItemClickListener() {
        binding.walletSpinner.setOnItemClickListener {
                parent, view, position, rowId ->

            walletSpinnerPositionGlobal = position

            val selectedWalletName = binding.walletSpinner.text.toString()

            if (selectedWalletName == Constants.ADD_NEW_WALLET) {
                setAddExpenseFormBeforeAddingWallet()

                val action = AddExpenseHistoryFragmentDirections.actionNavigationAddExpenseToNavigationAddWallet()
                action.source = Constants.ADD_INCOME_HISTORY_FRAGMENT
                findNavController().navigate(action)
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
//        val dateFormat =  //"yyyy-MM-dd HH:mm:ss"
//        val sdf = SimpleDateFormat(dateFormat, Locale.US)
//        binding.dateEditText.setText(sdf.format(calendar.time))
        val iso8601DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        binding.dateEditText.setText(OffsetDateTime.now().format(iso8601DateTimeFormatter))
    }

    private fun setAddExpenseFormBeforeAddExpenseGroup() {
        val amountBinding = binding.amountEditText.text.toString().trim()
        val dateEditText = binding.dateEditText.text.toString().trim()
        val commentBinding = binding.commentEditText.text.toString().trim()

        val addTransactionForm = AddTransactionForm(
            walletSpinnerPosition = walletSpinnerPositionGlobal,
            amount = amountBinding,
            date = dateEditText,
            comment = commentBinding
        )
        sharedModViewModel.set(addTransactionForm)
    }

    private fun setAddExpenseFormBeforeAddingExpenseSubGroup() {
        val amountBinding = binding.amountEditText.text.toString().trim()
        val dateEditText = binding.dateEditText.text.toString().trim()
        val commentBinding = binding.commentEditText.text.toString().trim()

        val addTransactionForm = AddTransactionForm(
            groupSpinnerPosition = groupSpinnerPositionGlobal,
            walletSpinnerPosition = walletSpinnerPositionGlobal,
            amount = amountBinding,
            date = dateEditText,
            comment = commentBinding
        )
        sharedModViewModel.set(addTransactionForm)
    }

    private fun setAddExpenseFormBeforeAddingWallet() {
        val amountBinding = binding.amountEditText.text.toString().trim()
        val commentBinding = binding.commentEditText.text.toString().trim()
        val dateEditText = binding.dateEditText.text.toString().trim()

        val addTransactionForm = AddTransactionForm(
            groupSpinnerPosition = groupSpinnerPositionGlobal,
            subGroupSpinnerPosition = subGroupSpinnerPositionGlobal,
            amount = amountBinding,
            comment = commentBinding,
            date = dateEditText
        )
        sharedModViewModel.set(addTransactionForm)
    }

    private fun resetSubGroupSpinner() {
        subGroupSpinnerPositionGlobal = null
        binding.expenseSubGroupSpinner.isVisible = true
        binding.expenseSubGroupSpinner.text = null
    }

}