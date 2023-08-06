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
import com.romandevyatov.bestfinance.data.entities.ExpenseGroup
import com.romandevyatov.bestfinance.data.entities.ExpenseSubGroup
import com.romandevyatov.bestfinance.data.validation.EmptyValidator
import com.romandevyatov.bestfinance.databinding.FragmentUpdateExpenseSubGroupBinding
import com.romandevyatov.bestfinance.ui.adapters.spinner.GroupSpinnerAdapter
import com.romandevyatov.bestfinance.ui.adapters.spinner.SpinnerItem
import com.romandevyatov.bestfinance.utils.Constants
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.UpdateExpenseSubGroupViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateExpenseSubGroupFragment : Fragment() {

    private var _binding: FragmentUpdateExpenseSubGroupBinding? = null
    private val binding get() = _binding!!
    private val updateSubGroupViewModel: UpdateExpenseSubGroupViewModel by viewModels()

    private val args: UpdateExpenseSubGroupFragmentArgs by navArgs()

    private var expenseSubGroupOldGlobal: ExpenseSubGroup? = null

    private var expenseGroupsGlobal: List<SpinnerItem>? = emptyList()

    private val clickDelayMs = 1000
    private var isButtonClickable = true

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val callback = object : OnBackPressedCallback(
            true
        ) {
            override fun handleOnBackPressed() {
                val action =
                    UpdateExpenseSubGroupFragmentDirections.actionNavigationUpdateExpenseSubGroupToNavigationSettingsGroupsAndSubGroupsSettingsFragment()
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
        _binding = FragmentUpdateExpenseSubGroupBinding.inflate(inflater, container, false)

        binding.reusable.addSubGroupNameButton.text = "Update"

        updateSubGroupViewModel.getExpenseSubGroupByIdLiveData(args.expenseSubGroupId)
            ?.observe(viewLifecycleOwner) { expenseSubGroup ->
                expenseSubGroupOldGlobal = ExpenseSubGroup(
                    expenseSubGroup.id,
                    expenseSubGroup.name,
                    expenseSubGroup.description,
                    expenseSubGroup.expenseGroupId,
                    expenseSubGroup.archivedDate)

                binding.reusable.subGroupNameEditText.setText(expenseSubGroup.name)
                binding.reusable.subGroupDescriptionEditText.setText(expenseSubGroup.description)
            }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initGroupSpinner()

        binding.reusable.addSubGroupNameButton.setOnClickListener {
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
            val newExpenseGroupId = groupSpinnerItem?.id

            updateSubGroupViewModel.getExpenseGroupWithExpenseSubGroupsByExpenseGroupId(newExpenseGroupId)
                .observe(viewLifecycleOwner) { groupWithSubGroups ->
                    val subGroups = groupWithSubGroups.expenseSubGroups.map { it.name }.toMutableList()

                    if ((expenseSubGroupOldGlobal?.name != newSubGroupNameBinding
                                || expenseSubGroupOldGlobal?.expenseGroupId != newExpenseGroupId)
                        && subGroups.contains(newSubGroupNameBinding)) {
                        showExistingDialog(requireContext(), "This sub group `$newSubGroupNameBinding` is already existing in `${newGroupNameBinding}` group.")
                    } else {
                        updateSubGroup(newSubGroupNameBinding, newDescriptionBinding, newExpenseGroupId)
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
            UpdateExpenseSubGroupFragmentDirections.actionNavigationUpdateExpenseSubGroupToNavigationSettingsGroupsAndSubGroupsSettingsFragment()
        findNavController().navigate(action)
    }

    private fun isValidForm(newSubGroupNameBinding: String, newGroupNameBinding: String): Boolean {
        val subGroupNameValidation = EmptyValidator(newSubGroupNameBinding).validate()
        binding.reusable.subGroupNameTextInputLayout.error =
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
        newExpenseGroupId: Long?
    ) {
        val updatedSubGroup = ExpenseSubGroup(
            id = expenseSubGroupOldGlobal?.id,
            name = newSubGroupNameBinding,
            description = newDescriptionBinding,
            expenseGroupId = newExpenseGroupId!!,
            archivedDate = expenseSubGroupOldGlobal?.archivedDate
        )
        updateSubGroupViewModel.updateExpenseSubGroup(updatedSubGroup)
    }

    private fun getGroupSpinnerItemByName(nameGroup: String): SpinnerItem? {
        return expenseGroupsGlobal?.find { it.name == nameGroup }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initGroupSpinner() {
        updateSubGroupViewModel.getAllExpenseGroupNotArchivedLiveData()
            .observe(viewLifecycleOwner) { expenseGroups ->
                val spinnerItems = getExpenseGroupList(expenseGroups)

                val spinnerAdapter =
                    GroupSpinnerAdapter(requireContext(), R.layout.item_with_del, spinnerItems,
                        Constants.ADD_NEW_EXPENSE_GROUP)

                expenseGroupsGlobal = spinnerItems

                binding.reusable.groupSpinner.setAdapter(spinnerAdapter)

                val groupName = spinnerItems.find { it.id == expenseSubGroupOldGlobal?.expenseGroupId }?.name
                binding.reusable.groupSpinner.setText(groupName, false)
            }
    }

    private fun getExpenseGroupList(groups: List<ExpenseGroup>?): ArrayList<SpinnerItem> {
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
