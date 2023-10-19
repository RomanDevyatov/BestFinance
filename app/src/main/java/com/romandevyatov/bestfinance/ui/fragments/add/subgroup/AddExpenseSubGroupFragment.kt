package com.romandevyatov.bestfinance.ui.fragments.add.subgroup

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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.data.entities.ExpenseGroup
import com.romandevyatov.bestfinance.data.entities.ExpenseSubGroup
import com.romandevyatov.bestfinance.data.validation.EmptyValidator
import com.romandevyatov.bestfinance.databinding.DialogAlertBinding
import com.romandevyatov.bestfinance.databinding.FragmentAddExpenseSubGroupBinding
import com.romandevyatov.bestfinance.ui.adapters.spinner.GroupSpinnerAdapter
import com.romandevyatov.bestfinance.ui.adapters.spinner.SpinnerItem
import com.romandevyatov.bestfinance.utils.Constants
import com.romandevyatov.bestfinance.utils.WindowUtil
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.AddExpenseSubGroupViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddExpenseSubGroupFragment : Fragment() {

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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initGroupSpinner()

        binding.addSubGroupNameButton.setOnClickListener {
            if (!isButtonClickable) return@setOnClickListener
            isButtonClickable = false
            view.isEnabled = false

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
                )?.observe(viewLifecycleOwner) { subGroup ->

                    if (subGroup == null) {
                        addIncomeSubGroup(subGroupNameBinding, descriptionBinding, groupId)

                        navigateToAddExpense(selectedGroupNameBinding, subGroupNameBinding)
                    } else if (subGroup.archivedDate == null) {
                        WindowUtil.showExistingDialog(
                            requireContext(),
                            getString(R.string.sub_group_exists_message, subGroupNameBinding)
                        )
                    } else {
                        showUnarchiveDialog(
                            requireContext(),
                            subGroup,
                            getString(R.string.confirm_unarchive_message, subGroupNameBinding)
                        )
                    }
                }
            }

            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                isButtonClickable = true
                view.isEnabled = true
            }, Constants.CLICK_DELAY_MS.toLong())
        }
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
            val spinnerItems = getExpenseGroupList(expenseGroupList)

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

    private fun getExpenseGroupList(groups: List<ExpenseGroup>): MutableList<SpinnerItem> {
        return groups.map {
            SpinnerItem(it.id, it.name)
        }.toMutableList()
    }

    private fun showUnarchiveDialog(
        context: Context,
        subGroup: ExpenseSubGroup,
        message: String
    ) {
        val binding = DialogAlertBinding.inflate(LayoutInflater.from(context))
        val dialog = Dialog(context)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.tvMessage.text = message

        binding.btnYes.setOnClickListener {
            addSubGroupViewModel.unarchiveExpenseSubGroup(subGroup)
            dialog.dismiss()
        }

        binding.btnNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

}
