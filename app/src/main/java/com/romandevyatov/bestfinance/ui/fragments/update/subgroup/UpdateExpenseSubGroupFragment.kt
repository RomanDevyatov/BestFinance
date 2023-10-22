package com.romandevyatov.bestfinance.ui.fragments.update.subgroup

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
import com.romandevyatov.bestfinance.data.entities.ExpenseSubGroup
import com.romandevyatov.bestfinance.data.validation.EmptyValidator
import com.romandevyatov.bestfinance.databinding.FragmentUpdateExpenseSubGroupBinding
import com.romandevyatov.bestfinance.ui.adapters.spinner.GroupSpinnerAdapter
import com.romandevyatov.bestfinance.ui.adapters.spinner.SpinnerItem
import com.romandevyatov.bestfinance.utils.Constants
import com.romandevyatov.bestfinance.utils.WindowUtil
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

    private var isButtonClickable = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateExpenseSubGroupBinding.inflate(inflater, container, false)

        setOnBackPressedHandler()

        binding.reusable.addSubGroupNameButton.text = getString(R.string.update)

        updateSubGroupViewModel.getExpenseSubGroupByIdLiveData(args.expenseSubGroupId)
            .observe(viewLifecycleOwner) { expenseSubGroup ->
                expenseSubGroup?.let {
                    expenseSubGroupOldGlobal = it.copy()

                    binding.reusable.subGroupNameEditText.setText(it.name)
                    binding.reusable.subGroupDescriptionEditText.setText(it.description)
                }
            }

        return binding.root
    }

    private fun setOnBackPressedHandler() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateToSettingGroupsAndSubGroups()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
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
                    groupWithSubGroups?.let { group ->
                        val subGroups = group.expenseSubGroups.map { it.name }.toMutableList()

                        if ((expenseSubGroupOldGlobal?.name != newSubGroupNameBinding
                                    || expenseSubGroupOldGlobal?.expenseGroupId != newExpenseGroupId)
                            && subGroups.contains(newSubGroupNameBinding)) {
                            WindowUtil.showExistingDialog(requireContext(), getString(R.string.sub_group_already_exist_in_group, newSubGroupNameBinding, newGroupNameBinding))
                        } else {
                            updateSubGroup(newSubGroupNameBinding, newDescriptionBinding, newExpenseGroupId)
                            navigateToSettingGroupsAndSubGroups()
                        }
                    }
                }
        }

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            isButtonClickable = true
            view.isEnabled = true
        }, Constants.CLICK_DELAY_MS)
    }

    private fun navigateToSettingGroupsAndSubGroups() {
        val action =
            UpdateExpenseSubGroupFragmentDirections.actionNavigationUpdateExpenseSubGroupToNavigationSettingsGroupsAndSubGroupsSettingsFragment()
        action.initialTabIndex = 1
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
                expenseGroups?.takeIf { it.isNotEmpty() }?.let {
                    val spinnerItems = getExpenseGroupList(it)

                    val spinnerAdapter = GroupSpinnerAdapter(
                        requireContext(), R.layout.item_with_del, spinnerItems, Constants.ADD_NEW_EXPENSE_GROUP
                    )

                    expenseGroupsGlobal = spinnerItems
                    binding.reusable.groupSpinner.setAdapter(spinnerAdapter)

                    val groupName = it.find { group -> group.id == expenseSubGroupOldGlobal?.expenseGroupId }?.name
                    binding.reusable.groupSpinner.setText(groupName, false)
                }
            }
    }

    private fun getExpenseGroupList(groups: List<ExpenseGroup>?): ArrayList<SpinnerItem> {
        val spinnerItems = ArrayList<SpinnerItem>()

        groups?.forEach {
            spinnerItems.add(SpinnerItem(it.id, it.name))
        }

        return spinnerItems
    }

}
