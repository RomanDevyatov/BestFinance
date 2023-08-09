package com.romandevyatov.bestfinance.ui.fragments.update.history

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.data.entities.IncomeGroup
import com.romandevyatov.bestfinance.data.entities.IncomeHistory
import com.romandevyatov.bestfinance.data.entities.Wallet
import com.romandevyatov.bestfinance.data.entities.relations.IncomeGroupWithIncomeSubGroups
import com.romandevyatov.bestfinance.data.roomdb.converters.LocalDateTimeRoomTypeConverter
import com.romandevyatov.bestfinance.data.validation.EmptyValidator
import com.romandevyatov.bestfinance.data.validation.IsDigitValidator
import com.romandevyatov.bestfinance.data.validation.base.BaseValidator
import com.romandevyatov.bestfinance.databinding.FragmentUpdateIncomeHistoryBinding
import com.romandevyatov.bestfinance.ui.adapters.spinner.GroupSpinnerAdapter
import com.romandevyatov.bestfinance.ui.adapters.spinner.SpinnerAdapter
import com.romandevyatov.bestfinance.ui.adapters.spinner.SpinnerItem
import com.romandevyatov.bestfinance.utils.Constants
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.UpdateIncomeHistoryViewModel
import com.romandevyatov.bestfinance.viewmodels.shared.SharedModifiedViewModel
import com.romandevyatov.bestfinance.viewmodels.shared.models.AddTransactionForm
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.util.*

@AndroidEntryPoint
class UpdateIncomeHistoryFragment : Fragment() {
    private var _binding: FragmentUpdateIncomeHistoryBinding? = null

    private val binding get() = _binding!!

    private val updateIncomeHistoryViewModel: UpdateIncomeHistoryViewModel by viewModels()

    private val sharedModViewModel: SharedModifiedViewModel<AddTransactionForm> by activityViewModels()

    private var groupSpinnerValueGlobalBeforeAdd: String? = null

    private var incomeHistoryGlobal: IncomeHistory? = null
    private var incomeGroupGlobal: IncomeGroup? = null


    private var spinnerGroupItemsGlobal: MutableList<SpinnerItem>? = null
    private var spinnerSubGroupItemsGlobal: MutableList<SpinnerItem>? = null
    private var walletSpinnerItemsGlobal: MutableList<SpinnerItem>? = null

    private val clickDelay = 1000 // Set the delay time in milliseconds
    private var isButtonClickable = true

    private val args: UpdateIncomeHistoryFragmentArgs by navArgs()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateIncomeHistoryBinding.inflate(inflater, container, false)

        binding.reusable.addHistoryButton.text = "Update"

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        setDateEditText()
//        setTimeEditText()

        updateIncomeHistoryViewModel.getIncomeHistoryByIdLiveData(args.incomeHistoryId)
            ?.observe(viewLifecycleOwner) { incomeHistory ->
                incomeHistoryGlobal = IncomeHistory(
                    incomeHistory.id,
                    incomeHistory.incomeSubGroupId,
                    incomeHistory.amount,
                    incomeHistory.comment,
                    incomeHistory.date,
                    incomeHistory.walletId,
                    incomeHistory.archivedDate,
                    incomeHistory.createdDate
                )

                binding.reusable.commentEditText.setText(incomeHistory.comment)
                binding.reusable.amountEditText.setText(incomeHistory.amount.toString())

                updateIncomeHistoryViewModel.getIncomeSubGroupByIdLiveData(incomeHistory.incomeSubGroupId)
                    ?.observe(viewLifecycleOwner) { incomeSubGroup ->
                        binding.reusable.subGroupSpinner.setText(incomeSubGroup.name, false)
                        updateIncomeHistoryViewModel.getIncomeGroupById(incomeSubGroup.incomeGroupId)?.observe(viewLifecycleOwner) { incomeGroup ->
                            binding.reusable.groupSpinner.setText(incomeGroup.name, false)

                            setSpinners()

                            setDateEditText()
                            setTimeEditText()

                            setButtonOnClickListener(view)
                        }
                    }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val callback = object : OnBackPressedCallback(true) {
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
        selectedDate.timeInMillis =
            incomeHistoryGlobal?.date?.atZone(java.time.ZoneId.systemDefault())?.toInstant()?.toEpochMilli()!!
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
            incomeHistoryGlobal?.date?.atZone(java.time.ZoneId.systemDefault())?.toInstant()?.toEpochMilli()!!

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
        binding.reusable.addHistoryButton.setOnClickListener {
            if (!isButtonClickable) return@setOnClickListener
            isButtonClickable = false
            view.isEnabled = false

            val subGroupNameBinding = binding.reusable.subGroupSpinner.text.toString()
            val amountBinding = binding.reusable.amountEditText.text.toString().trim()
            val commentBinding = binding.reusable.commentEditText.text.toString().trim()
            val walletNameBinding = binding.reusable.walletSpinner.text.toString()
            val dateBinding = binding.reusable.dateEditText.text.toString().trim()
            val timeBinding = binding.reusable.timeEditText.text.toString().trim()

            val subGroupNameBindingValidation = EmptyValidator(subGroupNameBinding).validate()
            binding.reusable.subGroupSpinnerLayout.error = if (!subGroupNameBindingValidation.isSuccess) getString(subGroupNameBindingValidation.message) else null

            val amountBindingValidation = BaseValidator.validate(EmptyValidator(amountBinding), IsDigitValidator(amountBinding))
            binding.reusable.amountLayout.error = if (!amountBindingValidation.isSuccess) getString(amountBindingValidation.message) else null

            val walletNameBindingValidation = EmptyValidator(walletNameBinding).validate()
            binding.reusable.walletSpinnerLayout.error = if (!walletNameBindingValidation.isSuccess) getString(walletNameBindingValidation.message) else null

            val dateBindingValidation = EmptyValidator(dateBinding).validate()
            binding.reusable.dateLayout.error = if (!dateBindingValidation.isSuccess) getString(dateBindingValidation.message) else null

            val timeBindingValidation = EmptyValidator(timeBinding).validate()
            binding.reusable.timeLayout.error = if (!timeBindingValidation.isSuccess) getString(timeBindingValidation.message) else null

            if (subGroupNameBindingValidation.isSuccess
                && amountBindingValidation.isSuccess
                && walletNameBindingValidation.isSuccess
                && dateBindingValidation.isSuccess
                && timeBindingValidation.isSuccess) {

                val fullDateTime = dateBinding.plus(" ").plus(timeBinding)
                val parsedLocalDateTime = LocalDateTime.from(LocalDateTimeRoomTypeConverter.dateTimeFormatter.parse(fullDateTime))

                updateIncomeHistoryViewModel.updateIncomeHistory(
                    IncomeHistory(
                        id = incomeHistoryGlobal?.id,
                        incomeSubGroupId = spinnerSubGroupItemsGlobal?.find { it.name == subGroupNameBinding }?.id!!,
                        amount = amountBinding.toDouble(),
                        comment = commentBinding,
                        date = parsedLocalDateTime,
                        walletId = walletSpinnerItemsGlobal?.find { it.name == walletNameBinding }?.id!!,
                        archivedDate = incomeHistoryGlobal?.archivedDate,
                        createdDate = incomeHistoryGlobal?.createdDate
                ))

                navigateToHistory()
            }

            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                isButtonClickable = true
                view.isEnabled = true
            }, clickDelay.toLong())
        }
    }

    private fun navigateToHistory() {
        findNavController().navigate(R.id.action_update_income_history_fragment_to_history_fragment)
    }

    private fun setGroupAndSubGroupSpinnerAdapter() {
        updateIncomeHistoryViewModel.getAllIncomeGroupNotArchived().observe(viewLifecycleOwner) { groups ->
            val spinnerGroupItems = getGroupItemsForSpinner(groups)

            val groupSpinnerAdapter = GroupSpinnerAdapter(
                requireContext(),
                R.layout.item_with_del,
                spinnerGroupItems,
                null,
                null)

            binding.reusable.groupSpinner.setAdapter(groupSpinnerAdapter)

            setSubGroupSpinnerAdapter()
        }
    }

    private fun setSubGroupSpinnerAdapter() {
        val groupSpinnerBinding = binding.reusable.groupSpinner.text.toString()

        if (groupSpinnerBinding.isNotBlank()) {
            setSubGroupSpinnerAdapterByGroupName(groupSpinnerBinding)
        } else {
            setEmptySubGroupSpinnerAdapter()
        }
    }

    private fun setSubGroupSpinnerAdapterByGroupName(groupSpinnerBinding: String) {
        updateIncomeHistoryViewModel.getIncomeGroupNotArchivedWithIncomeSubGroupsNotArchivedByIncomeGroupNameLiveData(
            groupSpinnerBinding
        ).observe(viewLifecycleOwner) { groupWithSubGroups ->
            val spinnerSubItems =
                getSpinnerSubItemsNotArchived(groupWithSubGroups)

            spinnerSubGroupItemsGlobal = spinnerSubItems

            val subGroupSpinnerAdapter =
                GroupSpinnerAdapter(requireContext(), R.layout.item_with_del, spinnerSubItems, null, null)

            binding.reusable.subGroupSpinner.setAdapter(subGroupSpinnerAdapter)

        }
    }

    private fun setIfAvailableSubGroupSpinnersValue() {
        val name = spinnerSubGroupItemsGlobal?.find { it.id == incomeHistoryGlobal?.incomeSubGroupId }?.name
        binding.reusable.walletSpinner.setText(name, false)
    }

    private fun setWalletSpinnerAdapter() {
        updateIncomeHistoryViewModel.walletsNotArchivedLiveData.observe(viewLifecycleOwner) { wallets ->

            val spinnerWalletItems = getWalletItemsForSpinner(wallets)

            val walletSpinnerAdapter =
                GroupSpinnerAdapter(requireContext(), R.layout.item_with_del, spinnerWalletItems, null, null)

            binding.reusable.walletSpinner.setAdapter(walletSpinnerAdapter)

            setIfAvailableWalletSpinnerValue()
        }
    }

    private fun setIfAvailableWalletSpinnerValue() {
        val name = walletSpinnerItemsGlobal?.find { it.id == incomeHistoryGlobal?.walletId }?.name
        binding.reusable.walletSpinner.setText(name, false)
    }

    private fun isNameInAdapter(subGroupSpinnerAdapter: SpinnerAdapter, savedSubGroupName: String?): Boolean {
        return subGroupSpinnerAdapter.getPosition(savedSubGroupName) > -1
    }

    private fun setEmptySubGroupSpinnerAdapter() {
        val emptySubGroupSpinnerAdapter: GroupSpinnerAdapter = getEmptySubGroupSpinnerAdapter()

        binding.reusable.subGroupSpinner.setAdapter(emptySubGroupSpinnerAdapter)
    }

    private fun getEmptySubGroupSpinnerAdapter(): GroupSpinnerAdapter {
        val subGroupSpinnerItems : MutableList<SpinnerItem> = mutableListOf()

        return GroupSpinnerAdapter(
            requireContext(),
            R.layout.item_with_del,
            subGroupSpinnerItems,
            null,
            null
        )
    }

    private fun setGroupSpinnerOnClickListener() {
        binding.reusable.groupSpinner.setOnItemClickListener {
                _, _, position, _ ->

            val selectedGroupName =
                binding.reusable.groupSpinner.text.toString()

            if (selectedGroupName != groupSpinnerValueGlobalBeforeAdd) {
                resetSubGroupSpinner()
            }
        }
    }

    private fun setSubGroupSpinnerOnClickListener() {
        binding.reusable.subGroupSpinner.setOnItemClickListener {
                _, _, position, _ ->

//            val selectedSubGroupName = binding.reusable.subGroupSpinner.text
//            binding.reusable.subGroupSpinner.setText(selectedSubGroupName)
        }
    }

    private fun setWalletSpinnerOnItemClickListener() {
        binding.reusable.walletSpinner.setOnItemClickListener {
                _, _, position, _ ->

            val selectedWalletName = binding.reusable.walletSpinner.text.toString()
        }
    }

    private fun getGroupItemsForSpinner(groups: List<IncomeGroup>?): MutableList<SpinnerItem> {
        val spinnerItems: MutableList<SpinnerItem> = mutableListOf()

        groups?.forEach { it ->
            spinnerItems.add(SpinnerItem(it.id, it.name))
        }

        return spinnerItems
    }

    private fun getSpinnerSubItemsNotArchived(groupWithSubGroups: IncomeGroupWithIncomeSubGroups?): MutableList<SpinnerItem> {
        val spinnerSubItems: MutableList<SpinnerItem> = mutableListOf()

        groupWithSubGroups?.incomeSubGroups?.forEach {
            if (it.archivedDate == null) {
                spinnerSubItems.add(SpinnerItem(it.id, it.name))
            }
        }

        return spinnerSubItems
    }

    private fun getWalletItemsForSpinner(walletList: List<Wallet>?): MutableList<SpinnerItem> {
        val spinnerItems = ArrayList<SpinnerItem>()

        walletList?.forEach { it ->
            spinnerItems.add(SpinnerItem(it.id, it.name))
        }

        walletSpinnerItemsGlobal = spinnerItems

        return spinnerItems
    }

    private fun resetSubGroupSpinner() {
        binding.reusable.subGroupSpinner.text = null
    }
}
