package com.romandevyatov.bestfinance.ui.fragments.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.romandevyatov.bestfinance.databinding.FragmentExpenseHistoryBinding
import com.romandevyatov.bestfinance.db.entities.ExpenseGroup
import com.romandevyatov.bestfinance.ui.adapters.history.ExpenseHistoryAdapter
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.ExpenseGroupViewModel
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.ExpenseHistoryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExpenseHistoryFragment : Fragment() {

    private var _binding: FragmentExpenseHistoryBinding? = null
    private val binding get() = _binding!!

    private val expenseHistoryViewModel: ExpenseHistoryViewModel by viewModels()
    private val groupViewModel: ExpenseGroupViewModel by viewModels()
    private var expenseHistoryAdapter: ExpenseHistoryAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpenseHistoryBinding.inflate(inflater, container, false)

        initRecyclerView()

        return binding.root
    }

    private fun initRecyclerView() {
        binding.expenseHistoryRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        groupViewModel.allExpenseGroupsNotArchivedLiveData.observe(viewLifecycleOwner) { groups ->
            val expenseGroupMap: Map<Long?, ExpenseGroup> = groups.associateBy { it.id }

            expenseHistoryAdapter = ExpenseHistoryAdapter(expenseGroupMap)
            binding.expenseHistoryRecyclerView.adapter = expenseHistoryAdapter
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        expenseHistoryViewModel.allExpenseHistoryWithExpenseGroupAndWalletLiveData.observe(viewLifecycleOwner) {
            expenseHistoryAdapter?.submitList(it)
        }
    }

}
