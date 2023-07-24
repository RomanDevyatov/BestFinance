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

    private val addHistoryViewModel: AddExpenseHistoryViewModel by viewModels()

    private val sharedModViewModel: SharedModifiedViewModel<AddTransactionForm> by activityViewModels()

    private var prevGroupSpinnerPositionGlobal: Int? = null
    private var prevSubGroupSpinnerPositionGlobal: Int? = null
    private var prevWalletSpinnerPositionGlobal: Int? = null

    private val args: AddExpenseHistoryFragmentArgs by navArgs()

    private val archiveGroupListener =
        object : SpinnerAdapter.DeleteItemClickListener {

            @RequiresApi(Build.VERSION_CODES.O)
            override fun archive(name: String) {
                addHistoryViewModel.archiveExpenseGroup(name)
            }
        }

    private val archiveSubGroupListener =
        object : SpinnerAdapter.DeleteItemClickListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun archive(name: String) {
                addHistoryViewModel.archiveExpenseSubGroup(name)
            }
        }

    private val archiveWalletListener =
        object : SpinnerAdapter.DeleteItemClickListener {

            @RequiresApi(Build.VERSION_CODES.O)
            override fun archive(name: String) {
                addHistoryViewModel.archiveWallet(name)
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
        setSubGroupSpinnerOnClickListener()

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
        addHistoryViewModel.getAllExpenseGroupNotArchivedLiveData().observe(viewLifecycleOwner) { groups ->
            val spinnerGroupItems = getGroupItemsForSpinner(groups)

            val groupSpinnerAdapter = SpinnerAdapter(requireContext(), R.layout.item_with_del, spinnerGroupItems,
                Constants.ADD_NEW_EXPENSE_GROUP, archiveGroupListener)

            binding.groupSpinner.setAdapter(groupSpinnerAdapter)

            setIfAvailableGroupSpinnersValue(groupSpinnerAdapter)

            setSubGroupSpinnerAdapter()
        }
    }

    private fun getGroupItemsForSpinner(groupList: List<ExpenseGroup>?): ArrayList<String> {
        val spinnerItems = ArrayList<String>()

        groupList?.forEach { it ->
            spinnerItems.add(it.name)
        }
        spinnerItems.add(Constants.ADD_NEW_EXPENSE_GROUP)

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
                SpinnerAdapter(requireContext(), R.layout.item_with_del, spinnerSubItems,
                    Constants.ADD_NEW_EXPENSE_SUB_GROUP, archiveSubGroupListener)

            binding.subGroupSpinner.setAdapter(subGroupSpinnerAdapter)

            setIfAvailableSubGroupSpinnersValue(subGroupSpinnerAdapter)
        }
    }

    private fun setEmptySubGroupSpinnerAdapter() {
        val emptySubGroupSpinnerAdapter = getEmptySubGroupSpinnerAdapter()
//        subGroupSpinnerPositionGlobal = null

        binding.subGroupSpinner.setAdapter(emptySubGroupSpinnerAdapter)
    }

    private fun getEmptySubGroupSpinnerAdapter(): SpinnerAdapter {
        val subGroupSpinnerItems = ArrayList<String>()

        subGroupSpinnerItems.add(Constants.ADD_NEW_EXPENSE_SUB_GROUP)

        val subGroupAdapter = SpinnerAdapter(requireContext(), R.layout.item_with_del, subGroupSpinnerItems,
            Constants.ADD_NEW_EXPENSE_GROUP, archiveSubGroupListener)

        return subGroupAdapter
    }

    private fun setGroupSpinnerOnClickListener() {
        binding.groupSpinner.setOnItemClickListener {
                _, _, position, _ ->

            if (position != prevGroupSpinnerPositionGlobal) {
                resetSubGroupSpinner()
            }

            val selectedGroupName =
                binding.groupSpinner.text.toString()

            if (selectedGroupName == Constants.ADD_NEW_EXPENSE_GROUP) {
                saveAddTransactionFormBeforeAddGroup()

                val action =
                    AddExpenseHistoryFragmentDirections.actionNavigationAddExpenseToNavigationAddNewExpenseGroup()
                findNavController().navigate(action)
            } else {
                // TODO: getExpenseGroupNotArchivedWithExpenseSubGroupsNotArchivedByExpenseGroupNameLiveData doesn't work
                addHistoryViewModel.getExpenseGroupNotArchivedWithExpenseSubGroupsNotArchivedByExpenseGroupNameLiveData(
                    selectedGroupName
                ).observe(viewLifecycleOwner) { groupWithSubGroups ->
                    val spinnerSubItems = getSpinnerSubItemsNotArchived(groupWithSubGroups)
                    val adapter =
                        SpinnerAdapter(requireContext(), R.layout.item_with_del, spinnerSubItems,
                            Constants.ADD_NEW_EXPENSE_SUB_GROUP, archiveSubGroupListener
                        )

                    binding.subGroupSpinner.setAdapter(adapter)
                }

                prevGroupSpinnerPositionGlobal = position
            }
        }
    }

    private fun setSubGroupSpinnerOnClickListener() {
        binding.subGroupSpinner.setOnItemClickListener {
                _, _, position, _ ->

            val selectedSubGroupName = binding.subGroupSpinner.text.toString()

            if (selectedSubGroupName == Constants.ADD_NEW_EXPENSE_SUB_GROUP) {
                saveAddTransactionFormBeforeAddSubGroup()

                val action = AddExpenseHistoryFragmentDirections.actionNavigationAddExpenseToNavigationAddNewExpenseSubGroup()
                action.expenseGroupName = binding.groupSpinner.text.toString()
                findNavController().navigate(action)
            } else {
                prevSubGroupSpinnerPositionGlobal = position
            }
        }
    }

    private fun setWalletSpinnerOnItemClickListener() {
        binding.walletSpinner.setOnItemClickListener {
                _, _, position, _ ->

            val selectedWalletName = binding.walletSpinner.text.toString()

            if (selectedWalletName == Constants.ADD_NEW_WALLET) {
                saveAddTransactionFormBeforeAddWallet()

                val action = AddExpenseHistoryFragmentDirections.actionNavigationAddExpenseToNavigationAddWallet()
                action.source = Constants.ADD_EXPENSE_HISTORY_FRAGMENT
                findNavController().navigate(action)
            } else {
                prevWalletSpinnerPositionGlobal = position
            }
        }
    }

    private fun setIfAvailableGroupSpinnersValue(groupSpinnerAdapter: SpinnerAdapter) {
        val groupNameArg = args.expenseGroupName

        if (groupNameArg != null && groupNameArg.isNotBlank()) {

            val groupSpinnerPosition = groupSpinnerAdapter.getPosition(groupNameArg)
            prevGroupSpinnerPositionGlobal = groupSpinnerPosition

            val groupName = groupSpinnerAdapter.getItem(groupSpinnerPosition)

            binding.groupSpinner.setText(groupName)
        } else {
            restoreGroupSpinnerValue(groupSpinnerAdapter)
        }
//        binding.groupSpinner.threshold = Int.MAX_VALUE
    }

    private fun restoreGroupSpinnerValue(
        groupSpinnerAdapter: SpinnerAdapter
    ) {
        val mod = sharedModViewModel.modelForm

        val groupSpinnerPosition = mod?.groupSpinnerPosition
        if (groupSpinnerPosition != null) {
            prevGroupSpinnerPositionGlobal = groupSpinnerPosition
            resetSubGroupSpinner()

            val groupName = groupSpinnerAdapter.getItem(groupSpinnerPosition)

            binding.groupSpinner.setText(groupName)
        }
    }

    private fun setIfAvailableSubGroupSpinnersValue(subGroupSpinnerAdapter: SpinnerAdapter) {
        val subGroupNameArg = args.expenseSubGroupName

        if (subGroupNameArg != null && subGroupNameArg.isNotBlank()) {
            val subGroupSpinnerPosition = subGroupSpinnerAdapter.getPosition(subGroupNameArg)
            prevSubGroupSpinnerPositionGlobal = subGroupSpinnerPosition

            val subGroupName = subGroupSpinnerAdapter.getItem(subGroupSpinnerPosition)

            binding.subGroupSpinner.setText(subGroupName)
        } else {
            restoreSubGroupSpinnerValue(subGroupSpinnerAdapter)
        }
//        binding.subGroupSpinner.threshold = Int.MAX_VALUE
    }

    private fun restoreSubGroupSpinnerValue(subGroupSpinnerAdapter: SpinnerAdapter) {
        val mod = sharedModViewModel.modelForm

        val subGroupSpinnerPosition = mod?.subGroupSpinnerPosition
        prevSubGroupSpinnerPositionGlobal = subGroupSpinnerPosition

        if (subGroupSpinnerPosition != null) {
            val subGroupName = subGroupSpinnerAdapter.getItem(subGroupSpinnerPosition)

            binding.subGroupSpinner.setText(subGroupName)
        }
    }

    private fun setIfAvailableWalletSpinnersValue(walletSpinnerAdapter: SpinnerAdapter) {
        if (args.walletName != null && args.walletName!!.isNotBlank()) {
            val spinnerPosition = walletSpinnerAdapter.getPosition(args.walletName)
            prevWalletSpinnerPositionGlobal = spinnerPosition

            val walletName = walletSpinnerAdapter.getItem(spinnerPosition)

            binding.walletSpinner.setText(walletName)
        } else {
            restoreWalletSpinnerValue(walletSpinnerAdapter)
        }
//        binding.walletSpinner.threshold = Int.MAX_VALUE
    }

    private fun restoreWalletSpinnerValue(walletSpinnerAdapter: SpinnerAdapter) {
        val mod = sharedModViewModel.modelForm

        val walletSpinnerPosition = mod?.walletSpinnerPosition
        prevWalletSpinnerPositionGlobal = walletSpinnerPosition

        if (walletSpinnerPosition != null) {
            val walletName = walletSpinnerAdapter.getItem(walletSpinnerPosition)

            binding.walletSpinner.setText(walletName)
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

        spinnerSubItems.add(Constants.ADD_NEW_EXPENSE_SUB_GROUP)

        return spinnerSubItems
    }

    private fun setWalletSpinnerAdapter() {
        addHistoryViewModel.walletsNotArchivedLiveData.observe(viewLifecycleOwner) { wallets ->

            val spinnerWalletItems = getWalletItemsForSpinner(wallets)

            val walletSpinnerAdapter = SpinnerAdapter(requireContext(), R.layout.item_with_del, spinnerWalletItems,
                Constants.ADD_NEW_WALLET, archiveWalletListener)

            binding.walletSpinner.setAdapter(walletSpinnerAdapter)

            setIfAvailableWalletSpinnersValue(walletSpinnerAdapter)
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
        val dateBinding = binding.dateEditText.text.toString().trim()
        val timeBinding = binding.timeEditText.text.toString().trim()
        val commentBinding = binding.commentEditText.text.toString().trim()

        val addTransactionForm = AddTransactionForm(
            groupSpinnerPosition = prevGroupSpinnerPositionGlobal,
            walletSpinnerPosition = prevWalletSpinnerPositionGlobal,
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
            groupSpinnerPosition = prevGroupSpinnerPositionGlobal,
            subGroupSpinnerPosition = prevSubGroupSpinnerPositionGlobal,
            walletSpinnerPosition = prevWalletSpinnerPositionGlobal,
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
            groupSpinnerPosition = prevGroupSpinnerPositionGlobal,
            subGroupSpinnerPosition = prevSubGroupSpinnerPositionGlobal,
            walletSpinnerPosition = prevWalletSpinnerPositionGlobal,
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

//    private fun markButtonDisable(button: Button) {
//        button.isEnabled = false
//        button.setTextColor(ContextCompat.getColor(binding.addExpenseHistoryButton.context, R.color.white))
//        button.setBackgroundColor(ContextCompat.getColor(binding.addExpenseHistoryButton.context, R.color.black))
//    }
//
//    fun showWalletDialog(){
//        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
//        builder.setTitle("Title")
//
//        val input = EditText(requireContext())
//        input.hint = "Enter Text"
//        input.inputType = InputType.TYPE_CLASS_TEXT
//        builder.setView(input)
//
//        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
//            // Here you get get input text from the Edittext
//            var m_Text = input.text.toString()
//        })
//        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
//
//        builder.show()
//    }
}
