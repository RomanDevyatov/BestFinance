package com.romandevyatov.bestfinance.ui.fragments.addictions.expense

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
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
import com.romandevyatov.bestfinance.db.roomdb.converters.LocalDateTimeRoomTypeConverter.Companion.dateFormat
import com.romandevyatov.bestfinance.db.roomdb.converters.LocalDateTimeRoomTypeConverter.Companion.dateTimeFormatter
import com.romandevyatov.bestfinance.db.roomdb.converters.LocalDateTimeRoomTypeConverter.Companion.timeFormat
import com.romandevyatov.bestfinance.ui.adapters.spinnerutils.SpinnerAdapter
import com.romandevyatov.bestfinance.ui.validators.EmptyValidator
import com.romandevyatov.bestfinance.utils.Constants.ADD_EXPENSE_HISTORY_FRAGMENT
import com.romandevyatov.bestfinance.utils.Constants.ADD_NEW_EXPENSE_GROUP
import com.romandevyatov.bestfinance.utils.Constants.ADD_NEW_EXPENSE_SUB_GROUP
import com.romandevyatov.bestfinance.utils.Constants.ADD_NEW_WALLET
import com.romandevyatov.bestfinance.viewmodels.*
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.AddExpenseHistoryViewModel
import com.romandevyatov.bestfinance.viewmodels.shared.SharedModifiedViewModel
import com.romandevyatov.bestfinance.viewmodels.shared.models.AddTransactionForm
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class AddExpenseHistoryFragment : Fragment() {

    private var _binding: FragmentAddExpenseHistoryBinding? = null
    private val binding get() = _binding!!

    private val addHistoryViewModel: AddExpenseHistoryViewModel by viewModels()

    private val sharedModViewModel: SharedModifiedViewModel<AddTransactionForm> by activityViewModels()

    private var groupSpinnerPositionGlobalBeforeAdd: Int? = -1
    private var subGroupSpinnerPositionGlobalBeforeAdd: Int? = -1
    private var walletSpinnerPositionGlobalBeforeAdd: Int? = -1

    private val args: AddExpenseHistoryFragmentArgs by navArgs()

    private val archiveGroupListener =
        object : SpinnerAdapter.DeleteItemClickListener {

            @RequiresApi(Build.VERSION_CODES.O)
            override fun archive(name: String) {
                addHistoryViewModel.archiveExpenseGroup(name)
                if (binding.groupSpinner.text.toString() == name) {
                    binding.groupSpinner.text = null
                    groupSpinnerPositionGlobalBeforeAdd = -1
                    resetSubGroupSpinner()
                }
                sharedModViewModel.set(null)

                dismissAndDropdownSpinner(binding.groupSpinner)
            }
        }

    private val archiveSubGroupListener =
        object : SpinnerAdapter.DeleteItemClickListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun archive(name: String) {
                addHistoryViewModel.archiveExpenseGroup(name)
                if (binding.subGroupSpinner.text.toString() == name) {
                    binding.subGroupSpinner.text = null
                    subGroupSpinnerPositionGlobalBeforeAdd = -1
                }
                sharedModViewModel.set(null)

                dismissAndDropdownSpinner(binding.subGroupSpinner)
            }
        }

    private val archiveWalletListener =
        object : SpinnerAdapter.DeleteItemClickListener {

            @RequiresApi(Build.VERSION_CODES.O)
            override fun archive(name: String) {
                addHistoryViewModel.archiveExpenseGroup(name)
                if (binding.walletSpinner.text.toString() == name) {
                    binding.walletSpinner.text = null
                    walletSpinnerPositionGlobalBeforeAdd = -1
                }
                sharedModViewModel.set(null)

                dismissAndDropdownSpinner(binding.walletSpinner)
            }
        }

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

        setSpinners()

        setDateEditText()
        setTimeEditText()

        setButtonOnClickListener()

        restoreAmountDateCommentValues()
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

    private fun setSpinners() {
        setGroupAndSubGroupSpinnerAdapter()
        setGroupSpinnerOnClickListener()
        setSubGroupSpinnerOnClickListener()

        setWalletSpinnerAdapter()
        setWalletSpinnerOnItemClickListener()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setButtonOnClickListener() {
        binding.addHistoryButton.setOnClickListener {
            val subGroupNameBinding = binding.subGroupSpinner.text.toString()
            val amountBinding = binding.amountEditText.text.toString().trim()
            val commentBinding = binding.commentEditText.text.toString().trim()
            val walletNameBinding = binding.walletSpinner.text.toString()
            val dateBinding = binding.dateEditText.text.toString().trim()
            val timeBinding = binding.timeEditText.text.toString().trim()

            val subGroupNameBindingValidation = EmptyValidator(subGroupNameBinding).validate()
            binding.subGroupSpinnerLayout.error = if (!subGroupNameBindingValidation.isSuccess) getString(subGroupNameBindingValidation.message) else null

            val amountBindingValidation = EmptyValidator(amountBinding).validate()
            binding.amountLayout.error = if (!amountBindingValidation.isSuccess) getString(amountBindingValidation.message) else null

            val walletNameBindingValidation = EmptyValidator(walletNameBinding).validate()
            binding.walletSpinnerLayout.error = if (!walletNameBindingValidation.isSuccess) getString(walletNameBindingValidation.message) else null

            val dateBindingValidation = EmptyValidator(dateBinding).validate()
            binding.dateLayout.error = if (!dateBindingValidation.isSuccess) getString(dateBindingValidation.message) else null

            val timeBindingValidation = EmptyValidator(timeBinding).validate()
            binding.timeLayout.error = if (!timeBindingValidation.isSuccess) getString(timeBindingValidation.message) else null

            if (subGroupNameBindingValidation.isSuccess
                && amountBindingValidation.isSuccess
                && walletNameBindingValidation.isSuccess
                && dateBindingValidation.isSuccess
                && timeBindingValidation.isSuccess) {

                val fullDateTime = dateBinding.plus(" ").plus(timeBinding)
                val parsedLocalDateTime = LocalDateTime.from(dateTimeFormatter.parse(fullDateTime))

                addHistoryViewModel.addExpenseHistory(
                    subGroupNameBinding,
                    amountBinding.toDouble(),
                    commentBinding,
                    parsedLocalDateTime,
                    walletNameBinding
                )

                sharedModViewModel.set(null)
                navigateToHome()
            }
        }
    }

    private fun navigateToHome() {
        findNavController().navigate(R.id.action_navigation_add_expense_to_navigation_home)
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

    private fun setGroupAndSubGroupSpinnerAdapter() {
        addHistoryViewModel.getAllExpenseGroupNotArchivedLiveData().observe(viewLifecycleOwner) { groups ->
            val spinnerGroupItems = getGroupItemsForSpinner(groups)

            val groupSpinnerAdapter = SpinnerAdapter(
                requireContext(),
                R.layout.item_with_del,
                spinnerGroupItems,
                ADD_NEW_EXPENSE_GROUP,
                archiveGroupListener)

            binding.groupSpinner.setAdapter(groupSpinnerAdapter)

            setIfAvailableGroupSpinnersValue(groupSpinnerAdapter)

            setSubGroupSpinnerAdapter()
        }
    }

    private fun getGroupItemsForSpinner(groups: List<ExpenseGroup>?): ArrayList<String> {
        val spinnerItems = ArrayList<String>()

        groups?.forEach { it ->
            spinnerItems.add(it.name)
        }
        spinnerItems.add(ADD_NEW_EXPENSE_GROUP)

        return spinnerItems
    }

    private fun setSubGroupSpinnerAdapter() {
        val groupSpinnerBinding = binding.groupSpinner.text.toString()

        if (groupSpinnerBinding.isNotBlank()) {
            setSubGroupSpinnerAdapterByGroupName(groupSpinnerBinding)
        } else {
            setEmptySubGroupSpinnerAdapter()
        }
    }

    private fun setSubGroupSpinnerAdapterByGroupName(groupSpinnerBinding: String) {
        addHistoryViewModel.getExpenseGroupNotArchivedWithExpenseSubGroupsNotArchivedByExpenseGroupNameLiveData(
            groupSpinnerBinding
        ).observe(viewLifecycleOwner) { groupWithSubGroups ->
            val spinnerSubItems =
                getSpinnerSubItemsNotArchived(groupWithSubGroups)

            val subGroupSpinnerAdapter =
                SpinnerAdapter(requireContext(), R.layout.item_with_del, spinnerSubItems, ADD_NEW_EXPENSE_SUB_GROUP, archiveSubGroupListener)

            binding.subGroupSpinner.setAdapter(subGroupSpinnerAdapter)

            setIfAvailableSubGroupSpinnersValue(subGroupSpinnerAdapter)
        }
    }

    private fun setEmptySubGroupSpinnerAdapter() {
        val emptySubGroupSpinnerAdapter = getEmptySubGroupSpinnerAdapter()

        binding.subGroupSpinner.setAdapter(emptySubGroupSpinnerAdapter)
    }

    private fun getEmptySubGroupSpinnerAdapter(): SpinnerAdapter {
        val subGroupSpinnerItems = ArrayList<String>()

        subGroupSpinnerItems.add(ADD_NEW_EXPENSE_SUB_GROUP)

        return SpinnerAdapter(
            requireContext(),
            R.layout.item_with_del,
            subGroupSpinnerItems,
            ADD_NEW_EXPENSE_GROUP,
            archiveSubGroupListener
        )
    }

    private fun setGroupSpinnerOnClickListener() {
        binding.groupSpinner.setOnItemClickListener {
                _, _, position, _ ->

            if (position != groupSpinnerPositionGlobalBeforeAdd) {
                resetSubGroupSpinner()
            }

            val selectedGroupName =
                binding.groupSpinner.text.toString()

            if (selectedGroupName == ADD_NEW_EXPENSE_GROUP) {
                setPrevValue(groupSpinnerPositionGlobalBeforeAdd, binding.groupSpinner)

                saveAddTransactionFormBeforeAddGroup()

                val action =
                    AddExpenseHistoryFragmentDirections.actionNavigationAddExpenseToNavigationAddExpenseGroup()
                findNavController().navigate(action)
            } else {
                // TODO: getExpenseGroupNotArchivedWithExpenseSubGroupsNotArchivedByExpenseGroupNameLiveData doesn't work
                addHistoryViewModel.getExpenseGroupNotArchivedWithExpenseSubGroupsNotArchivedByExpenseGroupNameLiveData(
                    selectedGroupName
                ).observe(viewLifecycleOwner) { groupWithSubGroups ->
                    val spinnerSubItems = getSpinnerSubItemsNotArchived(groupWithSubGroups)
                    val subGroupSpinnerAdapter =
                        SpinnerAdapter(requireContext(), R.layout.item_with_del, spinnerSubItems, ADD_NEW_EXPENSE_SUB_GROUP, archiveSubGroupListener)

                    binding.subGroupSpinner.setAdapter(subGroupSpinnerAdapter)
                }

                groupSpinnerPositionGlobalBeforeAdd = position
            }
        }
    }

    private fun setPrevValue(position: Int?, spinner: AutoCompleteTextView) {
        if (position == null || position == -1) {
            spinner.text = null
        } else {
            val name = spinner.adapter.getItem(position).toString()
            spinner.setText(name, false)
        }
    }

    private fun setSubGroupSpinnerOnClickListener() {
        binding.subGroupSpinner.setOnItemClickListener {
                _, _, position, _ ->

            val selectedSubGroupName = binding.subGroupSpinner.text.toString()

            if (selectedSubGroupName == ADD_NEW_EXPENSE_SUB_GROUP) {
                setPrevValue(subGroupSpinnerPositionGlobalBeforeAdd, binding.subGroupSpinner)

                saveAddTransactionFormBeforeAddSubGroup()

                val action = AddExpenseHistoryFragmentDirections.actionNavigationAddExpenseToNavigationAddExpenseSubGroup()
                action.expenseGroupName = binding.groupSpinner.text.toString()
                findNavController().navigate(action)
            } else {
                subGroupSpinnerPositionGlobalBeforeAdd = position
            }
        }
    }

    private fun setWalletSpinnerOnItemClickListener() {
        binding.walletSpinner.setOnItemClickListener {
                _, _, position, _ ->

            val selectedWalletName = binding.walletSpinner.text.toString()

            if (selectedWalletName == ADD_NEW_WALLET) {
                setPrevValue(walletSpinnerPositionGlobalBeforeAdd, binding.walletSpinner)

                saveAddTransactionFormBeforeAddWallet()

                val action = AddExpenseHistoryFragmentDirections.actionNavigationAddExpenseToNavigationAddWallet()
                action.source = ADD_EXPENSE_HISTORY_FRAGMENT
                findNavController().navigate(action)
            } else {
                walletSpinnerPositionGlobalBeforeAdd = position
            }
        }
    }

    private fun setIfAvailableGroupSpinnersValue(groupSpinnerAdapter: SpinnerAdapter) {
        val groupNameArg = args.expenseGroupName

        if (groupNameArg != null && groupNameArg.isNotBlank()) {
            val groupSpinnerPosition = groupSpinnerAdapter.getPosition(groupNameArg)

            resetSubGroupSpinner()
            if (groupSpinnerPosition != -1) {
                groupSpinnerPositionGlobalBeforeAdd = groupSpinnerPosition

                val groupName = groupSpinnerAdapter.getItem(groupSpinnerPosition)

                binding.groupSpinner.setText(groupName, false)
            }
        } else {
            restoreGroupSpinnerValue(groupSpinnerAdapter)
        }
    }

    private fun restoreGroupSpinnerValue(groupSpinnerAdapter: SpinnerAdapter) {
        val mod = sharedModViewModel.modelForm

        val groupSpinnerPosition = mod?.groupSpinnerPosition

        if (groupSpinnerPosition != null && groupSpinnerPosition != -1) {
            groupSpinnerPositionGlobalBeforeAdd = groupSpinnerPosition
            resetSubGroupSpinner()

            val groupName = groupSpinnerAdapter.getItem(groupSpinnerPosition)

            binding.groupSpinner.setText(groupName, false)
        }
    }

    private fun setIfAvailableSubGroupSpinnersValue(subGroupSpinnerAdapter: SpinnerAdapter) {
        val subGroupNameArg = args.expenseSubGroupName

        if (subGroupNameArg != null && subGroupNameArg.isNotBlank()) {
            val subGroupSpinnerPosition = subGroupSpinnerAdapter.getPosition(subGroupNameArg)

            if (subGroupSpinnerPosition != -1) {
                subGroupSpinnerPositionGlobalBeforeAdd = subGroupSpinnerPosition

                val subGroupName = subGroupSpinnerAdapter.getItem(subGroupSpinnerPosition)

                binding.subGroupSpinner.setText(subGroupName, false)
            }
        } else if (args.expenseGroupName == null || args.expenseGroupName?.isBlank() == true) {
            restoreSubGroupSpinnerValue(subGroupSpinnerAdapter)
        }
    }

    private fun restoreSubGroupSpinnerValue(subGroupSpinnerAdapter: SpinnerAdapter) {
        val mod = sharedModViewModel.modelForm

        val subGroupSpinnerPosition = mod?.subGroupSpinnerPosition

        if (subGroupSpinnerPosition != null && subGroupSpinnerPosition != -1) {
            subGroupSpinnerPositionGlobalBeforeAdd = subGroupSpinnerPosition

            val subGroupName = subGroupSpinnerAdapter.getItem(subGroupSpinnerPosition)

            binding.subGroupSpinner.setText(subGroupName, false)
        }
    }

    private fun setIfAvailableWalletSpinnersValue(walletSpinnerAdapter: SpinnerAdapter) {
        val walletNameArg = args.walletName ?: ""

        if (walletNameArg.isNotBlank()) {
            val walletSpinnerPosition = walletSpinnerAdapter.getPosition(walletNameArg)

            if (walletSpinnerPosition != -1) {
                walletSpinnerPositionGlobalBeforeAdd = walletSpinnerPosition

                val walletName = walletSpinnerAdapter.getItem(walletSpinnerPosition)

                binding.walletSpinner.setText(walletName, false)
            }
        } else {
            restoreWalletSpinnerValue(walletSpinnerAdapter)
        }
    }

    private fun restoreWalletSpinnerValue(walletSpinnerAdapter: SpinnerAdapter) {
        val mod = sharedModViewModel.modelForm

        val walletSpinnerPosition = mod?.walletSpinnerPosition

        if (walletSpinnerPosition != null && walletSpinnerPosition != -1) {
            walletSpinnerPositionGlobalBeforeAdd = walletSpinnerPosition

            val walletName = walletSpinnerAdapter.getItem(walletSpinnerPosition)

            binding.walletSpinner.setText(walletName, false)
        }
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

    private fun getSpinnerSubItemsNotArchived(groupWithSubGroups: ExpenseGroupWithExpenseSubGroups?): ArrayList<String> {
        val spinnerSubItems = ArrayList<String>()

        groupWithSubGroups?.expenseSubGroups?.forEach {
            if (it.archivedDate == null) {
                spinnerSubItems.add(it.name)
            }
        }

        spinnerSubItems.add(ADD_NEW_EXPENSE_SUB_GROUP)

        return spinnerSubItems
    }

    private fun setWalletSpinnerAdapter() {
        addHistoryViewModel.walletsNotArchivedLiveData.observe(viewLifecycleOwner) { wallets ->

            val spinnerWalletItems = getWalletItemsForSpinner(wallets)

            val walletSpinnerAdapter = SpinnerAdapter(requireContext(), R.layout.item_with_del, spinnerWalletItems, ADD_NEW_WALLET, archiveWalletListener)
            binding.walletSpinner.setAdapter(walletSpinnerAdapter)

            setIfAvailableWalletSpinnersValue(walletSpinnerAdapter)
        }
    }

    private fun getWalletItemsForSpinner(walletList: List<Wallet>?): ArrayList<String> {
        val spinnerItems = ArrayList<String>()

        walletList?.forEach { it ->
            spinnerItems.add(it.name)
        }
        spinnerItems.add(ADD_NEW_WALLET)

        return spinnerItems
    }

    private fun dismissAndDropdownSpinner(spinner: AutoCompleteTextView) {
        spinner.dismissDropDown()
        spinner.postDelayed({
            spinner.showDropDown()
        }, 30)
    }

    private fun saveAddTransactionFormBeforeAddGroup() {
        val amountBinding = binding.amountEditText.text.toString().trim()
        val dateBinding = binding.dateEditText.text.toString().trim()
        val timeBinding = binding.timeEditText.text.toString().trim()
        val commentBinding = binding.commentEditText.text.toString().trim()

        val addTransactionForm = AddTransactionForm(
            groupSpinnerPosition = groupSpinnerPositionGlobalBeforeAdd,
            subGroupSpinnerPosition = subGroupSpinnerPositionGlobalBeforeAdd,
            walletSpinnerPosition = walletSpinnerPositionGlobalBeforeAdd,
            amount = amountBinding,
            date = dateBinding,
            time = timeBinding,
            comment = commentBinding
        )
        sharedModViewModel.set(addTransactionForm)
    }

    private fun saveAddTransactionFormBeforeAddSubGroup() {
        val amountBinding = binding.amountEditText.text.toString().trim()
        val dateBinding = binding.dateEditText.text.toString().trim()
        val timeBinding = binding.timeEditText.text.toString().trim()
        val commentBinding = binding.commentEditText.text.toString().trim()

        val addTransactionForm = AddTransactionForm(
            groupSpinnerPosition = groupSpinnerPositionGlobalBeforeAdd,
            subGroupSpinnerPosition = subGroupSpinnerPositionGlobalBeforeAdd,
            walletSpinnerPosition = walletSpinnerPositionGlobalBeforeAdd,
            amount = amountBinding,
            date = dateBinding,
            time = timeBinding,
            comment = commentBinding
        )
        sharedModViewModel.set(addTransactionForm)
    }

    private fun saveAddTransactionFormBeforeAddWallet() {
        val amountBinding = binding.amountEditText.text.toString().trim()
        val dateBinding = binding.dateEditText.text.toString().trim()
        val timeBinding = binding.timeEditText.text.toString().trim()
        val commentBinding = binding.commentEditText.text.toString().trim()

        val addTransactionForm = AddTransactionForm(
            groupSpinnerPosition = groupSpinnerPositionGlobalBeforeAdd,
            subGroupSpinnerPosition = subGroupSpinnerPositionGlobalBeforeAdd,
            walletSpinnerPosition = walletSpinnerPositionGlobalBeforeAdd,
            amount = amountBinding,
            comment = commentBinding,
            date = dateBinding,
            time = timeBinding
        )
        sharedModViewModel.set(addTransactionForm)
    }

    private fun resetSubGroupSpinner() {
        binding.subGroupSpinner.text = null
    }

}
