package com.romandevyatov.bestfinance.ui.fragments.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.romandevyatov.bestfinance.databinding.FragmentIncomeBinding
import com.romandevyatov.bestfinance.db.entities.IncomeGroup
import com.romandevyatov.bestfinance.ui.adapters.utilities.AddItemClickListener
import com.romandevyatov.bestfinance.ui.adapters.income.IncomeGroupAdapter
import com.romandevyatov.bestfinance.ui.adapters.income.ParentIncomeGroupAdapter
import com.romandevyatov.bestfinance.viewmodels.IncomeGroupViewModel
import com.romandevyatov.bestfinance.viewmodels.IncomeHistoryViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class IncomeFragment : Fragment(), AddItemClickListener<IncomeGroup> {

    private lateinit var binding: FragmentIncomeBinding

    private val incomeGroupViewModel: IncomeGroupViewModel by viewModels()
    private val incomeHistoryViewModel: IncomeHistoryViewModel by viewModels()
    private lateinit var incomeGroupAdapter: IncomeGroupAdapter
    private lateinit var parentIncomeGroupAdapter: ParentIncomeGroupAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentIncomeBinding.inflate(inflater, container, false)

        return binding.root
    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        binding = FragmentIncomeBinding.bind(view)
//
//        binding.addIncomeGroupButton.setOnClickListener {
//            val newIncomeGroupName = binding.newIncomeGroupName.text.toString()
//            incomeGroupViewModel.insertIncomeGroup(IncomeGroup(name = newIncomeGroupName))
//
//            Snackbar.make(binding.root, "Group $newIncomeGroupName was added", Snackbar.LENGTH_SHORT).show()
//        }
//
//        incomeGroupViewModel.incomeGroupsLiveData.observe(viewLifecycleOwner) {
//            incomeGroupAdapter.submitList(it)
//        }
//
//        incomeHistoryViewModel. incomeHistoryLiveData.observe(viewLifecycleOwner) { incomeHistories ->
//            val sumOfIncomeHistoryAmount = incomeHistories.sumOf { it.amount }
//            binding.totalIncomeTextView.text = sumOfIncomeHistoryAmount.toString()
//        }
//
//        initRecyclerView()
//    }
//
//    private fun initRecyclerView() {
//        incomeGroupAdapter = IncomeGroupAdapter(this)
//        binding.incomeGroupRecyclerView.layoutManager = LinearLayoutManager(requireContext())
//        binding.incomeGroupRecyclerView.adapter = incomeGroupAdapter
//    }
//
//    override fun deleteItem(item: IncomeGroup) {
//        incomeGroupViewModel.deleteIncomeGroup(item)
//    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentIncomeBinding.bind(view)

        initRecyclerView()

        incomeGroupViewModel.allIncomeGroupWithIncomeSubGroupsIncludingIncomeHistoryAndLiveData.observe(viewLifecycleOwner) {
            parentIncomeGroupAdapter.submitList(it)
        }

        incomeHistoryViewModel.incomeHistoryLiveData.observe(viewLifecycleOwner) { expenseHistories ->
            val sumOfExpenseHistoryAmount = expenseHistories.sumOf { it.amount }
            binding.sumOfIncomeHistoryAmountTextView.text = sumOfExpenseHistoryAmount.toString()
        }

    }


    private fun initRecyclerView() {
        binding.parentRecyclerView.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.VERTICAL, false)
        parentIncomeGroupAdapter = ParentIncomeGroupAdapter(this)
        binding.parentRecyclerView.adapter = parentIncomeGroupAdapter
    }

    override fun addItem(item: IncomeGroup) {
        val action = IncomeFragmentDirections.actionNavigationIncomeToNavigationAddIncome()
        action.incomeGroupName = item.name
        findNavController().navigate(action)
    }

}
