package com.romandevyatov.bestfinance.ui.fragments.add.subgroup

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.data.entities.ExpenseGroup
import com.romandevyatov.bestfinance.data.entities.ExpenseSubGroup
import com.romandevyatov.bestfinance.data.validation.EmptyValidator
import com.romandevyatov.bestfinance.databinding.FragmentAddExpenseSubGroupBinding
import com.romandevyatov.bestfinance.ui.adapters.spinner.GroupSpinnerAdapter
import com.romandevyatov.bestfinance.ui.adapters.spinner.models.SpinnerItem
import com.romandevyatov.bestfinance.utils.Constants
import com.romandevyatov.bestfinance.utils.SpinnerUtil
import com.romandevyatov.bestfinance.utils.WindowUtil
import com.romandevyatov.bestfinance.utils.voiceassistance.InputState
import com.romandevyatov.bestfinance.utils.voiceassistance.base.VoiceAssistanceBaseFragment
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.AddExpenseSubGroupViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddExpenseSubGroupFragment : VoiceAssistanceBaseFragment() {

    private var _binding: FragmentAddExpenseSubGroupBinding? = null
    private val binding get() = _binding!!

    private val addSubGroupViewModel: AddExpenseSubGroupViewModel by viewModels()

    private val args: AddExpenseSubGroupFragmentArgs by navArgs()

    private val spinnerItemsGlobal: MutableList<SpinnerItem> = mutableListOf()

    private var isButtonClickable = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddExpenseSubGroupBinding.inflate(inflater, container, false)

        setUpSpeechRecognizer()

        setUpTextToSpeech()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initGroupSpinner()

        binding.addSubGroupNameButton.setOnClickListener {
            if (!isButtonClickable) return@setOnClickListener
            isButtonClickable = false
            view.isEnabled = false

            createExpenseSubGroup()

            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                isButtonClickable = true
                view.isEnabled = true
            }, Constants.CLICK_DELAY_MS)
        }
    }

    private fun createExpenseSubGroup() {
        val subGroupNameBinding = binding.subGroupNameEditText.text.toString()
        val descriptionBinding = binding.subGroupDescriptionEditText.text.toString()
        val selectedGroupNameBinding = binding.groupSpinner.text.toString()

        val subGroupNameValidation = EmptyValidator(subGroupNameBinding).validate()
        binding.subGroupNameTextInputLayout.error = if (!subGroupNameValidation.isSuccess) getString(R.string.error_empty_sub_group_name) else null

        val groupMaterialSpinnerValidation = EmptyValidator(selectedGroupNameBinding).validate()
        binding.groupSpinnerLayout.error = if (!groupMaterialSpinnerValidation.isSuccess) getString(R.string.error_empty_group_name) else null

        if (subGroupNameValidation.isSuccess && groupMaterialSpinnerValidation.isSuccess) {
            val groupId = spinnerItemsGlobal.find { it.name == selectedGroupNameBinding }?.id!!

            addSubGroupViewModel.getExpenseSubGroupByNameWithExpenseGroupIdLiveData(
                subGroupNameBinding, groupId
            ).observe(viewLifecycleOwner) { subGroup ->
                if (subGroup == null) {
                    addIncomeSubGroup(subGroupNameBinding, descriptionBinding, groupId)

                    navigateToAddExpense(selectedGroupNameBinding, subGroupNameBinding)
                } else if (subGroup.archivedDate == null) {
                    WindowUtil.showExistingDialog(
                        requireContext(),
                        getString(R.string.sub_group_exists_message, subGroupNameBinding)
                    )
                } else {
                    WindowUtil.showUnarchiveDialog(
                        requireContext(),
                        getString(R.string.confirm_unarchive_message, subGroupNameBinding)
                    ) {
                        addSubGroupViewModel.unarchiveExpenseSubGroup(subGroup)
                    }
                }
            }
        }
    }

    override fun calculateSteps(): MutableList<InputState> {
        val steps: MutableList<InputState> = mutableListOf()

        if (binding.groupSpinner.text.toString().isEmpty()) {
            steps.add(InputState.GROUP)
        }

        if (binding.subGroupNameEditText.text.toString().isEmpty()) {
            steps.add(InputState.SET_NAME)
        }

        if (binding.subGroupDescriptionEditText.text.toString().isEmpty()) {
            steps.add(InputState.DESCRIPTION)
        }

        steps.add(InputState.CONFIRM)

        return steps
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun handleUserInput(handledSpokenValue: String, currentStage: InputState) {
        when (currentStageName) {
            InputState.GROUP -> handleGroupInput(handledSpokenValue)
            InputState.SET_NAME -> handleNameInput(handledSpokenValue)
            InputState.DESCRIPTION -> handleDescriptionInput(handledSpokenValue)
            InputState.CONFIRM -> handleConfirmInput(handledSpokenValue)
            else -> {}
        }
    }

    private fun handleGroupInput(handledSpokenValue: String) {
        if (spokenValue == null) {
            val groupList = SpinnerUtil.getAllItemsFromAutoCompleteTextView(binding.groupSpinner)

            if (groupList.contains(handledSpokenValue)) { // success
                binding.groupSpinner.setText(handledSpokenValue, false)
                nextStage(speakTextBefore = getString(R.string.group_is_set))
            } else {
                spokenValue = handledSpokenValue

                val ask = getString(R.string.group_doesnt_exist_set_another_name, handledSpokenValue)
                speakTextAndRecognize(ask, false)
            }
        }
    }

    private fun handleNameInput(handledSpokenValue: String) {
        if (spokenValue == null) {
            val groupId = spinnerItemsGlobal.find { it.name == binding.groupSpinner.text.toString() }?.id
            if (groupId != null) {
                addSubGroupViewModel.getExpenseSubGroupByNameWithExpenseGroupIdLiveData(
                    handledSpokenValue,
                    groupId
                ).observe(viewLifecycleOwner) { group ->
                    group?.let {
                        val ask = getString(R.string.subgroup_is_already_existing_in_group_set_another_name, handledSpokenValue, binding.groupSpinner.text.toString())
                        startVoiceAssistance(ask)
                    } ?: run {
                        binding.subGroupNameEditText.setText(handledSpokenValue)
                        nextStage()
                    }
                }
            }
        }
    }

    private fun handleDescriptionInput(handledSpokenValue: String) {
        val speakText = if (handledSpokenValue.isNotEmpty()) {
            binding.subGroupDescriptionEditText.setText(handledSpokenValue)
            getString(R.string.description_is_set)
        } else {
            getString(R.string.description_is_empty)
        }

        nextStage(speakTextBefore = speakText)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleConfirmInput(handledSpokenValue: String) {
        when (handledSpokenValue.lowercase()) {
            getString(R.string.yes) -> { // sent
                createExpenseSubGroup()
                speakText(getString(R.string.expense_sub_group_added))
            }
            getString(R.string.no) -> { // no
                speakText(getString(R.string.exit))
            }
            else -> speakText(getString(R.string.you_said, handledSpokenValue))
        }
        spokenValue = null
    }

    private fun addIncomeSubGroup(
        subGroupNameBinding: String,
        descriptionBinding: String,
        groupId: Long
    ) {
        val newExpenseSubGroup = ExpenseSubGroup(
            name = subGroupNameBinding,
            description = descriptionBinding,
            expenseGroupId = groupId
        )

        addSubGroupViewModel.insertExpenseSubGroup(newExpenseSubGroup)
    }

    private fun navigateToAddExpense(
        selectedGroupNameBinding: String,
        subGroupNameBinding: String
    ) {
        val action =
            AddExpenseSubGroupFragmentDirections.actionNavigationAddExpenseSubGroupToNavigationAddExpense()
        action.expenseGroupName = selectedGroupNameBinding
        action.expenseSubGroupName = subGroupNameBinding

        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initGroupSpinner() {
        addSubGroupViewModel.allExpenseGroupsNotArchivedLiveData.observe(viewLifecycleOwner) { expenseGroupList ->
            val spinnerItems = getExpenseGroupListForSpinner(expenseGroupList)

            spinnerItemsGlobal.clear()
            spinnerItemsGlobal.addAll(spinnerItems)

            val spinnerAdapter = GroupSpinnerAdapter(
                requireContext(),
                R.layout.item_with_del,
                spinnerItems,
                null,
                null
            )

            binding.groupSpinner.setAdapter(spinnerAdapter)

            if (args.expenseGroupName?.isNotBlank() == true) {
                binding.groupSpinner.setText(args.expenseGroupName.toString(), false)
            }
        }
    }

    private fun getExpenseGroupListForSpinner(groups: List<ExpenseGroup>): MutableList<SpinnerItem> {
        return groups.map {
            SpinnerItem(it.id, it.name)
        }.toMutableList()
    }

}
