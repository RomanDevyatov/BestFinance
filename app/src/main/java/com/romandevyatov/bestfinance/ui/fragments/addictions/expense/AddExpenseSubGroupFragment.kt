package com.romandevyatov.bestfinance.ui.fragments.addictions.expense

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.romandevyatov.bestfinance.databinding.FragmentAddExpenseSubGroupBinding
import com.romandevyatov.bestfinance.db.entities.ExpenseGroup
import com.romandevyatov.bestfinance.db.entities.ExpenseSubGroup
import com.romandevyatov.bestfinance.ui.validators.EmptyValidator
import com.romandevyatov.bestfinance.utils.Constants
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.ExpenseGroupViewModel
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.ExpenseSubGroupViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddExpenseSubGroupFragment : Fragment() {

    private var _binding: FragmentAddExpenseSubGroupBinding? = null
    private val binding get() = _binding!!

    private val expenseSubGroupViewModel: ExpenseSubGroupViewModel by viewModels()
    private val expenseGroupViewModel: ExpenseGroupViewModel by viewModels()

    private val args: AddExpenseSubGroupFragmentArgs by navArgs()

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

        initExpenseGroupSpinner()

        binding.addNewExpenseSubGroupNameButton.setOnClickListener {

            val expenseSubGroupNameBinding = binding.subGroupNameEditText.text.toString()
            val expenseSubGroupDescriptionBinding = binding.subGroupDescriptionEditText.text.toString()
            val selectedExpenseGroupNameBinding = binding.groupSpinnerAutoCompleteTextView.text.toString()

            val expenseSubGroupNameValidation = EmptyValidator(expenseSubGroupNameBinding).validate()
            binding.subGroupNameTextInputLayout.error = if (!expenseSubGroupNameValidation.isSuccess) getString(expenseSubGroupNameValidation.message) else null

            val expenseGroupMaterialSpinnerValidation = EmptyValidator(selectedExpenseGroupNameBinding).validate()
            binding.groupSpinnerAutoCompleteTextView.error = if (!expenseGroupMaterialSpinnerValidation.isSuccess) getString(expenseGroupMaterialSpinnerValidation.message) else null

            if (expenseSubGroupNameValidation.isSuccess
                && expenseGroupMaterialSpinnerValidation.isSuccess
            ) {
                expenseGroupViewModel.getExpenseGroupNotArchivedByNameLiveData(selectedExpenseGroupNameBinding).observe(viewLifecycleOwner) {
                    val expenseGroupId = it.id!!

                    val newExpenseSubGroup = ExpenseSubGroup(
                        name = expenseSubGroupNameBinding,
                        description = expenseSubGroupDescriptionBinding,
                        expenseGroupId = expenseGroupId
                    )

                    expenseSubGroupViewModel.insertExpenseSubGroup(newExpenseSubGroup)

                    val action =
                        AddExpenseSubGroupFragmentDirections.actionNavigationAddNewExpenseSubGroupToNavigationAddExpense()
                    action.expenseGroupName = selectedExpenseGroupNameBinding
                    action.expenseSubGroupName = expenseSubGroupNameBinding
                    findNavController().navigate(action)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initExpenseGroupSpinner() {
        expenseGroupViewModel.allExpenseGroupsNotArchivedLiveData.observe(viewLifecycleOwner) { expenseGroupList ->
            val spinnerItems = getCustomerList(expenseGroupList)

            val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, spinnerItems)

            binding.groupSpinnerAutoCompleteTextView.setAdapter(spinnerAdapter)

//            if (args.expenseGroupName != null && args.expenseGroupName!!.isNotBlank()) {
//                val spinnerPosition = spinnerAdapter.getPosition(args.expenseGroupName.toString())
//                binding.groupSpinnerAutoCompleteTextView.setText(args.expenseGroupName.toString(), false) //.setSelection(spinnerPosition)
//            }

        }
    }

    private fun getCustomerList(expenseGroupList: List<ExpenseGroup>?): ArrayList<String> {
        val spinnerItems = ArrayList<String>()

        expenseGroupList?.forEach { it ->
            spinnerItems.add(it.name)
        }

        return spinnerItems
    }

}
