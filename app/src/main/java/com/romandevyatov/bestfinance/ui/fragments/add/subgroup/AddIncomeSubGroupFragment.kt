package com.romandevyatov.bestfinance.ui.fragments.add.subgroup

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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.data.entities.IncomeGroup
import com.romandevyatov.bestfinance.data.entities.IncomeSubGroup
import com.romandevyatov.bestfinance.data.validation.EmptyValidator
import com.romandevyatov.bestfinance.databinding.FragmentAddIncomeSubGroupBinding
import com.romandevyatov.bestfinance.ui.adapters.spinner.GroupSpinnerAdapter
import com.romandevyatov.bestfinance.ui.adapters.spinner.models.SpinnerItem
import com.romandevyatov.bestfinance.utils.Constants
import com.romandevyatov.bestfinance.utils.SpinnerUtil
import com.romandevyatov.bestfinance.utils.WindowUtil
import com.romandevyatov.bestfinance.utils.voiceassistance.InputState
import com.romandevyatov.bestfinance.utils.voiceassistance.base.VoiceAssistanceBaseFragment
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.AddIncomeSubGroupViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddIncomeSubGroupFragment : VoiceAssistanceBaseFragment() {

    private var _binding: FragmentAddIncomeSubGroupBinding? = null
    private val binding get() = _binding!!

    private val addSubGroupViewModel: AddIncomeSubGroupViewModel by viewModels()

    private val args: AddIncomeSubGroupFragmentArgs by navArgs()

    private val spinnerItemsGlobal: MutableList<SpinnerItem> = mutableListOf()

    private var isButtonClickable = true

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val callback = object : OnBackPressedCallback(
            true
        ) {
            override fun handleOnBackPressed() {
                val action =
                    AddIncomeSubGroupFragmentDirections.actionNavigationAddIncomeSubGroupToNavigationAddIncome()
                action.incomeGroupName = null
                action.incomeSubGroupName = null
                action.walletName = null
                findNavController().navigate(action)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddIncomeSubGroupBinding.inflate(inflater, container, false)

        setUpSpeechRecognizer()

        setUpTextToSpeech()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initGroupSpinner()

        binding.addSubGroupButton.setOnClickListener {
            if (!isButtonClickable) return@setOnClickListener
            isButtonClickable = false
            view.isEnabled = false

            createIncomeSubGroup()

            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                isButtonClickable = true
                view.isEnabled = true
            }, Constants.CLICK_DELAY_MS)
        }
    }

    fun createIncomeSubGroup() {
        val subGroupNameBinding = binding.subGroupNameEditText.text.toString()
        val descriptionBinding = binding.subGroupDescriptionEditText.text.toString()
        val selectedGroupNameBinding = binding.groupSpinner.text.toString()

        val subGroupNameValidation = EmptyValidator(subGroupNameBinding).validate()
        binding.incomeSubGroupNameTextInputLayout.error = if (!subGroupNameValidation.isSuccess) getString(R.string.error_empty_sub_group_name) else null

        val groupSpinnerValidation = EmptyValidator(selectedGroupNameBinding).validate()
        binding.groupSpinnerLayout.error = if (!groupSpinnerValidation.isSuccess) getString(R.string.error_empty_group_name) else null

        if (subGroupNameValidation.isSuccess && groupSpinnerValidation.isSuccess) {
            val groupId = spinnerItemsGlobal.find { it.name == selectedGroupNameBinding }?.id!!

            addSubGroupViewModel.getIncomeSubGroupByNameWithIncomeGroupIdLiveData(
                subGroupNameBinding, groupId
            ).observe(viewLifecycleOwner) { subGroup ->
                if (subGroup == null) {
                    val newIncomeSubGroup = IncomeSubGroup(
                        name = subGroupNameBinding,
                        description = descriptionBinding,
                        incomeGroupId = groupId
                    )

                    addSubGroupViewModel.insertIncomeSubGroup(newIncomeSubGroup)
                    val action =
                        AddIncomeSubGroupFragmentDirections.actionNavigationAddIncomeSubGroupToNavigationAddIncome()
                    action.incomeGroupName = selectedGroupNameBinding
                    action.incomeSubGroupName = subGroupNameBinding
                    findNavController().navigate(action)
                } else if (subGroup.archivedDate == null) {
                    WindowUtil.showExistingDialog(
                        requireContext(),
                        getString(R.string.error_existing_sub_group, subGroupNameBinding)
                    )
                } else {
                    WindowUtil.showUnarchiveDialog(
                        requireContext(),
                        getString(R.string.confirm_unarchive_message, subGroupNameBinding)
                    ) {
                        addSubGroupViewModel.unarchiveIncomeSubGroup(subGroup)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

            if (groupList.contains(handledSpokenValue)) {
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
                addSubGroupViewModel.getIncomeSubGroupByNameWithIncomeGroupIdLiveData(
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
                createIncomeSubGroup()
                speakText(getString(R.string.income_sub_group_added))
            }
            getString(R.string.no) -> { // no
                speakText(getString(R.string.exit))
            }
            else -> speakText(getString(R.string.you_said, handledSpokenValue))
        }
        spokenValue = null
    }

    private fun initGroupSpinner() {
        addSubGroupViewModel.incomeGroupsNotArchivedLiveData.observe(viewLifecycleOwner) { incomeGroupList ->
            incomeGroupList?.let { groups ->
                val spinnerItems = getIncomeGroupList(groups)

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

                if (args.incomeGroupName?.isNotBlank() == true) {
                    binding.groupSpinner.setText(args.incomeGroupName.toString(), false)
                }
            }
        }
    }

    private fun getIncomeGroupList(groups: List<IncomeGroup>): MutableList<SpinnerItem> {
        return groups.map {
            SpinnerItem(it.id, it.name)
        }.toMutableList()
    }
}
