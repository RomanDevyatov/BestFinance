package com.romandevyatov.bestfinance.ui.fragments.addictions.expense

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.romandevyatov.bestfinance.databinding.FragmentAddExpenseSubGroupBinding
import com.romandevyatov.bestfinance.db.entities.ExpenseSubGroup
import com.romandevyatov.bestfinance.ui.adapters.spinnerutils.SpinnerUtils
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
            val selectedExpenseGroupName = binding.groupSpinner.selectedItem.toString()

            val expenseSubGroupNameBinding = binding.subGroupNameEditText.text.toString()

            expenseGroupViewModel.getExpenseGroupByNameAndArchivedDateIsNull(selectedExpenseGroupName).observe(viewLifecycleOwner) {
                val expenseGroupId = it.id!!

                val descriptionBinding = binding.subGroupDescriptionEditText.text.toString()

                val newExpenseSubGroup = ExpenseSubGroup(
                    name = expenseSubGroupNameBinding,
                    description = descriptionBinding,
                    expenseGroupId = expenseGroupId
                )

                expenseSubGroupViewModel.insertExpenseSubGroup(newExpenseSubGroup)

                val action = AddExpenseSubGroupFragmentDirections.actionNavigationAddNewExpenseSubGroupToNavigationAddExpense()
                action.expenseGroupName = selectedExpenseGroupName
                action.expenseSubGroupName = expenseSubGroupNameBinding
                findNavController().navigate(action)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initExpenseGroupSpinner() {
        val spinnerAdapter = SpinnerUtils.getArraySpinner(requireContext())

        expenseGroupViewModel.allExpenseGroupsNotArchivedLiveData.observe(viewLifecycleOwner) { expenseGroupList ->
            spinnerAdapter.clear()
            spinnerAdapter.add(Constants.EXPENSE_GROUP)
            expenseGroupList?.forEach { it ->
                spinnerAdapter.add(it.name)
            }

            if (args.expenseGroupName != null && args.expenseGroupName!!.isNotBlank()) {
                val spinnerPosition = spinnerAdapter.getPosition(args.expenseGroupName.toString())
                binding.groupSpinner.setSelection(spinnerPosition)
            }
        }

        val expenseGroupSpinner = binding.groupSpinner
        expenseGroupSpinner.adapter = spinnerAdapter
    }


}
