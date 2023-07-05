package com.romandevyatov.bestfinance.ui.fragments.addiction.expense

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.R
import com.romandevyatov.bestfinance.databinding.FragmentAddExpenseSubGroupBinding
import com.romandevyatov.bestfinance.db.entities.ExpenseSubGroup
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.ExpenseGroupViewModel
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.ExpenseSubGroupViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddExpenseSubGroupFragment : Fragment() {

    private var _binding: FragmentAddExpenseSubGroupBinding? = null
    private val binding get() = _binding!!

    private val expenseSubGroupViewModel: ExpenseSubGroupViewModel by viewModels()
    private val expenseGroupViewModel: ExpenseGroupViewModel by viewModels()

    val args: AddExpenseSubGroupFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddExpenseSubGroupBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    private fun getArraySpinner(): ArrayAdapter<String> {
        val spinnerAdapter: ArrayAdapter<String> =
            object : ArrayAdapter<String>(requireContext(), R.layout.support_simple_spinner_dropdown_item) {

                override fun isEnabled(position: Int): Boolean {
                    return position != 0
                }

                override fun areAllItemsEnabled(): Boolean {
                    return false
                }

                override fun getDropDownView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup
                ): View {
                    val view: TextView = super.getDropDownView(position, convertView, parent) as TextView
                    if (position == 0) {
                        view.setTextColor(Color.GRAY)
                    } else {

                    }

                    return view
                }
            }

        return spinnerAdapter
    }

    private fun initExpenseGroupSpinner() {
        val spinnerAdapter = getArraySpinner()

        expenseGroupViewModel.expenseGroupsLiveData.observe(viewLifecycleOwner) { expenseGroupList ->
            spinnerAdapter.clear()
            spinnerAdapter.add("Expense group")
            expenseGroupList?.forEach { it ->
                spinnerAdapter.add(it.name)
            }

            if (args.expenseGroupName != null && args.expenseGroupName!!.isNotBlank()) {
                val spinnerPosition = spinnerAdapter.getPosition(args.expenseGroupName.toString())
                binding.toExpenseGroupSpinner.setSelection(spinnerPosition)
            }
        }

        val expenseGroupSpinner = binding.toExpenseGroupSpinner
        expenseGroupSpinner.adapter = spinnerAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initExpenseGroupSpinner()

        binding.addNewExpenseSubGroupNameButton.setOnClickListener {
            val selectedExpenseGroupName = binding.toExpenseGroupSpinner.selectedItem.toString()
            expenseGroupViewModel.getExpenseGroupByNameAndArchivedDateIsNull(selectedExpenseGroupName).observe(viewLifecycleOwner) {
                expenseSubGroupViewModel.insertExpenseSubGroup(
                    ExpenseSubGroup(
                        name = binding.newExpenseSubGroupName.text.toString(),
                        expenseGroupId = it.id!!
                    )
                )
            }

            val action = AddExpenseSubGroupFragmentDirections.actionNavigationAddNewExpenseSubGroupToNavigationAddExpense()
            action.expenseGroupName = selectedExpenseGroupName
            action.expenseSubGroupName = binding.newExpenseSubGroupName.text.toString()
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
