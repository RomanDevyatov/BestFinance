package com.romandevyatov.bestfinance.ui.fragments.deprecated

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.romandevyatov.bestfinance.databinding.FragmentExpenseBinding
import com.romandevyatov.bestfinance.db.entities.ExpenseGroup
import com.romandevyatov.bestfinance.ui.adapters.transactions_deprecated.expense.ParentExpenseGroupAdapter
import com.romandevyatov.bestfinance.ui.adapters.clicklisteners.AddItemClickListener
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.ExpenseGroupViewModel
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.ExpenseHistoryViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ExpenseFragment : Fragment(),
    AddItemClickListener<ExpenseGroup> {

    private lateinit var binding: FragmentExpenseBinding

    private val expenseViewModel: ExpenseGroupViewModel by viewModels()
    private val expenseHistoryViewModel: ExpenseHistoryViewModel by viewModels()
    private lateinit var parentExpenseGroupAdapter: ParentExpenseGroupAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentExpenseBinding.bind(view)

        initRecyclerView()

        expenseViewModel.allExpenseGroupWithExpenseSubGroupsIncludingExpenseHistoryAndLiveData.observe(viewLifecycleOwner) {
            parentExpenseGroupAdapter.submitList(it)
        }

        expenseHistoryViewModel.expenseHistoryLiveData.observe(viewLifecycleOwner) { expenseHistories ->
            val sumOfExpenseHistoryAmount = expenseHistories.sumOf { it.amount }
            binding.sumOfExpenseHistoryAmountTextView.text = sumOfExpenseHistoryAmount.toString()
        }

        binding.addExpenseGroupButton.setOnClickListener {
//            val action =
//                ExpenseFragmentDirections.actionNavigationExpenseToNavigationAddNewExpenseGroup()
//            findNavController().navigate(action)
        }

    }


    private fun initRecyclerView() {
        binding.parentRecyclerView.layoutManager = LinearLayoutManager(requireContext(),
                    LinearLayoutManager.VERTICAL, false)
        parentExpenseGroupAdapter = ParentExpenseGroupAdapter(this)
        binding.parentRecyclerView.adapter = parentExpenseGroupAdapter
    }

    override fun addItem(item: ExpenseGroup) {
//        val action =
//            ExpenseFragmentDirections.actionNavigationExpenseToNavigationAddExpense()
//        action.expenseGroupName = item.name
//        findNavController().navigate(action)
    }

}
