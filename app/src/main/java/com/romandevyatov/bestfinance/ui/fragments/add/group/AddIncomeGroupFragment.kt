package com.romandevyatov.bestfinance.ui.fragments.add.group

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
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.data.entities.IncomeGroup
import com.romandevyatov.bestfinance.data.validation.EmptyValidator
import com.romandevyatov.bestfinance.databinding.FragmentAddIncomeGroupBinding
import com.romandevyatov.bestfinance.utils.Constants
import com.romandevyatov.bestfinance.utils.WindowUtil
import com.romandevyatov.bestfinance.utils.voiceassistance.InputState
import com.romandevyatov.bestfinance.utils.voiceassistance.base.VoiceAssistanceBaseFragment
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.AddIncomeGroupViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddIncomeGroupFragment : VoiceAssistanceBaseFragment() {

    private var _binding: FragmentAddIncomeGroupBinding? = null
    private val binding get() = _binding!!

    private val addGroupViewModel: AddIncomeGroupViewModel by viewModels()

    private var isButtonClickable = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddIncomeGroupBinding.inflate(inflater, container, false)

        setUpSpeechRecognizer()

        setUpTextToSpeech()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val action =
                    AddIncomeGroupFragmentDirections.actionNavigationAddIncomeGroupToNavigationAddIncome()
                action.incomeGroupName = null
                action.incomeSubGroupName = null
                action.walletName = null
                findNavController().navigate(action)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        binding.addNewGroupButton.setOnClickListener {
            handleButtonClick(view)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun calculateSteps(): MutableList<InputState> {
        val steps: MutableList<InputState> = mutableListOf()

        if (binding.groupNameInputEditText.text.toString().isEmpty()) {
            steps.add(InputState.SET_NAME)
        }

        if (binding.groupDescriptionInputEditText.text.toString().isEmpty()) {
            steps.add(InputState.DESCRIPTION)
        }

        if (!binding.isPassiveCheckBox.isChecked) {
            steps.add(InputState.IS_PASSIVE)
        }

        steps.add(InputState.CONFIRM)

        return steps
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun handleUserInput(handledSpokenValue: String, currentStage: InputState) {
        when (currentStage) {
            InputState.SET_NAME -> handleNameInput(handledSpokenValue)
            InputState.DESCRIPTION -> handleDescriptionInput(handledSpokenValue)
            InputState.IS_PASSIVE -> handleIsPassiveInput(handledSpokenValue)
            InputState.CONFIRM -> handleConfirmInput(handledSpokenValue)
            else -> {}
        }
    }

    private fun handleNameInput(handledSpokenValue: String) {
        addGroupViewModel.getIncomeGroupNameByNameLiveData(handledSpokenValue).observe(viewLifecycleOwner) { group ->
            group?.let {
                val ask = getString(R.string.group_is_already_existing_set_another_name, handledSpokenValue)
                startVoiceAssistance(ask)
            } ?: run {
                binding.groupNameInputEditText.setText(handledSpokenValue)
                nextStage()
            }
        }
    }

    private fun handleDescriptionInput(handledSpokenValue: String) {
        val speakText = if (handledSpokenValue.isNotEmpty()) {
            binding.groupDescriptionInputEditText.setText(handledSpokenValue)
            getString(R.string.description_is_set)
        } else {
            getString(R.string.description_is_empty)
        }

        nextStage(speakTextBefore = speakText)
    }

    private fun handleIsPassiveInput(handledSpokenValue: String) {
        val isChecked = when (handledSpokenValue.lowercase()) {
            getString(R.string.yes) -> { // sent
                binding.isPassiveCheckBox.isChecked = true
                true
            }
            getString(R.string.no) -> { //
                binding.isPassiveCheckBox.isChecked = false
                false
            }
            else -> {
                speakText(getString(R.string.you_said, handledSpokenValue))
                null
            }
        }

        if (isChecked != null) nextStage()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleConfirmInput(handledSpokenValue: String) {
        when (handledSpokenValue.lowercase()) {
            getString(R.string.yes) -> { // sent
                createIncomeGroup()
                speakText(getString(R.string.income_group_added))
            }
            getString(R.string.no) -> { // no
                speakText(getString(R.string.exit))
            }
            else -> speakText(getString(R.string.you_said, handledSpokenValue))
        }
        spokenValue = null
    }

    private fun createIncomeGroup() {
        val groupNameBinding = binding.groupNameInputEditText.text.toString().trim()
        val groupDescriptionBinding = binding.groupDescriptionInputEditText.text.toString().trim()
        val isPassiveBinding = binding.isPassiveCheckBox.isChecked

        val nameEmptyValidation = EmptyValidator(groupNameBinding).validate()
        binding.groupNameInputLayout.error = if (!nameEmptyValidation.isSuccess) getString(nameEmptyValidation.message) else null

        if (nameEmptyValidation.isSuccess) {
            addGroupViewModel.getIncomeGroupNameByNameLiveData(groupNameBinding)
                .observe(viewLifecycleOwner) { incomeGroup ->
                    if (incomeGroup == null) {
                        addGroupViewModel.insertIncomeGroup(
                            IncomeGroup(
                                name = groupNameBinding,
                                description = groupDescriptionBinding,
                                isPassive = isPassiveBinding
                            )
                        )
                        val action =
                            AddIncomeGroupFragmentDirections.actionNavigationAddIncomeGroupToNavigationAddIncome()
                        action.incomeGroupName = groupNameBinding
                        findNavController().navigate(action)
                    } else if (incomeGroup.archivedDate == null) {
                        WindowUtil.showExistingDialog(
                            requireContext(),
                            getString(R.string.group_is_already_existing, groupNameBinding)
                        )
                    } else {
                        WindowUtil.showUnarchiveDialog(
                            requireContext(),
                            getString(R.string.group_is_archived, groupNameBinding, groupNameBinding)
                        ) {
                            addGroupViewModel.unarchiveIncomeGroup(incomeGroup)
                            val action = AddIncomeGroupFragmentDirections.actionNavigationAddIncomeGroupToNavigationAddIncome()
                            action.incomeGroupName = incomeGroup.name
                            findNavController().navigate(action)
                        }
                    }
                }
        }
    }

    private fun handleButtonClick(view: View) {
        if (!isButtonClickable) return
        isButtonClickable = false
        view.isEnabled = false

        createIncomeGroup()

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            isButtonClickable = true
            view.isEnabled = true
        }, Constants.CLICK_DELAY_MS)
    }

}
