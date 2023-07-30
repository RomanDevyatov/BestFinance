package com.romandevyatov.bestfinance.ui.fragments.add.income

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
import com.romandevyatov.bestfinance.databinding.FragmentAddIncomeHistoryBinding
import com.romandevyatov.bestfinance.data.entities.IncomeGroup
import com.romandevyatov.bestfinance.data.entities.Wallet
import com.romandevyatov.bestfinance.data.entities.relations.IncomeGroupWithIncomeSubGroups
import com.romandevyatov.bestfinance.data.roomdb.converters.LocalDateTimeRoomTypeConverter.Companion.dateFormat
import com.romandevyatov.bestfinance.data.roomdb.converters.LocalDateTimeRoomTypeConverter.Companion.dateTimeFormatter
import com.romandevyatov.bestfinance.data.roomdb.converters.LocalDateTimeRoomTypeConverter.Companion.timeFormat
import com.romandevyatov.bestfinance.ui.adapters.spinner.SpinnerAdapter
import com.romandevyatov.bestfinance.data.validation.EmptyValidator
import com.romandevyatov.bestfinance.data.validation.IsDigitValidator
import com.romandevyatov.bestfinance.data.validation.base.BaseValidator
import com.romandevyatov.bestfinance.utils.Constants.ADD_INCOME_HISTORY_FRAGMENT
import com.romandevyatov.bestfinance.utils.Constants.ADD_NEW_INCOME_GROUP
import com.romandevyatov.bestfinance.utils.Constants.ADD_NEW_INCOME_SUB_GROUP
import com.romandevyatov.bestfinance.utils.Constants.ADD_NEW_WALLET
import com.romandevyatov.bestfinance.viewmodels.*
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.AddIncomeHistoryViewModel
import com.romandevyatov.bestfinance.viewmodels.shared.SharedModifiedViewModel
import com.romandevyatov.bestfinance.viewmodels.shared.models.AddTransactionForm
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class AddIncomeHistoryFragment : Fragment() {

    private var _binding: FragmentAddIncomeHistoryBinding? = null
    private val binding get() = _binding!!

    private val addHistoryViewModel: AddIncomeHistoryViewModel by viewModels()

    private val sharedModViewModel: SharedModifiedViewModel<AddTransactionForm> by activityViewModels()

    private var groupSpinnerValueGlobalBeforeAdd: String? = null
    private var subGroupSpinnerValueGlobalBeforeAdd: String? = null
    private var walletSpinnerValueGlobalBeforeAdd: String? = null

    private val args: AddIncomeHistoryFragmentArgs by navArgs()

    private val archiveGroupListener =
        object : SpinnerAdapter.DeleteItemClickListener {

            @RequiresApi(Build.VERSION_CODES.O)
            override fun archive(name: String) {
                addHistoryViewModel.archiveIncomeGroup(name)
                if (binding.groupSpinner.text.toString() == name) {
                    resetSubGroupSpinner()
                    binding.groupSpinner.text = null
                    groupSpinnerValueGlobalBeforeAdd = null
                }
                sharedModViewModel.set(null)

                dismissAndDropdownSpinner(binding.groupSpinner)
            }
        }

    private val archiveSubGroupListener =
        object : SpinnerAdapter.DeleteItemClickListener {

            @RequiresApi(Build.VERSION_CODES.O)
            override fun archive(name: String) {
                addHistoryViewModel.archiveIncomeSubGroup(name)
                if (binding.subGroupSpinner.text.toString() == name) {
                    binding.subGroupSpinner.text = null
                    subGroupSpinnerValueGlobalBeforeAdd = null
                }
                sharedModViewModel.set(null)

                dismissAndDropdownSpinner(binding.subGroupSpinner)
            }
        }

    private val archiveWalletListener =
        object : SpinnerAdapter.DeleteItemClickListener {

            @RequiresApi(Build.VERSION_CODES.O)
            override fun archive(name: String) {
                addHistoryViewModel.archiveWallet(name)
                if (binding.walletSpinner.text.toString() == name) {
                    binding.walletSpinner.text = null
                    walletSpinnerValueGlobalBeforeAdd = null
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
        _binding = FragmentAddIncomeHistoryBinding.inflate(inflater, container, false)
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
                findNavController().navigate(R.id.action_navigation_add_income_to_navigation_home)
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
        binding.addHistoryButton.setOnClickListener {
            val subGroupNameBinding = binding.subGroupSpinner.text.toString()
            val amountBinding = binding.amountEditText.text.toString().trim()
            val commentBinding = binding.commentEditText.text.toString().trim()
            val walletNameBinding = binding.walletSpinner.text.toString()
            val dateBinding = binding.dateEditText.text.toString().trim()
            val timeBinding = binding.timeEditText.text.toString().trim()

            val subGroupNameBindingValidation = EmptyValidator(subGroupNameBinding).validate()
            binding.subGroupSpinnerLayout.error = if (!subGroupNameBindingValidation.isSuccess) getString(subGroupNameBindingValidation.message) else null

            val amountBindingValidation = BaseValidator.validate(EmptyValidator(amountBinding), IsDigitValidator(amountBinding))
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

                addHistoryViewModel.addIncomeHistory(
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
        findNavController().navigate(R.id.action_navigation_add_income_to_navigation_home)
    }

    private fun setGroupAndSubGroupSpinnerAdapter() {
        addHistoryViewModel.getAllIncomeGroupNotArchived().observe(viewLifecycleOwner) { groups ->
            val spinnerGroupItems = getGroupItemsForSpinner(groups)

            val groupSpinnerAdapter = SpinnerAdapter(
                requireContext(),
                R.layout.item_with_del,
                spinnerGroupItems,
                ADD_NEW_INCOME_GROUP,
                archiveGroupListener)

            binding.groupSpinner.setAdapter(groupSpinnerAdapter)

            setIfAvailableGroupSpinnersValue(groupSpinnerAdapter)

            setSubGroupSpinnerAdapter()
        }
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
        addHistoryViewModel.getIncomeGroupNotArchivedWithIncomeSubGroupsNotArchivedByIncomeGroupNameLiveData(
            groupSpinnerBinding
        ).observe(viewLifecycleOwner) { groupWithSubGroups ->
            val spinnerSubItems =
                getSpinnerSubItemsNotArchived(groupWithSubGroups)

            val subGroupSpinnerAdapter =
                SpinnerAdapter(requireContext(), R.layout.item_with_del, spinnerSubItems, ADD_NEW_INCOME_SUB_GROUP, archiveSubGroupListener)

            binding.subGroupSpinner.setAdapter(subGroupSpinnerAdapter)

            setIfAvailableSubGroupSpinnersValue(subGroupSpinnerAdapter)
        }
    }

    private fun setWalletSpinnerAdapter() {
        addHistoryViewModel.walletsNotArchivedLiveData.observe(viewLifecycleOwner) { wallets ->

            val spinnerWalletItems = getWalletItemsForSpinner(wallets)

            val walletSpinnerAdapter =
                SpinnerAdapter(requireContext(), R.layout.item_with_del, spinnerWalletItems, ADD_NEW_WALLET, archiveWalletListener)

            binding.walletSpinner.setAdapter(walletSpinnerAdapter)

            setIfAvailableWalletSpinnerValue(walletSpinnerAdapter)
        }
    }

    private fun setEmptySubGroupSpinnerAdapter() {
        val emptySubGroupSpinnerAdapter = getEmptySubGroupSpinnerAdapter()

        binding.subGroupSpinner.setAdapter(emptySubGroupSpinnerAdapter)
    }

    private fun getEmptySubGroupSpinnerAdapter(): SpinnerAdapter {
        val subGroupSpinnerItems = ArrayList<String>()

        subGroupSpinnerItems.add(ADD_NEW_INCOME_SUB_GROUP)

        return SpinnerAdapter(
            requireContext(),
            R.layout.item_with_del,
            subGroupSpinnerItems,
            ADD_NEW_INCOME_GROUP,
            archiveSubGroupListener
        )
    }

    private fun setGroupSpinnerOnClickListener() {
        binding.groupSpinner.setOnItemClickListener {
                _, _, position, _ ->

            val selectedGroupName =
                binding.groupSpinner.text.toString()

            if (selectedGroupName != groupSpinnerValueGlobalBeforeAdd) {
                resetSubGroupSpinner()
            }

            if (selectedGroupName == ADD_NEW_INCOME_GROUP) {
                setPrevValue(groupSpinnerValueGlobalBeforeAdd, binding.groupSpinner)

                saveAddTransactionForm()

                val action =
                    AddIncomeHistoryFragmentDirections.actionNavigationAddIncomeToNavigationAddIncomeGroup()
                findNavController().navigate(action)
            } else {
                // TODO: getIncomeGroupNotArchivedWithIncomeSubGroupsNotArchivedByIncomeGroupNameLiveData doesn't work
                addHistoryViewModel.getIncomeGroupNotArchivedWithIncomeSubGroupsNotArchivedByIncomeGroupNameLiveData(
                    selectedGroupName
                ).observe(viewLifecycleOwner) { groupWithSubGroups ->
                    val spinnerSubItems = getSpinnerSubItemsNotArchived(groupWithSubGroups)
                    val subGroupSpinnerAdapter =
                        SpinnerAdapter(requireContext(), R.layout.item_with_del, spinnerSubItems, ADD_NEW_INCOME_SUB_GROUP, archiveSubGroupListener)

                    binding.subGroupSpinner.setAdapter(subGroupSpinnerAdapter)
                }

                groupSpinnerValueGlobalBeforeAdd = selectedGroupName
            }
        }
    }

    private fun setPrevValue(value: String?, spinner: AutoCompleteTextView) {
        spinner.setText(value, false)
    }

    private fun setSubGroupSpinnerOnClickListener() {
        binding.subGroupSpinner.setOnItemClickListener {
                _, _, position, _ ->

            val selectedSubGroupName = binding.subGroupSpinner.text.toString()

            if (selectedSubGroupName == ADD_NEW_INCOME_SUB_GROUP) {
                setPrevValue(subGroupSpinnerValueGlobalBeforeAdd, binding.subGroupSpinner)

                saveAddTransactionForm()

                val action = AddIncomeHistoryFragmentDirections.actionNavigationAddIncomeToNavigationAddSubIncomeGroup()
                action.incomeGroupName = binding.groupSpinner.text.toString()
                findNavController().navigate(action)
            } else {
                subGroupSpinnerValueGlobalBeforeAdd = selectedSubGroupName
            }
        }
    }

    private fun setWalletSpinnerOnItemClickListener() {
        binding.walletSpinner.setOnItemClickListener {
                _, _, position, _ ->

            val selectedWalletName = binding.walletSpinner.text.toString()

            if (selectedWalletName == ADD_NEW_WALLET) {
                setPrevValue(walletSpinnerValueGlobalBeforeAdd, binding.walletSpinner)

                saveAddTransactionForm()

                val action = AddIncomeHistoryFragmentDirections.actionNavigationAddIncomeToNavigationAddWallet()
                action.source = ADD_INCOME_HISTORY_FRAGMENT
                action.spinnerType = null
                findNavController().navigate(action)
            } else {
                walletSpinnerValueGlobalBeforeAdd = selectedWalletName
            }
        }
    }

    private fun setIfAvailableGroupSpinnersValue(groupSpinnerAdapter: SpinnerAdapter) {
        val savedGroupName = args.incomeGroupName ?: sharedModViewModel.modelForm?.groupSpinnerValue

        if (savedGroupName?.isNotBlank() == true) {
            resetSubGroupSpinner()

            if (isNameInAdapter(groupSpinnerAdapter, savedGroupName)) {
                groupSpinnerValueGlobalBeforeAdd = savedGroupName

                binding.groupSpinner.setText(savedGroupName, false)
            }
        }
    }

    private fun setIfAvailableSubGroupSpinnersValue(subGroupSpinnerAdapter: SpinnerAdapter) {
        val savedSubGroupName = args.incomeSubGroupName ?: sharedModViewModel.modelForm?.subGroupSpinnerValue

        if (savedSubGroupName?.isNotBlank() == true && isNameInAdapter(subGroupSpinnerAdapter, savedSubGroupName)) {
            subGroupSpinnerValueGlobalBeforeAdd = savedSubGroupName

            binding.subGroupSpinner.setText(savedSubGroupName, false)
        }
    }

    private fun setIfAvailableWalletSpinnerValue(walletSpinnerAdapter: SpinnerAdapter) {
        val savedWalletName = args.walletName ?: sharedModViewModel.modelForm?.walletSpinnerValue

        if (savedWalletName?.isNotBlank() == true && isNameInAdapter(walletSpinnerAdapter, savedWalletName)) {
            walletSpinnerValueGlobalBeforeAdd = savedWalletName

            binding.walletSpinner.setText(savedWalletName, false)
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

    private fun getGroupItemsForSpinner(groups: List<IncomeGroup>?): ArrayList<String> {
        val spinnerItems = ArrayList<String>()

        groups?.forEach { it ->
            spinnerItems.add(it.name)
        }
        spinnerItems.add(ADD_NEW_INCOME_GROUP)

        return spinnerItems
    }

    private fun getSpinnerSubItemsNotArchived(groupWithSubGroups: IncomeGroupWithIncomeSubGroups?): ArrayList<String> {
        val spinnerSubItems = ArrayList<String>()

        groupWithSubGroups?.incomeSubGroups?.forEach {
            if (it.archivedDate == null) {
                spinnerSubItems.add(it.name)
            }
        }

        spinnerSubItems.add(ADD_NEW_INCOME_SUB_GROUP)

        return spinnerSubItems
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

    private fun saveAddTransactionForm() {
        val amountBinding = binding.amountEditText.text.toString().trim()
        val dateBinding = binding.dateEditText.text.toString().trim()
        val timeBinding = binding.timeEditText.text.toString().trim()
        val commentBinding = binding.commentEditText.text.toString().trim()

        var isSend = true
        if (amountBinding.isNotBlank()) {
            val amountBindingValidation = IsDigitValidator(amountBinding).validate()
            binding.amountLayout.error =
                if (!amountBindingValidation.isSuccess) getString(amountBindingValidation.message) else null

            if (!amountBindingValidation.isSuccess) {
                isSend = false
            }
        }

        if (isSend) {
            val addTransactionForm = AddTransactionForm(
                groupSpinnerValue = groupSpinnerValueGlobalBeforeAdd,
                subGroupSpinnerValue = subGroupSpinnerValueGlobalBeforeAdd,
                walletSpinnerValue = walletSpinnerValueGlobalBeforeAdd,
                amount = amountBinding,
                date = dateBinding,
                time = timeBinding,
                comment = commentBinding
            )
            sharedModViewModel.set(addTransactionForm)
        }
    }

    private fun resetSubGroupSpinner() {
        binding.subGroupSpinner.text = null
    }

}
