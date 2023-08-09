package com.romandevyatov.bestfinance.ui.fragments.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.romandevyatov.bestfinance.data.entities.IncomeGroup
import com.romandevyatov.bestfinance.databinding.FragmentIncomeHistoryBinding
import com.romandevyatov.bestfinance.ui.adapters.history.income.IncomeHistoryAdapter
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
        groupViewModel.allIncomeGroupsLiveData.observe(viewLifecycleOwner) { groups ->
            val incomeGroupMap: Map<Long?, IncomeGroup> = groups.associateBy { it.id }

            val listener = object : IncomeHistoryAdapter.ItemClickListener {

                override fun navigate(id: Long) {
                    val action = HistoryFragmentDirections.actionHistoryFragmentToUpdateIncomeHistoryFragment()
                    action.incomeHistoryId = id
                    findNavController().navigate(action)
                }
            }

            incomeHistoryAdapter = IncomeHistoryAdapter(incomeGroupMap, listener)
            binding.incomeHistoryRecyclerView.adapter = incomeHistoryAdapter
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        incomeHistoryViewModel.allIncomeHistoryWithIncomeSubGroupAndWalletLiveData.observe(viewLifecycleOwner) { allIncomeHistoryWithIncomeGroupAndWallet ->
                incomeHistoryAdapter?.submitList(allIncomeHistoryWithIncomeGroupAndWallet.reversed())
        }

    }


}