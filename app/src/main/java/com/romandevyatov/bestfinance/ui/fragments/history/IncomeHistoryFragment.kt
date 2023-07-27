package com.romandevyatov.bestfinance.ui.fragments.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.romandevyatov.bestfinance.databinding.FragmentIncomeHistoryBinding
import com.romandevyatov.bestfinance.db.entities.IncomeGroup
import com.romandevyatov.bestfinance.ui.adapters.history.IncomeHistoryAdapter
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.IncomeGroupViewModel
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.IncomeHistoryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IncomeHistoryFragment : Fragment() {

    private var _binding: FragmentIncomeHistoryBinding? = null
    private val binding get() = _binding!!

    private val incomeHistoryViewModel: IncomeHistoryViewModel by viewModels()
    private val groupViewModel: IncomeGroupViewModel by viewModels()
    private var incomeHistoryAdapter: IncomeHistoryAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIncomeHistoryBinding.inflate(inflater, container, false)

        initRecyclerView()

        return binding.root
    }

    private fun initRecyclerView() {
        binding.incomeHistoryRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        groupViewModel.getAllIncomeGroupNotArchivedLiveData().observe(viewLifecycleOwner) { groups ->
            val incomeGroupMap: Map<Long?, IncomeGroup> = groups.associateBy { it.id }

            incomeHistoryAdapter = IncomeHistoryAdapter(incomeGroupMap)
            binding.incomeHistoryRecyclerView.adapter = incomeHistoryAdapter
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        incomeHistoryViewModel.allIncomeHistoryWithIncomeGroupAndWalletLiveData.observe(viewLifecycleOwner) { allIncomeHistoryWithIncomeGroupAndWallet ->
                incomeHistoryAdapter?.submitList(allIncomeHistoryWithIncomeGroupAndWallet.reversed())
        }

    }


}