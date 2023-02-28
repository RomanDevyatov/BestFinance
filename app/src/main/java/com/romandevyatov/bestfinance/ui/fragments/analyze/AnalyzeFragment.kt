package com.romandevyatov.bestfinance.ui.fragments.analyze

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.romandevyatov.bestfinance.databinding.FragmentAnalyzeBinding
import com.romandevyatov.bestfinance.db.entities.mediator.AnalyzeParentData
import com.romandevyatov.bestfinance.db.entities.mediator.ChildData
import com.romandevyatov.bestfinance.db.entities.mediator.SubParentData
import com.romandevyatov.bestfinance.ui.adapters.analyze.ExpandableAnalyzeGroupAdapter
import com.romandevyatov.bestfinance.utils.Constants
import com.romandevyatov.bestfinance.viewmodels.ExpenseGroupViewModel
import com.romandevyatov.bestfinance.viewmodels.IncomeGroupViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AnalyzeFragment : Fragment() {

    private var _binding: FragmentAnalyzeBinding? = null
    private val binding get() = _binding!!

    private val incomeGroupViewModel: IncomeGroupViewModel by viewModels()
    private val expenseGroupViewModel: ExpenseGroupViewModel by viewModels()
    private lateinit var expandableAnalyzeGroupAdapter: ExpandableAnalyzeGroupAdapter

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

        val mList: ArrayList<AnalyzeParentData> = ArrayList()

        incomeGroupViewModel.allNotArchivedIncomeGroupWithIncomeSubGroupsIncludingIncomeHistoryAndLiveData.observe(viewLifecycleOwner) { incomeGroupWithIncomeSubGroupsIncludingIncomeHistories ->
            val apd = AnalyzeParentData(
                analyzeParentTitle = "Incomings",
                type = Constants.INCOMINGS_PARENT_TYPE,
                subParentNestedList = incomeGroupWithIncomeSubGroupsIncludingIncomeHistories
            )

            mList.add(apd)
            expandableAnalyzeGroupAdapter = ExpandableAnalyzeGroupAdapter(mList)
            binding.analyzeGroupRecycler.adapter = expandableAnalyzeGroupAdapter
            binding.analyzeGroupRecycler.layoutManager = LinearLayoutManager(requireContext())
        }

        expenseGroupViewModel.allExpenseGroupWithExpenseSubGroupsIncludingExpenseHistoryAndLiveData.observe(viewLifecycleOwner) { expenses ->
            val apdr = AnalyzeParentData(
                analyzeParentTitle = "Expenses",
                type = Constants.EXPENSES_PARENT_TYPE,
                subParentNestedListExpenses = expenses
            )

            mList.add(apdr)
            expandableAnalyzeGroupAdapter = ExpandableAnalyzeGroupAdapter(mList)
            binding.analyzeGroupRecycler.adapter = expandableAnalyzeGroupAdapter
            binding.analyzeGroupRecycler.layoutManager = LinearLayoutManager(requireContext())
        }




    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
