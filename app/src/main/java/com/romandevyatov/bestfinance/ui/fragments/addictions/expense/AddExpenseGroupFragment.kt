package com.romandevyatov.bestfinance.ui.fragments.addictions.expense

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.romandevyatov.bestfinance.databinding.FragmentAddExpenseGroupBinding
import com.romandevyatov.bestfinance.db.entities.ExpenseGroup
import com.romandevyatov.bestfinance.ui.fragments.addictions.income.AddIncomeGroupFragmentDirections
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.AddExpenseGroupViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddExpenseGroupFragment : Fragment() {

    private var _binding: FragmentAddExpenseGroupBinding? = null
    private val binding get() = _binding!!

    private val expenseGroupViewModel: AddExpenseGroupViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val callback = object : OnBackPressedCallback(
            true
        ) {
            override fun handleOnBackPressed() {
                val action =
                    AddExpenseGroupFragmentDirections.actionNavigationAddNewExpenseGroupToNavigationAddExpense()
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
        _binding = FragmentAddExpenseGroupBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addNewExpenseGroupNameButton.setOnClickListener {
            val expenseGroupNameBinding = binding.newExpenseGroupName.text.toString().trim()
            val descriptionBinding = binding.descriptionEditText.text.toString().trim()

            expenseGroupViewModel.insertExpenseGroup(
                ExpenseGroup(
                    name = expenseGroupNameBinding,
                    description = descriptionBinding
                )
            )

            val action =
                AddExpenseGroupFragmentDirections.actionNavigationAddNewExpenseGroupToNavigationAddExpense()
            action.expenseGroupName = expenseGroupNameBinding
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
