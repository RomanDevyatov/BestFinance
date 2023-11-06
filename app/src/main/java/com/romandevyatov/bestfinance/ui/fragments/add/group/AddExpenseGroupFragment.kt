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
import com.romandevyatov.bestfinance.data.entities.ExpenseGroupEntity
import com.romandevyatov.bestfinance.data.validation.EmptyValidator
import com.romandevyatov.bestfinance.databinding.FragmentAddExpenseGroupBinding
import com.romandevyatov.bestfinance.utils.Constants
import com.romandevyatov.bestfinance.utils.WindowUtil
import com.romandevyatov.bestfinance.utils.voiceassistance.InputState
import com.romandevyatov.bestfinance.utils.voiceassistance.base.VoiceAssistanceBaseFragment
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.AddExpenseGroupViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddExpenseGroupFragment : VoiceAssistanceBaseFragment() {

    private var _binding: FragmentAddExpenseGroupBinding? = null
    private val binding get() = _binding!!

    private val addGroupViewModel: AddExpenseGroupViewModel by viewModels()

    private var isButtonClickable = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddExpenseGroupBinding.inflate(inflater, container, false)

        setUpSpeechRecognizer()

        setUpTextToSpeech()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callback = object : OnBackPressedCallback(
            true
        ) {
            override fun handleOnBackPressed() {
                val action =
                    AddExpenseGroupFragmentDirections.actionNavigationAddExpenseGroupToNavigationAddExpense()
                action.expenseGroupName = null
                findNavController().navigate(action)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        binding.addNewExpenseGroupNameButton.setOnClickListener {
            handleButtonClick(view)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun calculateSteps(): MutableList<InputState> {
        val steps: MutableList<InputState> = mutableListOf()

        if (binding.newExpenseGroupName.text.toString().isEmpty()) {
            steps.add(InputState.SET_NAME)
        }

        if (binding.descriptionEditText.text.toString().isEmpty()) {
            steps.add(InputState.DESCRIPTION)
        }

        steps.add(InputState.CONFIRM)

        return steps
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun handleUserInput(handledSpokenValue: String, currentStage: InputState) {
        when (currentStage) {
            InputState.SET_NAME -> handleNameInput(handledSpokenValue)
            InputState.DESCRIPTION -> handleDescriptionInput(handledSpokenValue)
            InputState.CONFIRM -> handleConfirmInput(handledSpokenValue)
            else -> {}
        }
    }

    private fun handleNameInput(handledSpokenValue: String) {
        if (spokenValue == null) {
            addGroupViewModel.getExpenseGroupByNameLiveData(handledSpokenValue).observe(viewLifecycleOwner) { group ->
                group?.let {
                    val ask = getString(R.string.group_is_already_existing_set_another_name, handledSpokenValue)
                    startVoiceAssistance(ask)
                } ?: run {
                    binding.newExpenseGroupName.setText(handledSpokenValue)
                    nextStage()
                }
            }
        }
    }

    private fun handleDescriptionInput(handledSpokenValue: String) {
        val speakText = if (handledSpokenValue.isNotEmpty()) {
            binding.descriptionEditText.setText(handledSpokenValue)
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
                createExpenseGroup()
                speakText(getString(R.string.expense_group_added))
            }
            getString(R.string.no) -> { // no
                speakText(getString(R.string.exit))
            }
            else -> speakText(getString(R.string.you_said, handledSpokenValue))
        }
        spokenValue = null
    }

    private fun createExpenseGroup() {
        val groupNameBinding = binding.newExpenseGroupName.text.toString().trim()
        val descriptionBinding = binding.descriptionEditText.text.toString().trim()

        val nameEmptyValidation = EmptyValidator(groupNameBinding).validate()
        binding.newExpenseGroupNameLayout.error = if (!nameEmptyValidation.isSuccess) getString(nameEmptyValidation.message) else null

        if (nameEmptyValidation.isSuccess) {
            addGroupViewModel.getExpenseGroupByNameLiveData(groupNameBinding)
                .observe(viewLifecycleOwner) { expenseGroup ->
                    if (expenseGroup == null) {
                        addGroupViewModel.insertExpenseGroup(
                            ExpenseGroupEntity(
                                name = groupNameBinding,
                                description = descriptionBinding
                            )
                        )

                        val action =
                            AddExpenseGroupFragmentDirections.actionNavigationAddExpenseGroupToNavigationAddExpense()
                        action.expenseGroupName = groupNameBinding
                        findNavController().navigate(action)
                    } else if (expenseGroup.archivedDate == null) {
                        WindowUtil.showExistingDialog(
                            requireContext(),
                            getString(R.string.group_is_already_existing, groupNameBinding)
                        )
                    } else {
                        WindowUtil.showUnarchiveDialog(
                            requireContext(),
                            getString(R.string.group_is_archived, groupNameBinding, groupNameBinding)
                        ) {
                            addGroupViewModel.unarchiveExpenseGroup(expenseGroup)
                            val action = AddExpenseGroupFragmentDirections.actionNavigationAddExpenseGroupToNavigationAddExpense()
                            action.expenseGroupName = expenseGroup.name
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

        createExpenseGroup()

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            isButtonClickable = true
            view.isEnabled = true
        }, Constants.CLICK_DELAY_MS)
    }

}
