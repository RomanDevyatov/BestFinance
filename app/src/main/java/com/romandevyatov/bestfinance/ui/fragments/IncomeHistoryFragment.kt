package com.romandevyatov.bestfinance.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.romandevyatov.bestfinance.databinding.FragmentIncomeHistoryBinding
import com.romandevyatov.bestfinance.ui.adapters.IncomeHistoryAdapter
import com.romandevyatov.bestfinance.viewmodels.IncomeHistoryViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class IncomeHistoryFragment : Fragment() {

    private var _binding: FragmentIncomeHistoryBinding? = null
    private val binding get() = _binding!!

    private val incomeHistoryViewModel: IncomeHistoryViewModel by viewModels()
    private val incomeHistoryAdapter: IncomeHistoryAdapter = IncomeHistoryAdapter()

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
        binding.incomeHistoryRecyclerView.adapter = incomeHistoryAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        incomeHistoryViewModel.getAllIncomeHistoryWithIncomeGroupAndWallet.observe(viewLifecycleOwner) {
            incomeHistoryAdapter.submitList(it)
        }

//        incomeHistoryViewModel.incomeHistoryLiveData.observe(viewLifecycleOwner) {
//            incomeHistoryAdapter.submitList(it)
//        }

    }


}