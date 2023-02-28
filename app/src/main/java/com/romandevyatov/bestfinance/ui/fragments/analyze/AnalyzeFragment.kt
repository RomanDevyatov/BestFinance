package com.romandevyatov.bestfinance.ui.fragments.analyze

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.romandevyatov.bestfinance.databinding.FragmentAnalyzeBinding
import com.romandevyatov.bestfinance.db.entities.mediator.ParentData
import com.romandevyatov.bestfinance.db.entities.relations.IncomeSubGroupWithIncomeHistories
import com.romandevyatov.bestfinance.ui.adapters.analyze.ExpandableRecyclerAdapter
import com.romandevyatov.bestfinance.viewmodels.IncomeGroupViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AnalyzeFragment : Fragment() {

    private var _binding: FragmentAnalyzeBinding? = null
    private val binding get() = _binding!!

    private val incomeGroupViewModel: IncomeGroupViewModel by viewModels()
    private val analyzeAdapter: ExpandableRecyclerAdapter = ExpandableRecyclerAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalyzeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mList: ArrayList<ParentData> = ArrayList()

        incomeGroupViewModel.allNotArchivedIncomeGroupWithIncomeSubGroupsIncludingIncomeHistoryAndLiveData.observe(viewLifecycleOwner) { incomeGroupWithIncomeSubGroupsIncludingIncomeHistories ->
            val listData = incomeGroupWithIncomeSubGroupsIncludingIncomeHistories.map {
                ParentData(
                    parentTitle = it.incomeGroup.name,
                    nestedList = it.incomeSubGroupWithIncomeHistories as MutableList<IncomeSubGroupWithIncomeHistories>
                )
            }

            mList.addAll(listData)
            analyzeAdapter.submitList(mList)
        }

        binding.exRecycle.adapter = analyzeAdapter
        binding.exRecycle.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
