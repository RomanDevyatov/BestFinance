package com.romandevyatov.bestfinance.ui.fragments.update.history

import com.romandevyatov.bestfinance.utils.numberpad.addGenericTextWatcher
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
import com.romandevyatov.bestfinance.data.entities.ExpenseGroupEntity
import com.romandevyatov.bestfinance.data.entities.ExpenseHistoryEntity
import com.romandevyatov.bestfinance.data.entities.ExpenseSubGroupEntity
import com.romandevyatov.bestfinance.data.entities.WalletEntity
import com.romandevyatov.bestfinance.data.entities.relations.ExpenseGroupWithExpenseSubGroups
import com.romandevyatov.bestfinance.data.entities.relations.ExpenseHistoryWithExpenseSubGroupAndWallet
import com.romandevyatov.bestfinance.data.roomdb.converters.LocalDateTimeRoomTypeConverter
import com.romandevyatov.bestfinance.data.roomdb.converters.LocalDateTimeRoomTypeConverter.Companion.dateFormat
import com.romandevyatov.bestfinance.data.roomdb.converters.LocalDateTimeRoomTypeConverter.Companion.timeFormat
import com.romandevyatov.bestfinance.data.validation.EmptyValidator
import com.romandevyatov.bestfinance.data.validation.IsDigitValidator
import com.romandevyatov.bestfinance.data.validation.base.BaseValidator
import com.romandevyatov.bestfinance.data.validation.base.ValidateResult
import com.romandevyatov.bestfinance.databinding.FragmentUpdateExpenseHistoryBinding
import com.romandevyatov.bestfinance.ui.adapters.spinner.GroupSpinnerAdapter
import com.romandevyatov.bestfinance.ui.adapters.spinner.models.SpinnerItem
import com.romandevyatov.bestfinance.utils.*
import com.romandevyatov.bestfinance.utils.TextFormatter.removeTrailingZeros
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.UpdateExpenseHistoryViewModel
import com.romandevyatov.bestfinance.viewmodels.shared.SharedInitialTabIndexViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime

@AndroidEntryPoint
class UpdateExpenseHistoryFragment : Fragment() {

    private var _binding: FragmentUpdateExpenseHistoryBinding? = null
    private val binding get() = _binding!!

    private val updateExpenseHistoryViewModel: UpdateExpenseHistoryViewModel by viewModels()

    private val sharedInitialTabIndexViewModel: SharedInitialTabIndexViewModel by activityViewModels()

    private var prevGroupSpinnerValueGlobal: String? = null

    private lateinit var historyWithSubGroupAndWalletGlobal: ExpenseHistoryWithExpenseSubGroupAndWallet
    private var expenseGroupEntityGlobal: ExpenseGroupEntity? = null

    private var spinnerSubGroupItemsGlobal: MutableList<SpinnerItem>? = null
    private var walletSpinnerItemsGlobal: MutableList<SpinnerItem>? = null

    private var isButtonClickable = true
    private val args: UpdateExpenseHistoryFragmentArgs by navArgs()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateExpenseHistoryBinding.inflate(inflater, container, false)

        binding.reusable.addHistoryButton.text = getString(R.string.update)

        updateExpenseHistoryViewModel.getExpenseHistoryWithExpenseSubGroupAndWalletById(args.expenseHistoryId)
            .observe(viewLifecycleOwner) { historyWithSubGroupAndWallet ->
                historyWithSubGroupAndWallet?.let {
                    historyWithSubGroupAndWalletGlobal = it.copy()

                    setupSpinnersValues(
                        it.expenseSubGroupEntity,
                        it.walletEntity
                    )

                    setupSpinners()

                    setupDateTimeFiledValues()

                    val expenseHistory = it.expenseHistoryEntity
                    binding.reusable.commentEditText.setText(expenseHistory.comment)
                    val formattedAmountText = removeTrailingZeros(expenseHistory.amount.toString())
                    binding.reusable.amountEditText.setText(formattedAmountText)
                }
            }

        BackStackLogger.logBackStack(findNavController())

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.reusable.amountEditText.addGenericTextWatcher()

        setOnBackPressedHandler()

        setupButtonClickListeners(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun deleteRecord() {
        WindowUtil.showDeleteDialog(
            context = requireContext(),
            viewModel = updateExpenseHistoryViewModel,
            message = getString(R.string.delete_confirmation_warning_message, expenseGroupEntityGlobal?.name),
            itemId = args.expenseHistoryId,
            isCountdown = false,
            rootView = binding.root
        ) { navigateToHistory() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupSpinnersValues(expenseSubGroupEntity: ExpenseSubGroupEntity?, walletEntity: WalletEntity?) {
        if (expenseSubGroupEntity != null) {
            setSubGroupSpinnerValue(expenseSubGroupEntity)
            setGroupSpinnerValue(expenseSubGroupEntity.expenseGroupId)
        }

        if (walletEntity != null) {
            setWalletSpinnerValue(walletEntity)
        }
    }

    private fun setSubGroupSpinnerValue(expenseSubGroupEntity: ExpenseSubGroupEntity) {
        binding.reusable.subGroupSpinner.setText(expenseSubGroupEntity.name, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setGroupSpinnerValue(expenseGroupId: Long) {
        updateExpenseHistoryViewModel.getExpenseGroupByIdLiveData(expenseGroupId)
            .observe(viewLifecycleOwner) { expenseGroup ->
                expenseGroup?.let {
                    expenseGroupEntityGlobal = it.copy()

                    binding.reusable.groupSpinner.setText(it.name, false)
                }
            }
    }

    private fun setWalletSpinnerValue(walletEntity: WalletEntity) {
        binding.reusable.walletSpinner.setText(walletEntity.name, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupSpinners() {
        setGroupAndSubGroupSpinnerAdapter()

        setGroupSpinnerOnClickListener()
        setSubGroupSpinnerOnClickListener()

        setWalletSpinnerAdapter()

        setWalletSpinnerOnItemClickListener()
    }

    private fun setGroupAndSubGroupSpinnerAdapter() {
        updateExpenseHistoryViewModel.getAllExpenseGroupNotArchived().observe(viewLifecycleOwner) { expenseGroups ->
            expenseGroups?.let { groups ->
                val spinnerGroupItems = getGroupItemsForSpinner(groups)

                val groupSpinnerAdapter = GroupSpinnerAdapter(
                    requireContext(),
                    R.layout.item_with_del,
                    spinnerGroupItems,
                    null,
                    null
                )

                binding.reusable.groupSpinner.setAdapter(groupSpinnerAdapter)

                setSubGroupSpinnerAdapter()
            }
        }
    }

    private fun setWalletSpinnerAdapter() {
        updateExpenseHistoryViewModel.walletsNotArchivedLiveData.observe(viewLifecycleOwner) { allWallets ->
            allWallets?.let { wallets ->
                val spinnerWalletItems = getWalletItemsForSpinner(wallets)

                val walletSpinnerAdapter =
                    GroupSpinnerAdapter(
                        requireContext(),
                        R.layout.item_with_del,
                        spinnerWalletItems,
                        null,
                        null
                    )

                binding.reusable.walletSpinner.setAdapter(walletSpinnerAdapter)
            }
        }
    }

    private fun setGroupSpinnerOnClickListener() {
        binding.reusable.groupSpinner.setOnItemClickListener {
                _, _, _, _ ->

            val selectedGroupName =
                binding.reusable.groupSpinner.text.toString()

            if (selectedGroupName != prevGroupSpinnerValueGlobal) {
                resetSubGroupSpinner()

                updateExpenseHistoryViewModel.getExpenseGroupNotArchivedWithExpenseSubGroupsNotArchivedByExpenseGroupNameLiveData(
                    selectedGroupName
                ).observe(viewLifecycleOwner) { groupWithSubGroups ->
                    groupWithSubGroups?.let {
                        val spinnerSubItems = getSpinnerSubItemsNotArchived(groupWithSubGroups)

                        spinnerSubGroupItemsGlobal = spinnerSubItems

                        val subGroupSpinnerAdapter =
                            GroupSpinnerAdapter(
                                requireContext(),
                                R.layout.item_with_del,
                                spinnerSubItems,
                                null,
                                null
                            )

                        binding.reusable.subGroupSpinner.setAdapter(subGroupSpinnerAdapter)
                    }
                }
            }

            prevGroupSpinnerValueGlobal = selectedGroupName
        }
    }

    private fun resetSubGroupSpinner() {
        binding.reusable.subGroupSpinner.text = null
    }

    private fun setSubGroupSpinnerOnClickListener() {
        binding.reusable.subGroupSpinner.setOnItemClickListener {
                _, _, _, _ ->
        }
    }

    private fun setWalletSpinnerOnItemClickListener() {
        binding.reusable.walletSpinner.setOnItemClickListener {
                _, _, _, _ ->
        }
    }

    private fun getGroupItemsForSpinner(groups: List<ExpenseGroupEntity>?): MutableList<SpinnerItem> {
        val spinnerItems: MutableList<SpinnerItem> = mutableListOf()

        groups?.forEach { it ->
            spinnerItems.add(SpinnerItem(it.id, it.name))
        }

        return spinnerItems
    }

    private fun getWalletItemsForSpinner(walletEntityList: List<WalletEntity>?): MutableList<SpinnerItem> {
        val spinnerItems: MutableList<SpinnerItem> = mutableListOf()

        walletEntityList?.forEach { it ->
            spinnerItems.add(SpinnerItem(it.id, it.name))
        }

        walletSpinnerItemsGlobal = spinnerItems

        return spinnerItems
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
        updateExpenseHistoryViewModel.getExpenseGroupNotArchivedWithExpenseSubGroupsNotArchivedByExpenseGroupNameLiveData(
            groupSpinnerBinding
        ).observe(viewLifecycleOwner) { groupWithSubGroups ->
            groupWithSubGroups?.let {
                val spinnerSubItems =
                    getSpinnerSubItemsNotArchived(groupWithSubGroups)

                spinnerSubGroupItemsGlobal = spinnerSubItems

                val subGroupSpinnerAdapter =
                    GroupSpinnerAdapter(
                        requireContext(),
                        R.layout.item_with_del,
                        spinnerSubItems,
                        null,
                        null
                    )

                binding.reusable.subGroupSpinner.setAdapter(subGroupSpinnerAdapter)
            }
        }
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

    private fun getSpinnerSubItemsNotArchived(groupWithSubGroups: ExpenseGroupWithExpenseSubGroups?): MutableList<SpinnerItem> {
        val spinnerSubItems: MutableList<SpinnerItem> = mutableListOf()

        groupWithSubGroups?.expenseSubGroupEntities?.forEach {
            if (it.archivedDate == null) {
                spinnerSubItems.add(SpinnerItem(it.id, it.name))
            }
        }

        return spinnerSubItems
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupDateTimeFiledValues() {
        setDateEditText()
        setTimeEditText()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setDateEditText() {
        DateTimeUtils.setupDatePicker(binding.reusable.dateEditText, dateFormat)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setTimeEditText() {
        DateTimeUtils.setupTimePicker(binding.reusable.timeEditText, timeFormat)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupButtonClickListeners(view: View) {
        binding.reusable.addHistoryButton.setOnClickListener {
            if (!isButtonClickable) return@setOnClickListener
            isButtonClickable = false
            view.isEnabled = false

            val subGroupNameBinding = binding.reusable.subGroupSpinner.text.toString().trim()
            val amountBinding = binding.reusable.amountEditText.text.toString().trim()
            val commentBinding = binding.reusable.commentEditText.text.toString().trim()
            val walletNameBinding = binding.reusable.walletSpinner.text.toString().trim()
            val dateBinding = binding.reusable.dateEditText.text.toString().trim()
            val timeBinding = binding.reusable.timeEditText.text.toString().trim()

            var subGroupNameBindingValidation = EmptyValidator(subGroupNameBinding).validate()
            if (historyWithSubGroupAndWalletGlobal.expenseSubGroupEntity != null ) {
                binding.reusable.subGroupSpinnerLayout.error =
                    if (!subGroupNameBindingValidation.isSuccess) getString(
                        subGroupNameBindingValidation.message
                    ) else null
            } else {
                subGroupNameBindingValidation = ValidateResult(true, R.string.text_validation_success)
            }

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

                val expenseHistory = historyWithSubGroupAndWalletGlobal.expenseHistoryEntity

                val walletOld = historyWithSubGroupAndWalletGlobal.walletEntity
                if (walletOld != null) {
                    val updatedBalanceOld = walletOld.balance + expenseHistory.amount
                    val updatedOutputOld = walletOld.input.minus(expenseHistory.amount)
                    updateOldWallet(walletOld, updatedBalanceOld, updatedOutputOld)
                }
                val walletId = walletSpinnerItemsGlobal?.find { it.name == walletNameBinding}?.id

                val expenseSubGroupId = spinnerSubGroupItemsGlobal?.find { it.name == subGroupNameBinding }?.id

                updateExpenseHistoryViewModel.updateExpenseHistoryAndWallet(
                    ExpenseHistoryEntity(
                        id = expenseHistory.id,
                        expenseSubGroupId = expenseSubGroupId,
                        amount = amountBinding.toDouble(),
                        comment = commentBinding,
                        date = parsedLocalDateTime,
                        walletId = walletId!!,
                        archivedDate = expenseHistory.archivedDate,
                        createdDate = expenseHistory.createdDate,
                        amountBase = amountBinding.toDouble()
                    )
                )

                navigateToHistory()
            }

            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                isButtonClickable = true
                view.isEnabled = true
            }, Constants.CLICK_DELAY_MS)
        }
    }

    private fun updateOldWallet(
        walletEntity: WalletEntity,
        updatedBalance: Double,
        updatedOutput: Double
    ) {
        updateExpenseHistoryViewModel.updateWallet(
            walletEntity.copy(
                balance = updatedBalance,
                output = updatedOutput
            )
        )
    }

    private fun navigateToHistory() {
        sharedInitialTabIndexViewModel.set(2)
        findNavController().popBackStack(R.id.history_fragment, false)
    }

    private fun setOnBackPressedHandler() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateToHistory()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
}
