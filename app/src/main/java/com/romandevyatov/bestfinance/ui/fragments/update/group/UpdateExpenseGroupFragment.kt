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
import com.romandevyatov.bestfinance.data.entities.ExpenseGroupEntity
import com.romandevyatov.bestfinance.data.validation.EmptyValidator
import com.romandevyatov.bestfinance.databinding.FragmentUpdateExpenseGroupBinding
import com.romandevyatov.bestfinance.utils.Constants
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

    private var expenseGroupEntityGlobal: ExpenseGroupEntity? = null

    private var isButtonClickable = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateExpenseGroupBinding.inflate(inflater, container, false)

        setOnBackPressedHandler()

        binding.reusable.addNewExpenseGroupNameButton.text = getString(R.string.update)

        updateExpenseGroupViewModel.getExpenseGroupByNameLiveData(args.expenseGroupName.toString())
            .observe(viewLifecycleOwner) { expenseGroup ->
                expenseGroup?.let {
                    expenseGroupEntityGlobal = it.copy()
                    binding.reusable.newExpenseGroupName.setText(it.name)
                    binding.reusable.descriptionEditText.setText(it.description)
                    expenseGroupId = it.id
                    binding.checkedTextView.isChecked = it.archivedDate != null
                    binding.checkedTextView.isEnabled = false
                }
            }

        return binding.root
    }

    private fun setOnBackPressedHandler() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateToSettingsGroupsAndSubGroupsSettingsFragment()
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
                    .observe(viewLifecycleOwner) { group ->
                        if (nameBinding == args.expenseGroupName.toString() || group == null) {
                            updateExpenseGroup(nameBinding, descriptionBinding)

                            navigateToSettingsGroupsAndSubGroupsSettingsFragment()
                        } else {
                            // Group is already existing
                            WindowUtil.showExistingDialog(
                                requireContext(),
                                getString(R.string.error_existing_group, nameBinding)
                            )
                        }
                    }
            }

            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                isButtonClickable = true
                view.isEnabled = true
            }, Constants.CLICK_DELAY_MS)
        }
    }

    private fun navigateToSettingsGroupsAndSubGroupsSettingsFragment() {
        val action =
            UpdateExpenseGroupFragmentDirections.actionNavigationUpdateExpenseGroupToNavigationSettingsGroupsAndSubGroupsSettingsFragment()
        action.initialTabIndex = 1
        findNavController().navigate(action)
    }

    private fun updateExpenseGroup(nameBinding: String, descriptionBinding: String) {
        val updatedExpenseGroupEntity = ExpenseGroupEntity(
            id = expenseGroupEntityGlobal?.id,
            name = nameBinding,
            description = descriptionBinding,
            archivedDate = expenseGroupEntityGlobal?.archivedDate
        )
        updateExpenseGroupViewModel.updateExpenseGroup(updatedExpenseGroupEntity)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
