package com.romandevyatov.bestfinance.ui.fragments.addictions.income

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import com.romandevyatov.bestfinance.databinding.FragmentAddIncomeHistoryBinding
import com.romandevyatov.bestfinance.db.entities.IncomeGroup
import com.romandevyatov.bestfinance.db.entities.Wallet
import com.romandevyatov.bestfinance.db.entities.relations.IncomeGroupWithIncomeSubGroups
import com.romandevyatov.bestfinance.ui.adapters.spinnerutils.SpinnerAdapter
import com.romandevyatov.bestfinance.ui.validators.EmptyValidator
import com.romandevyatov.bestfinance.utils.Constants
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.AddIncomeHistoryViewModel
import com.romandevyatov.bestfinance.viewmodels.shared.SharedModifiedViewModel
import com.romandevyatov.bestfinance.viewmodels.shared.models.AddTransactionForm
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class AddIncomeHistoryFragment : Fragment() {

    private var _binding: FragmentAddIncomeHistoryBinding? = null
    private val binding get() = _binding!!

    private val addIncomeHistoryViewModel: AddIncomeHistoryViewModel by viewModels()

    private val sharedModViewModel: SharedModifiedViewModel<AddTransactionForm> by activityViewModels()

    private var groupSpinnerPositionGlobal: Int? = null
    private var subGroupSpinnerPositionGlobal: Int? = null
    private var walletSpinnerPositionGlobal: Int? = null

    private var subGroupSpinnerAdapterGlobal: SpinnerAdapter? = null
    private var groupSpinnerAdapterGlobal: SpinnerAdapter? = null
    private var walletSpinnerAdapterGlobal: SpinnerAdapter? = null

    private val args: AddIncomeHistoryFragmentArgs by navArgs()

    private val timeFormat = SimpleDateFormat("hh:mm", Locale.US)
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.US)
//    val iso8601DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    private val archiveGroupListener =
        object : SpinnerAdapter.DeleteItemClickListener {

            @RequiresApi(Build.VERSION_CODES.O)
            override fun archive(name: String) {
                addIncomeHistoryViewModel.archiveIncomeGroup(name)
            }
        }

    private val archiveSubGroupListener =
        object : SpinnerAdapter.DeleteItemClickListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun archive(name: String) {
                addIncomeHistoryViewModel.archiveIncomeSubGroup(name)
            }
        }

    private val archiveWalletListener =
        object : SpinnerAdapter.DeleteItemClickListener {

            @RequiresApi(Build.VERSION_CODES.O)
            override fun archive(name: String) {
                addIncomeHistoryViewModel.archiveWallet(name)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddIncomeHistoryBinding.inflate(inflater, container, false)
//        markButtonDisable(binding.addIncomeHistoryButton)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSpinners()

        setDateEditText()
        setTimeEditText()

        setButtonOnClickListener()

//        restoreAmountDateCommentValues()
    }

    private fun setSpinners() {
        setGroupAndSubGroupSpinnerAdapter()
        setIncomeGroupSpinnerOnClickListener()
        setIncomeSubGroupSpinnerListener()

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
                findNavController().navigate(R.id.action_navigation_add_income_to_navigation_home)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setButtonOnClickListener() {
        binding.addIncomeHistoryButton.setOnClickListener {
            val incomeSubGroupNameBinding = binding.incomeSubGroupSpinner.text.toString()
            val amountBinding = binding.amountEditText.text.toString()
            val commentBinding = binding.commentEditText.text.toString()
            val walletNameBinding = binding.walletSpinner.text.toString()
            val dateBinding = binding.dateEditText.text.toString().trim()
            val timeBinding = binding.timeEditText.text.toString().trim()

            val fullDateTime = getFullDateTime()

            val incomeSubGroupNameBindingValidation = EmptyValidator(incomeSubGroupNameBinding).validate()
            binding.incomeSubGroupSpinnerLayout.error = if (!incomeSubGroupNameBindingValidation.isSuccess) getString(incomeSubGroupNameBindingValidation.message) else null

            val amountBindingValidation = EmptyValidator(amountBinding).validate()
            binding.amountTextInputLayout.error = if (!amountBindingValidation.isSuccess) getString(amountBindingValidation.message) else null

            val walletNameBindingValidation = EmptyValidator(walletNameBinding).validate()
            binding.walletSpinnerLayout.error = if (!walletNameBindingValidation.isSuccess) getString(walletNameBindingValidation.message) else null

            val dateBindingValidation = EmptyValidator(dateBinding).validate()
            binding.dateTextInputLayout.error = if (!dateBindingValidation.isSuccess) getString(dateBindingValidation.message) else null

            val timeBindingValidation = EmptyValidator(timeBinding).validate()
            binding.timeTextInputLayout.error = if (!timeBindingValidation.isSuccess) getString(timeBindingValidation.message) else null

            if (incomeSubGroupNameBindingValidation.isSuccess
                && amountBindingValidation.isSuccess
                && walletNameBindingValidation.isSuccess
                && dateBindingValidation.isSuccess
                && timeBindingValidation.isSuccess) {
                addIncomeHistoryViewModel.addIncomeHistory(
                    incomeSubGroupNameBinding,
                    amountBinding.toDouble(),
                    commentBinding,
                    fullDateTime,
                    walletNameBinding
                )

                sharedModViewModel.set(null)
                findNavController().navigate(R.id.action_navigation_add_income_to_navigation_home)
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

            val dateFormat = "dd/MM/yyyy"
            val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.US)

            binding.dateEditText.setText(simpleDateFormat.format(selectedDate.time))
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

    private fun getIncomeGroupItemsForSpinner(incomeGroupList: List<IncomeGroup>?): ArrayList<String> {
        val spinnerItems = ArrayList<String>()

        incomeGroupList?.forEach { it ->
            spinnerItems.add(it.name)
        }
        spinnerItems.add(Constants.ADD_NEW_INCOME_GROUP)

        return spinnerItems
    }

    private fun setGroupAndSubGroupSpinnerAdapter() {
        addIncomeHistoryViewModel.getAllIncomeGroupNotArchived().observe(viewLifecycleOwner) { incomeGroups ->
            val spinnerGroupItems = getIncomeGroupItemsForSpinner(incomeGroups)

            val groupSpinnerAdapter = SpinnerAdapter(requireContext(), R.layout.item_with_del, spinnerGroupItems, Constants.ADD_NEW_INCOME_GROUP, archiveGroupListener)
            groupSpinnerAdapterGlobal = groupSpinnerAdapter

            binding.incomeGroupSpinner.setAdapter(groupSpinnerAdapter)

            setIfAvailableGroupSpinnersValue(groupSpinnerAdapter)

            setSubGroupSpinnerAdapter()
        }
    }

    private fun setSubGroupSpinnerAdapter() {
        val subGroupSpinnerAdapter = getEmptySubGroupSpinnerAdapter()

        binding.incomeSubGroupSpinner.setAdapter(subGroupSpinnerAdapter)

        setIfAvailableSubGroupSpinnersValue(subGroupSpinnerAdapter)
    }

    private fun getEmptySubGroupSpinnerAdapter(): SpinnerAdapter {
        val subGroupSpinnerItems = ArrayList<String>()
        subGroupSpinnerItems.add(Constants.ADD_NEW_INCOME_SUB_GROUP)

        val subGroupAdapter = SpinnerAdapter(requireContext(), R.layout.item_with_del, subGroupSpinnerItems, Constants.ADD_NEW_INCOME_GROUP, archiveSubGroupListener)
        subGroupSpinnerAdapterGlobal = subGroupAdapter

        return subGroupAdapter
    }

    private fun setIncomeGroupSpinnerOnClickListener() {
        binding.incomeGroupSpinner.setOnItemClickListener {
                parent, view, position, rowId ->

            resetSubGroupSpinner()

            groupSpinnerPositionGlobal = position

            val selectedGroupName =
                binding.incomeGroupSpinner.text.toString()

            if (selectedGroupName == Constants.ADD_NEW_INCOME_GROUP) {
                setAddIncomeFormBeforeAddIncomeGroup()

                val action =
                    AddIncomeHistoryFragmentDirections.actionNavigationAddIncomeToNavigationAddNewIncomeGroup()
                findNavController().navigate(action)
            }

            // TODO: getIncomeGroupNotArchivedWithIncomeSubGroupsNotArchivedByIncomeGroupNameLiveData doesn't work
            addIncomeHistoryViewModel.getIncomeGroupNotArchivedWithIncomeSubGroupsNotArchivedByIncomeGroupNameLiveData(selectedGroupName)
                .observe(viewLifecycleOwner) { incomeGroupWithIncomeSubGroups ->
                    val spinnerSubItems = getSpinnerSubItemsNotArchived(incomeGroupWithIncomeSubGroups)
                    val adapter = SpinnerAdapter(requireContext(), R.layout.item_with_del, spinnerSubItems, Constants.ADD_NEW_INCOME_SUB_GROUP, archiveSubGroupListener)

                    subGroupSpinnerAdapterGlobal = adapter
                    binding.incomeSubGroupSpinner.setAdapter(adapter)
                }
        }
    }

    private fun setIncomeSubGroupSpinnerListener() {
        binding.incomeSubGroupSpinner.setOnItemClickListener {
                parent, view, position, rowId ->
            subGroupSpinnerPositionGlobal = position
            val selectedIncomeSubGroupName = binding.incomeSubGroupSpinner.text.toString()

            if (selectedIncomeSubGroupName == Constants.ADD_NEW_INCOME_SUB_GROUP) {
                setAddIncomeFormBeforeAddingIncomeSubGroup()

                val action = AddIncomeHistoryFragmentDirections.actionNavigationAddIncomeToNavigationAddNewSubIncomeGroup()
                action.incomeGroupName = binding.incomeGroupSpinner.text.toString()
                findNavController().navigate(action)
            }
        }
    }

    private fun setIfAvailableGroupSpinnersValue(
        groupSpinnerAdapter: SpinnerAdapter
    ) {
        val groupNameArg = args.incomeGroupName
        if (groupNameArg != null && groupNameArg.isNotBlank()) {
            val spinnerPosition = groupSpinnerAdapter.getPosition(groupNameArg)

            val groupName = groupSpinnerAdapter.getItem(spinnerPosition)

            binding.incomeGroupSpinner.setText(groupName.toString())
        } else {
            restoreGroupSpinnerValue(groupSpinnerAdapter)
        }
    }

    private fun restoreGroupSpinnerValue( // need to save before moving to retrieve value here
        groupSpinnerAdapter: SpinnerAdapter?
    ) {
        val mod = sharedModViewModel.modelForm
        binding.incomeGroupSpinner.setText(mod?.groupSpinnerPosition?.let {
            groupSpinnerAdapter?.getItem(
                it
            )
        })
    }

    private fun setIfAvailableSubGroupSpinnersValue(subGroupSpinnerAdapter: SpinnerAdapter?) {
        val groupName = args.incomeGroupName
        val subGroupName = args.incomeSubGroupName

        if (groupName != null
            && groupName.isNotBlank()
            && subGroupName != null
            && subGroupName.isNotBlank()) {
            addIncomeHistoryViewModel.getIncomeGroupNotArchivedWithIncomeSubGroupsNotArchivedByIncomeGroupNameLiveData(groupName)
                .observe(viewLifecycleOwner) { incomeGroupWithIncomeSubGroups ->
                    val spinnerSubItems = getSpinnerSubItemsNotArchived(incomeGroupWithIncomeSubGroups)

                    val subGroupAdapter = SpinnerAdapter(requireContext(), R.layout.item_with_del, spinnerSubItems, Constants.ADD_NEW_INCOME_SUB_GROUP, archiveSubGroupListener)
                    subGroupSpinnerAdapterGlobal = subGroupAdapter

                    val subGroupSpinnerPosition = subGroupAdapter.getPosition(subGroupName)
                    subGroupSpinnerPositionGlobal = subGroupSpinnerPosition

                    binding.incomeSubGroupSpinner.setAdapter(subGroupAdapter)
                    binding.incomeSubGroupSpinner.setText(subGroupAdapter.getItem(subGroupSpinnerPosition))
                }
        } else {
            restoreSubGroupSpinnerValue(subGroupSpinnerAdapter)
        }
    }

    private fun restoreSubGroupSpinnerValue(subGroupSpinnerAdapter: SpinnerAdapter?) {
        val mod = sharedModViewModel.modelForm
        binding.incomeSubGroupSpinner.setText(mod?.subGroupSpinnerPosition?.let {
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

        if (mod?.time != null) {
            binding.timeEditText.setText(mod.time)
        }

        if (mod?.comment != null) {
            binding.commentEditText.setText(mod.comment)
        }
    }

    private fun getSpinnerSubItemsNotArchived(incomeGroupWithIncomeSubGroups: IncomeGroupWithIncomeSubGroups?): ArrayList<String> {
        val spinnerSubItems = ArrayList<String>()

        incomeGroupWithIncomeSubGroups?.incomeSubGroups?.forEach {
            if (it.archivedDate == null) {
                spinnerSubItems.add(it.name)
            }
        }

        spinnerSubItems.add(Constants.ADD_NEW_INCOME_SUB_GROUP)

        return spinnerSubItems
    }

    private fun setWalletSpinnerAdapter() {
        addIncomeHistoryViewModel.walletsNotArchivedLiveData.observe(viewLifecycleOwner) { walletList ->

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
                setAddIncomeFormBeforeAddingWallet()

                val action = AddIncomeHistoryFragmentDirections.actionNavigationAddIncomeToNavigationAddWallet()
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

    private fun setAddIncomeFormBeforeAddIncomeGroup() {
        val amountBinding = binding.amountEditText.text.toString().trim()
        val dateBinding = binding.dateEditText.text.toString().trim()
        val timeBinding = binding.timeEditText.text.toString().trim()
        val commentBinding = binding.commentEditText.text.toString().trim()

        val addTransactionForm = AddTransactionForm(
            walletSpinnerPosition = walletSpinnerPositionGlobal,
            amount = amountBinding,
            date = dateBinding,
            time = timeBinding,
            comment = commentBinding
        )
        sharedModViewModel.set(addTransactionForm)
    }

    private fun getFullDateTime(): String {
        val dateBinding = binding.dateEditText.text.toString().trim()
        val timeBinding = binding.timeEditText.text.toString().trim()

        return dateBinding.plus(" ").plus(timeBinding)
    }

    private fun setAddIncomeFormBeforeAddingIncomeSubGroup() {
        val amountBinding = binding.amountEditText.text.toString().trim()
        val dateBinding = binding.dateEditText.text.toString().trim()
        val timeBinding = binding.timeEditText.text.toString().trim()
        val commentBinding = binding.commentEditText.text.toString().trim()

        val addTransactionForm = AddTransactionForm(
            groupSpinnerPosition = groupSpinnerPositionGlobal,
            walletSpinnerPosition = walletSpinnerPositionGlobal,
            amount = amountBinding,
            date = dateBinding,
            time = timeBinding,
            comment = commentBinding
        )
        sharedModViewModel.set(addTransactionForm)
    }

    private fun setAddIncomeFormBeforeAddingWallet() {
        val amountBinding = binding.amountEditText.text.toString().trim()
        val dateBinding = binding.dateEditText.text.toString().trim()
        val timeBinding = binding.timeEditText.text.toString().trim()
        val commentBinding = binding.commentEditText.text.toString().trim()

        val addTransactionForm = AddTransactionForm(
            groupSpinnerPosition = groupSpinnerPositionGlobal,
            subGroupSpinnerPosition = subGroupSpinnerPositionGlobal,
            amount = amountBinding,
            comment = commentBinding,
            date = dateBinding,
            time = timeBinding
        )
        sharedModViewModel.set(addTransactionForm)
    }

    private fun resetSubGroupSpinner() {
        subGroupSpinnerPositionGlobal = null
        binding.incomeSubGroupSpinner.isVisible = true
        binding.incomeSubGroupSpinner.text = null
    }

    private fun markButtonDisable(button: Button) {
        button.isEnabled = false
        button.setTextColor(ContextCompat.getColor(binding.addIncomeHistoryButton.context, R.color.white))
        button.setBackgroundColor(ContextCompat.getColor(binding.addIncomeHistoryButton.context, R.color.black))
    }

    fun showWalletDialog(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Title")

        val input = EditText(requireContext())
        input.hint = "Enter Text"
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
            // Here you get get input text from the Edittext
            var m_Text = input.text.toString()
        })
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

        builder.show()
    }
}
