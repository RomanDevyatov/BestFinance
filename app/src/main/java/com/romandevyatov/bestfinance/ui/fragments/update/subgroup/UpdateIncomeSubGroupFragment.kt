package com.romandevyatov.bestfinance.ui.fragments.update.subgroup

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.data.entities.IncomeGroup
import com.romandevyatov.bestfinance.data.entities.IncomeSubGroup
import com.romandevyatov.bestfinance.data.validation.EmptyValidator
import com.romandevyatov.bestfinance.databinding.FragmentUpdateIncomeSubGroupBinding
import com.romandevyatov.bestfinance.ui.adapters.spinner.GroupSpinnerAdapter
import com.romandevyatov.bestfinance.ui.adapters.spinner.SpinnerItem
import com.romandevyatov.bestfinance.utils.Constants
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.UpdateIncomeSubGroupViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateIncomeSubGroupFragment : Fragment() {

    private var _binding: FragmentUpdateIncomeSubGroupBinding? = null
    private val binding get() = _binding!!
    private val updateSubGroupViewModel: UpdateIncomeSubGroupViewModel by viewModels()

    private val args: UpdateIncomeSubGroupFragmentArgs by navArgs()

    private var incomeSubGroupOldGlobal: IncomeSubGroup? = null

    private var incomeGroupsGlobal: List<SpinnerItem>? = emptyList()

    private val clickDelayMs = 1000
    private var isButtonClickable = true

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val callback = object : OnBackPressedCallback(
            true
        ) {
            override fun handleOnBackPressed() {
                val action =
                    UpdateIncomeSubGroupFragmentDirections.actionNavigationUpdateIncomeSubGroupToNavigationSettingsGroupsAndSubGroupsSettingsFragment()
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
        _binding = FragmentUpdateIncomeSubGroupBinding.inflate(inflater, container, false)

        binding.reusable.addSubGroupButton.text = "Update"

        updateSubGroupViewModel.getIncomeSubGroupByIdLiveData(args.incomeSubGroupId)
            ?.observe(viewLifecycleOwner) { incomeSubGroup ->
                incomeSubGroupOldGlobal = IncomeSubGroup(
                    incomeSubGroup.id,
                    incomeSubGroup.name,
                    incomeSubGroup.description,
                    incomeSubGroup.incomeGroupId,
                    incomeSubGroup.archivedDate)

                binding.reusable.subGroupNameEditText.setText(incomeSubGroup.name)
                binding.reusable.subGroupDescriptionEditText.setText(incomeSubGroup.description)
            }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initGroupSpinner()

        binding.reusable.addSubGroupButton.setOnClickListener {
            handleUpdateSubGroup(view)

        }
    }

    private fun handleUpdateSubGroup(view: View) {
        if (!isButtonClickable) return
        isButtonClickable = false
        view.isEnabled = false

        val newSubGroupNameBinding = binding.reusable.subGroupNameEditText.text.toString()
        val newDescriptionBinding = binding.reusable.subGroupDescriptionEditText.text.toString()
        val newGroupNameBinding = binding.reusable.groupSpinner.text.toString()

        if (isValidForm(newSubGroupNameBinding, newGroupNameBinding)) {
            val groupSpinnerItem = getGroupSpinnerItemByName(newGroupNameBinding)
            val newIncomeGroupId = groupSpinnerItem?.id

            updateSubGroupViewModel.getIncomeGroupWithIncomeSubGroupsByIncomeGroupId(newIncomeGroupId)
                .observe(viewLifecycleOwner) { groupWithSubGroups ->
                    val subGroups = groupWithSubGroups.incomeSubGroups.map { it.name }.toMutableList()

                    if ((incomeSubGroupOldGlobal?.name != newSubGroupNameBinding
                                || incomeSubGroupOldGlobal?.incomeGroupId != newIncomeGroupId)
                        && subGroups.contains(newSubGroupNameBinding)) {
                        showExistingDialog(requireContext(), "This sub group `$newSubGroupNameBinding` is already existing in `${newGroupNameBinding}` group.")
                    } else {
                        updateSubGroup(newSubGroupNameBinding, newDescriptionBinding, newIncomeGroupId)
                        navigateToSettingGroupsAndSubGroups()
                    }
                }
        }

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            isButtonClickable = true
            view.isEnabled = true
        }, clickDelayMs.toLong())
    }

    private fun navigateToSettingGroupsAndSubGroups() {
        val action =
            UpdateIncomeSubGroupFragmentDirections.actionNavigationUpdateIncomeSubGroupToNavigationSettingsGroupsAndSubGroupsSettingsFragment()
        findNavController().navigate(action)
    }

    private fun isValidForm(newSubGroupNameBinding: String, newGroupNameBinding: String): Boolean {
        val subGroupNameValidation = EmptyValidator(newSubGroupNameBinding).validate()
        binding.reusable.incomeSubGroupNameTextInputLayout.error =
            if (!subGroupNameValidation.isSuccess) getString(subGroupNameValidation.message) else null

        val groupSpinnerValidation = EmptyValidator(newGroupNameBinding).validate()
        binding.reusable.groupSpinnerLayout.error =
            if (!groupSpinnerValidation.isSuccess) getString(groupSpinnerValidation.message) else null

        return subGroupNameValidation.isSuccess
                && groupSpinnerValidation.isSuccess
    }

    private fun updateSubGroup(
        newSubGroupNameBinding: String,
        newDescriptionBinding: String,
        newIncomeGroupId: Long?
    ) {
        val updatedSubGroup = IncomeSubGroup(
            id = incomeSubGroupOldGlobal?.id,
            name = newSubGroupNameBinding,
            description = newDescriptionBinding,
            incomeGroupId = newIncomeGroupId!!,
            archivedDate = incomeSubGroupOldGlobal?.archivedDate
        )
        updateSubGroupViewModel.updateIncomeSubGroup(updatedSubGroup)
    }

    private fun getGroupSpinnerItemByName(nameGroup: String): SpinnerItem? {
        return incomeGroupsGlobal?.find { it.name == nameGroup }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initGroupSpinner() {
        updateSubGroupViewModel.getAllIncomeGroupNotArchivedLiveData()
            .observe(viewLifecycleOwner) { incomeGroups ->
                val spinnerItems = getIncomeGroupList(incomeGroups)

                val spinnerAdapter =
                    GroupSpinnerAdapter(requireContext(), R.layout.item_with_del, spinnerItems,
                        Constants.ADD_NEW_INCOME_GROUP)

                incomeGroupsGlobal = spinnerItems

                binding.reusable.groupSpinner.setAdapter(spinnerAdapter)

                val groupName = spinnerItems.find { it.id == incomeSubGroupOldGlobal?.incomeGroupId }?.name
                binding.reusable.groupSpinner.setText(groupName, false)
            }
    }

    private fun getIncomeGroupList(groups: List<IncomeGroup>?): ArrayList<SpinnerItem> {
        val spinnerItems = ArrayList<SpinnerItem>()

        groups?.forEach {
            spinnerItems.add(SpinnerItem(it.id, it.name))
        }

        return spinnerItems
    }

    private fun showExistingDialog(context: Context, message: String?) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_info)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val tvMessage: TextView = dialog.findViewById(com.romandevyatov.bestfinance.R.id.tvMessage)
        val btnOk: Button = dialog.findViewById(com.romandevyatov.bestfinance.R.id.btnOk)

        tvMessage.text = message

        btnOk.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}