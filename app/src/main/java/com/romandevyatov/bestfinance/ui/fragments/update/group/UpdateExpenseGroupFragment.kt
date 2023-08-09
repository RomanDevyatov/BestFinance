package com.romandevyatov.bestfinance.ui.fragments.update.group

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.data.entities.ExpenseGroup
import com.romandevyatov.bestfinance.data.validation.EmptyValidator
import com.romandevyatov.bestfinance.databinding.FragmentUpdateExpenseGroupBinding
import com.romandevyatov.bestfinance.utils.WindowUtil
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.UpdateExpenseGroupViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateExpenseGroupFragment : Fragment() {
    private var _binding: FragmentUpdateExpenseGroupBinding? = null

    private val binding get() = _binding!!

    private val updateExpenseGroupViewModel: UpdateExpenseGroupViewModel by viewModels()

    private val args: UpdateExpenseGroupFragmentArgs by navArgs()

    private var expenseGroupId: Long? = null

    private var expenseGroupGlobal: ExpenseGroup? = null

    private val clickDelay = 1000 // Set the delay time in milliseconds
    private var isButtonClickable = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateExpenseGroupBinding.inflate(inflater, container, false)

        setOnBackPressedHandler()

        binding.reusable.addNewExpenseGroupNameButton.text = "Update"

        updateExpenseGroupViewModel.getExpenseGroupByNameLiveData(args.expenseGroupName.toString())
            ?.observe(viewLifecycleOwner) { expenseGroup ->
                expenseGroupGlobal = ExpenseGroup(
                    expenseGroup.id,
                    expenseGroup.name,
                    expenseGroup.description,
                    expenseGroup.archivedDate)
            binding.reusable.newExpenseGroupName.setText(expenseGroup.name)
            binding.reusable.descriptionEditText.setText(expenseGroup.description)
            expenseGroupId = expenseGroup.id
            binding.checkedTextView.isChecked = expenseGroup.archivedDate != null
            binding.checkedTextView.isEnabled = false
        }

        return binding.root
    }

    private fun setOnBackPressedHandler() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_navigation_update_expense_group_to_navigation_settings_groups_and_sub_groups_settings_fragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.reusable.addNewExpenseGroupNameButton.setOnClickListener {
            if (!isButtonClickable) return@setOnClickListener
            isButtonClickable = false
            view.isEnabled = false

            val nameBinding = binding.reusable.newExpenseGroupName.text.toString()
            val descriptionBinding = binding.reusable.descriptionEditText.text.toString()

            val nameEmptyValidation = EmptyValidator(nameBinding).validate()
            binding.reusable.newExpenseGroupNameLayout.error = if (!nameEmptyValidation.isSuccess) getString(nameEmptyValidation.message) else null

            if (nameEmptyValidation.isSuccess) {
                updateExpenseGroupViewModel.getExpenseGroupByNameLiveData(nameBinding)
                    ?.observe(viewLifecycleOwner) { group ->
                        if (nameBinding == args.expenseGroupName.toString() || group == null) {
                            updateExpenseGroup(nameBinding, descriptionBinding)

                            navigateToSettingsGroupsAndSubGroupsSettingsFragment()
                        } else {
                            // Group is already existing
                            WindowUtil.showExistingDialog(
                                requireContext(),
                                "This group `$nameBinding` is already existing."
                            )
                        }
                    }
            }

            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                isButtonClickable = true
                view.isEnabled = true
            }, clickDelay.toLong())
        }
    }

    private fun navigateToSettingsGroupsAndSubGroupsSettingsFragment() {
        val action =
            UpdateExpenseGroupFragmentDirections.actionNavigationUpdateExpenseGroupToNavigationSettingsGroupsAndSubGroupsSettingsFragment()
        findNavController().navigate(action)
    }

    private fun updateExpenseGroup(nameBinding: String, descriptionBinding: String) {
        val updatedExpenseGroup = ExpenseGroup(
            id = expenseGroupGlobal?.id,
            name = nameBinding,
            description = descriptionBinding,
            archivedDate = expenseGroupGlobal?.archivedDate
        )
        updateExpenseGroupViewModel.updateExpenseGroup(updatedExpenseGroup)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
