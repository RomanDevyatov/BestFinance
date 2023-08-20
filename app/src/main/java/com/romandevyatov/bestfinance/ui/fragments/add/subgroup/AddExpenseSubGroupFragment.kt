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
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.data.entities.ExpenseGroup
import com.romandevyatov.bestfinance.databinding.FragmentAddExpenseSubGroupBinding
import com.romandevyatov.bestfinance.data.entities.ExpenseSubGroup
import com.romandevyatov.bestfinance.data.validation.EmptyValidator
import com.romandevyatov.bestfinance.ui.adapters.spinner.GroupSpinnerAdapter
import com.romandevyatov.bestfinance.ui.adapters.spinner.SpinnerItem
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

    private val clickDelay = 1000
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
            binding.subGroupNameTextInputLayout.error = if (!subGroupNameValidation.isSuccess) getString(subGroupNameValidation.message) else null

            val groupMaterialSpinnerValidation = EmptyValidator(selectedGroupNameBinding).validate()
            binding.groupSpinnerLayout.error = if (!groupMaterialSpinnerValidation.isSuccess) getString(groupMaterialSpinnerValidation.message) else null

            if (subGroupNameValidation.isSuccess
                && groupMaterialSpinnerValidation.isSuccess
            ) {
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
                            "This sub group `$subGroupNameBinding` is already existing."
                        )
                    } else {
                        showUnarchiveDialog(
                            requireContext(),
                            subGroup,
                            "The sub group with this name is archived. Do you want to unarchive `${subGroupNameBinding}` expense sub group?")
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
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_alert)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val tvMessage: TextView = dialog.findViewById(R.id.tvMessage)
        val btnYes: Button = dialog.findViewById(R.id.btnYes)
        val bntNo: Button = dialog.findViewById(R.id.btnNo)

        tvMessage.text = message

        btnYes.setOnClickListener {
            addSubGroupViewModel.unarchiveExpenseSubGroup(subGroup)
            dialog.dismiss()
        }

        bntNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

}
