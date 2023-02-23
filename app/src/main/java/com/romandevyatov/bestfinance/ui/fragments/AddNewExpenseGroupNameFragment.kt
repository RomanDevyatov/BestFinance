package com.romandevyatov.bestfinance.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.romandevyatov.bestfinance.databinding.FragmentAddNewExpenseGroupBinding
import com.romandevyatov.bestfinance.db.entities.ExpenseGroup
import com.romandevyatov.bestfinance.viewmodels.ExpenseGroupViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddNewExpenseGroupNameFragment : Fragment() {

    private var _binding: FragmentAddNewExpenseGroupBinding? = null
    private val binding get() = _binding!!

    private val expenseGroupViewModel: ExpenseGroupViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddNewExpenseGroupBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addNewExpenseGroupNameButton.setOnClickListener {
            expenseGroupViewModel.insertExpenseGroup(
                ExpenseGroup(
                    name = binding.newExpenseGroupName.text.toString()
                )
            )
            val action = AddNewExpenseGroupNameFragmentDirections.actionNavigationAddNewExpenseGroupToNavigationAddExpense()
            action.expenseGroupName = binding.newExpenseGroupName.text.toString()
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
