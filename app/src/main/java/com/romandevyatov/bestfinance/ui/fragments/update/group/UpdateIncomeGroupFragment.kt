package com.romandevyatov.bestfinance.ui.fragments.update.group

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.data.entities.IncomeGroup
import com.romandevyatov.bestfinance.data.validation.EmptyValidator
import com.romandevyatov.bestfinance.databinding.FragmentUpdateIncomeGroupBinding
import com.romandevyatov.bestfinance.utils.WindowUtil
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.UpdateIncomeGroupViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateIncomeGroupFragment : Fragment() {

    private var _binding: FragmentUpdateIncomeGroupBinding? = null

    private val binding get() = _binding!!

    private val updateIncomeGroupViewModel: UpdateIncomeGroupViewModel by viewModels()

    private val args: UpdateIncomeGroupFragmentArgs by navArgs()

    private var incomeGroupId: Long? = null

    private var incomeGroupGlobal: IncomeGroup? = null

    private val clickDelay = 1000 // Set the delay time in milliseconds
    private var isButtonClickable = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateIncomeGroupBinding.inflate(inflater, container, false)

        setOnBackPressedHandler()

        binding.reusable.addNewGroupButton.text = "Update"

        updateIncomeGroupViewModel.getIncomeGroupByNameLiveData(args.incomeGroupName.toString())
            ?.observe(viewLifecycleOwner) { incomeGroup ->
                incomeGroupGlobal = IncomeGroup(
                    incomeGroup.id,
                    incomeGroup.name,
                    incomeGroup.isPassive,
                    incomeGroup.description,
                    incomeGroup.archivedDate)
                binding.reusable.groupNameInputEditText.setText(incomeGroup.name)
                binding.reusable.groupDescriptionInputEditText.setText(incomeGroup.description)
                incomeGroupId = incomeGroup.id
                binding.checkedTextView.isChecked = incomeGroup.archivedDate != null
                binding.checkedTextView.isEnabled = false
            }

        return binding.root
    }

    private fun setOnBackPressedHandler() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_navigation_update_income_group_to_navigation_settings_groups_and_sub_groups_settings_fragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.reusable.addNewGroupButton.setOnClickListener {
            if (!isButtonClickable) return@setOnClickListener
            isButtonClickable = false
            view.isEnabled = false

            val nameBinding = binding.reusable.groupNameInputEditText.text.toString()
            val descriptionBinding = binding.reusable.groupDescriptionInputEditText.text.toString()
            val isPassiveBinding = binding.reusable.isPassiveCheckBox.isChecked

            val nameEmptyValidation = EmptyValidator(nameBinding).validate()
            binding.reusable.groupNameInputLayout.error = if (!nameEmptyValidation.isSuccess) getString(nameEmptyValidation.message) else null

            if (nameEmptyValidation.isSuccess) {
                updateIncomeGroupViewModel.getIncomeGroupByNameLiveData(nameBinding)
                    ?.observe(viewLifecycleOwner) { group ->
                        if (nameBinding == args.incomeGroupName.toString() || group == null) {
                            updateIncomeGroup(nameBinding, descriptionBinding, isPassiveBinding)

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
            UpdateIncomeGroupFragmentDirections.actionNavigationUpdateIncomeGroupToNavigationSettingsGroupsAndSubGroupsSettingsFragment()
        findNavController().navigate(action)
    }

    private fun updateIncomeGroup(
        nameBinding: String,
        descriptionBinding: String,
        isPassiveBinding: Boolean
    ) {
        val updatedIncomeGroup = IncomeGroup(
            id = incomeGroupId,
            name = nameBinding,
            isPassive = isPassiveBinding,
            description = descriptionBinding,
            archivedDate = incomeGroupGlobal?.archivedDate
        )

        updateIncomeGroupViewModel.updateIncomeGroup(updatedIncomeGroup)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}