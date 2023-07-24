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
import com.romandevyatov.bestfinance.utils.Constants
import com.romandevyatov.bestfinance.viewmodels.*
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.AddExpenseHistoryViewModel
import com.romandevyatov.bestfinance.viewmodels.shared.SharedModifiedViewModel
import com.romandevyatov.bestfinance.viewmodels.shared.models.AddTransactionForm
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.util.*

@AndroidEntryPoint
class AddExpenseHistoryFragment : Fragment() {

    private var _binding: FragmentAddExpenseHistoryBinding? = null
    private val binding get() = _binding!!

    private val addExpenseHistoryViewModel: AddExpenseHistoryViewModel by viewModels()

    private val sharedModViewModel: SharedModifiedViewModel<AddTransactionForm> by activityViewModels()

    private var prevSubGroupSpinnerPositionGlobal: Int? = null
    private var prevWalletSpinnerPositionGlobal: Int? = null

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

    private val archiveSubGroupListener =
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
        setTimeEditText()

        setButtonOnClickListener()

        restoreAmountDateCommentValues()
    }

    private fun setSpinners() {
        setGroupAndSubGroupSpinnerAdapter()
        setGroupSpinnerOnClickListener()
        setSubGroupSpinnerListener()

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
            val amountBinding = binding.amountEditText.text.toString().trim()
            val commentBinding = binding.commentEditText.text.toString().trim()
            val walletNameBinding = binding.walletSpinner.text.toString()
            val dateBinding = binding.dateEditText.text.toString().trim()
            val timeBinding = binding.timeEditText.text.toString().trim()

            val expenseSubGroupNameBindingValidation = EmptyValidator(expenseSubGroupNameBinding).validate()
            binding.expenseSubGroupSpinnerLayout.error = if (!expenseSubGroupNameBindingValidation.isSuccess) getString(expenseSubGroupNameBindingValidation.message) else null

            val amountBindingValidation = EmptyValidator(amountBinding).validate()
            binding.amountTextInputLayout.error = if (!amountBindingValidation.isSuccess) getString(amountBindingValidation.message) else null

            val walletNameBindingValidation = EmptyValidator(walletNameBinding).validate()
            binding.walletSpinnerLayout.error = if (!walletNameBindingValidation.isSuccess) getString(walletNameBindingValidation.message) else null

            val dateBindingValidation = EmptyValidator(dateBinding).validate()
            binding.dateTextInputLayout.error = if (!dateBindingValidation.isSuccess) getString(dateBindingValidation.message) else null

            val timeBindingValidation = EmptyValidator(timeBinding).validate()
            binding.timeTextInputLayout.error = if (!timeBindingValidation.isSuccess) getString(timeBindingValidation.message) else null

            if (expenseSubGroupNameBindingValidation.isSuccess
                && amountBindingValidation.isSuccess
                && walletNameBindingValidation.isSuccess
                && dateBindingValidation.isSuccess
                && timeBindingValidation.isSuccess) {

                val fullDateTime = dateBinding.plus(" ").plus(timeBinding)
                val parsedLocalDateTime = LocalDateTime.from(dateTimeFormatter.parse(fullDateTime))

                addExpenseHistoryViewModel.addExpenseHistory(
                    expenseSubGroupNameBinding,
                    amountBinding.toDouble(),
                    commentBinding,
                    parsedLocalDateTime,
                    walletNameBinding
                )

                sharedModViewModel.set(null)
                findNavController().navigate(R.id.action_navigation_add_expense_to_navigation_home)
            }
        }
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
        addExpenseHistoryViewModel.getAllExpenseGroupNotArchivedLiveData().observe(viewLifecycleOwner) { expenseGroups ->
            val spinnerGroupItems = getGroupItemsForSpinner(expenseGroups)

            val groupSpinnerAdapter = SpinnerAdapter(requireContext(), R.layout.item_with_del, spinnerGroupItems, Constants.ADD_NEW_INCOME_GROUP, archiveExpenseGroupListener)
            groupSpinnerAdapterGlobal = groupSpinnerAdapter

            binding.expenseGroupSpinner.setAdapter(groupSpinnerAdapter)

            setIfAvailableGroupSpinnersValue(groupSpinnerAdapter)

            setSubGroupSpinnerAdapter()
        }
    }

    private fun getGroupItemsForSpinner(expenseGroupList: List<ExpenseGroup>?): ArrayList<String> {
        val spinnerItems = ArrayList<String>()

        expenseGroupList?.forEach { it ->
            spinnerItems.add(it.name)
        }
        spinnerItems.add(Constants.ADD_NEW_EXPENSE_GROUP)

        return spinnerItems
    }

    private fun setSubGroupSpinnerAdapter() {
        val groupSpinnerBinding = binding.expenseGroupSpinner.text.toString()

        if (groupSpinnerBinding.isNotBlank()) {
            setSubGroupSpinnerAdapterByGroupName(groupSpinnerBinding)
        } else {
            setEmptySubGroupSpinnerAdapter()
        }
    }

    private fun setSubGroupSpinnerAdapterByGroupName(groupSpinnerBinding: String) {
        addExpenseHistoryViewModel.getExpenseGroupNotArchivedWithExpenseSubGroupsNotArchivedByExpenseGroupNameLiveData(
            groupSpinnerBinding
        ).observe(viewLifecycleOwner) { groupWithSubGroups ->
            val spinnerSubItems =
                getSpinnerSubItemsNotArchived(groupWithSubGroups)

            val subGroupSpinnerAdapter = SpinnerAdapter(
                requireContext(),
                R.layout.item_with_del,
                spinnerSubItems,
                Constants.ADD_NEW_EXPENSE_SUB_GROUP,
                archiveSubGroupListener
            )
            subGroupSpinnerAdapterGlobal = subGroupSpinnerAdapter
            resetSubGroupSpinner()
            binding.expenseSubGroupSpinner.setAdapter(subGroupSpinnerAdapter)

            setIfAvailableSubGroupSpinnersValue(subGroupSpinnerAdapter)
        }
    }

    private fun setEmptySubGroupSpinnerAdapter() {
        val emptySubGroupSpinnerAdapter = getEmptySubGroupSpinnerAdapter()
        subGroupSpinnerPositionGlobal = null // TODO: change on -1

        binding.expenseSubGroupSpinner.setAdapter(emptySubGroupSpinnerAdapter)
    }

    private fun getEmptySubGroupSpinnerAdapter(): SpinnerAdapter {
        val subGroupSpinnerItems = ArrayList<String>()
        subGroupSpinnerItems.add(Constants.ADD_NEW_EXPENSE_SUB_GROUP)

        val subGroupAdapter = SpinnerAdapter(requireContext(), R.layout.item_with_del, subGroupSpinnerItems, Constants.ADD_NEW_EXPENSE_GROUP, archiveSubGroupListener)
        subGroupSpinnerAdapterGlobal = subGroupAdapter

        return subGroupAdapter
    }

    private fun setGroupSpinnerOnClickListener() {
        binding.expenseGroupSpinner.setOnItemClickListener {
                _, _, position, _ ->

            groupSpinnerPositionGlobal = position
            resetSubGroupSpinner()

            val selectedGroupName =
                binding.expenseGroupSpinner.text.toString()

            if (selectedGroupName == Constants.ADD_NEW_EXPENSE_GROUP) {
                resetGroupSpinner()
                saveAddTransactionFormBeforeAddGroup()

                val action =
                    AddExpenseHistoryFragmentDirections.actionNavigationAddExpenseToNavigationAddNewExpenseGroup()
                findNavController().navigate(action)
            } else {

                // TODO: getExpenseGroupNotArchivedWithExpenseSubGroupsNotArchivedByExpenseGroupNameLiveData doesn't work
                addExpenseHistoryViewModel.getExpenseGroupNotArchivedWithExpenseSubGroupsNotArchivedByExpenseGroupNameLiveData(
                    selectedGroupName
                )
                    .observe(viewLifecycleOwner) { groupWithSubGroups ->
                        val spinnerSubItems = getSpinnerSubItemsNotArchived(groupWithSubGroups)
                        val adapter = SpinnerAdapter(
                            requireContext(),
                            R.layout.item_with_del,
                            spinnerSubItems,
                            Constants.ADD_NEW_INCOME_SUB_GROUP,
                            archiveSubGroupListener
                        )

                        subGroupSpinnerAdapterGlobal = adapter
                        binding.expenseSubGroupSpinner.setAdapter(adapter)
                    }
            }
        }
    }

    private fun setSubGroupSpinnerListener() {
        binding.expenseSubGroupSpinner.setOnItemClickListener {
                _, _, position, _ ->

            subGroupSpinnerPositionGlobal = position

            val selectedSubGroupName = binding.expenseSubGroupSpinner.text.toString()

            if (selectedSubGroupName == Constants.ADD_NEW_EXPENSE_SUB_GROUP) {
                subGroupSpinnerPositionGlobal = prevSubGroupSpinnerPositionGlobal
                saveAddTransactionFormBeforeAddSubGroup()

                val action = AddExpenseHistoryFragmentDirections.actionNavigationAddExpenseToNavigationAddNewExpenseSubGroup()
                action.expenseGroupName = binding.expenseGroupSpinner.text.toString()
                findNavController().navigate(action)
            } else {
                prevSubGroupSpinnerPositionGlobal = position
            }
        }
    }

    private fun setIfAvailableGroupSpinnersValue(
        groupSpinnerAdapter: SpinnerAdapter
    ) {
        val groupNameArg = args.expenseGroupName
        if (groupNameArg != null && groupNameArg.isNotBlank()) {
            val spinnerPosition = groupSpinnerAdapter.getPosition(groupNameArg)
            groupSpinnerPositionGlobal = spinnerPosition
            subGroupSpinnerPositionGlobal = null

            val groupName = groupSpinnerAdapter.getItem(spinnerPosition)

            binding.expenseGroupSpinner.setText(groupName)
        } else {
            restoreGroupSpinnerValue(groupSpinnerAdapter)
        }
    }

    private fun restoreGroupSpinnerValue(
        groupSpinnerAdapter: SpinnerAdapter
    ) {
        val mod = sharedModViewModel.modelForm

        val groupSpinnerPosition = mod?.groupSpinnerPosition
        if (groupSpinnerPosition != null) {
            groupSpinnerPositionGlobal = groupSpinnerPosition
            resetSubGroupSpinner()

            val selectedGroupName = groupSpinnerAdapter.getItem(groupSpinnerPosition)

            binding.expenseGroupSpinner.setText(selectedGroupName)
        }
    }

    private fun setIfAvailableSubGroupSpinnersValue(subGroupSpinnerAdapter: SpinnerAdapter) {
        val subGroupNameArg = args.expenseSubGroupName

        if (subGroupNameArg != null && subGroupNameArg.isNotBlank()) {
            val subGroupSpinnerPosition = subGroupSpinnerAdapter.getPosition(subGroupNameArg)
            subGroupSpinnerPositionGlobal = subGroupSpinnerPosition

            val subGroupName = subGroupSpinnerAdapter.getItem(subGroupSpinnerPosition)

            binding.expenseSubGroupSpinner.setText(subGroupName)
        } else {
            restoreSubGroupSpinnerValue(subGroupSpinnerAdapter)
        }
    }

    private fun restoreSubGroupSpinnerValue(subGroupSpinnerAdapter: SpinnerAdapter) {
        val mod = sharedModViewModel.modelForm

        val subGroupSpinnerPosition = mod?.subGroupSpinnerPosition
        if (subGroupSpinnerPosition != null) {
            subGroupSpinnerPositionGlobal = subGroupSpinnerPosition

            val selectedSubGroupName = subGroupSpinnerAdapter.getItem(subGroupSpinnerPosition)

            binding.expenseSubGroupSpinner.setText(selectedSubGroupName)
        }
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

        if (mod?.time != null) {
            binding.timeEditText.setText(mod.time)
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

        spinnerSubItems.add(Constants.ADD_NEW_EXPENSE_GROUP)

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
                _, _, position, _ ->

            walletSpinnerPositionGlobal = position

            val selectedWalletName = binding.walletSpinner.text.toString()

            if (selectedWalletName == Constants.ADD_NEW_WALLET) {
                walletSpinnerPositionGlobal = prevWalletSpinnerPositionGlobal
                saveAddTransactionFormBeforeAddWallet()

                val action = AddExpenseHistoryFragmentDirections.actionNavigationAddExpenseToNavigationAddWallet()
                action.source = Constants.ADD_EXPENSE_HISTORY_FRAGMENT
                findNavController().navigate(action)
            } else {
                prevWalletSpinnerPositionGlobal = walletSpinnerPositionGlobal
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

    private fun saveAddTransactionFormBeforeAddGroup() {
        val amountBinding = binding.amountEditText.text.toString().trim()
        val dateEditText = binding.dateEditText.text.toString().trim()
        val timeEditText = binding.timeEditText.text.toString().trim()
        val commentBinding = binding.commentEditText.text.toString().trim()

        val addTransactionForm = AddTransactionForm(
            groupSpinnerPosition = groupSpinnerPositionGlobal,
            subGroupSpinnerPosition = subGroupSpinnerPositionGlobal,
            walletSpinnerPosition = walletSpinnerPositionGlobal,
            amount = amountBinding,
            date = dateEditText,
            time = timeEditText,
            comment = commentBinding
        )
        sharedModViewModel.set(addTransactionForm)
    }

    private fun saveAddTransactionFormBeforeAddSubGroup() {
        val amountBinding = binding.amountEditText.text.toString().trim()
        val dateEditText = binding.dateEditText.text.toString().trim()
        val timeBinding = binding.timeEditText.text.toString().trim()
        val commentBinding = binding.commentEditText.text.toString().trim()

        val addTransactionForm = AddTransactionForm(
            groupSpinnerPosition = groupSpinnerPositionGlobal,
            subGroupSpinnerPosition = subGroupSpinnerPositionGlobal,
            walletSpinnerPosition = walletSpinnerPositionGlobal,
            amount = amountBinding,
            date = dateEditText,
            time = timeBinding,
            comment = commentBinding
        )
        sharedModViewModel.set(addTransactionForm)
    }

    private fun saveAddTransactionFormBeforeAddWallet() {
        val amountBinding = binding.amountEditText.text.toString().trim()
        val commentBinding = binding.commentEditText.text.toString().trim()
        val dateEditText = binding.dateEditText.text.toString().trim()
        val timeEditText = binding.timeEditText.text.toString().trim()

        val addTransactionForm = AddTransactionForm(
            groupSpinnerPosition = groupSpinnerPositionGlobal,
            subGroupSpinnerPosition = subGroupSpinnerPositionGlobal,
            walletSpinnerPosition = walletSpinnerPositionGlobal,
            amount = amountBinding,
            comment = commentBinding,
            date = dateEditText,
            time = timeEditText
        )
        sharedModViewModel.set(addTransactionForm)
    }

    private fun resetSubGroupSpinner() {
        subGroupSpinnerPositionGlobal = null
        binding.expenseSubGroupSpinner.text = null
    }

    private fun resetGroupSpinner() {
        groupSpinnerPositionGlobal = null
        binding.expenseGroupSpinner.text = null
    }

}