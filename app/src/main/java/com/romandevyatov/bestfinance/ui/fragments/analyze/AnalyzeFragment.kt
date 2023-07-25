package com.romandevyatov.bestfinance.ui.fragments.analyze

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.romandevyatov.bestfinance.databinding.FragmentAnalyzeBinding
import com.romandevyatov.bestfinance.ui.adapters.analyze.ExpandableGroupAdapter
import com.romandevyatov.bestfinance.ui.adapters.analyze.models.ParentData
import com.romandevyatov.bestfinance.utils.Constants
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.ExpenseGroupViewModel
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.ExpenseHistoryViewModel
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.IncomeGroupViewModel
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.IncomeHistoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class AnalyzeFragment : Fragment() {

    private var _binding: FragmentAnalyzeBinding? = null
    private val binding get() = _binding!!

    private val incomeGroupViewModel: IncomeGroupViewModel by viewModels()
    private val expenseGroupViewModel: ExpenseGroupViewModel by viewModels()
    private val incomeHistoryViewModel: IncomeHistoryViewModel by viewModels()
    private val expenseHistoryViewModel: ExpenseHistoryViewModel by viewModels()
    private lateinit var expandableGroupAdapter: ExpandableGroupAdapter

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

        incomeGroupViewModel.allIncomeGroupWithIncomeSubGroupsIncludingIncomeHistoryAndNotArchivedLiveData.observe(viewLifecycleOwner) { incomeGroupWithIncomeSubGroupsIncludingIncomeHistories ->
            expenseGroupViewModel.allExpenseGroupWithExpenseSubGroupsIncludingExpenseHistoryAndLiveData.observe(
                viewLifecycleOwner
            ) { expenses ->
                val apd = ParentData(
                    analyzeParentTitle = Constants.INCOMES,
                    type = Constants.INCOMINGS_PARENT_TYPE,
                    subParentNestedListIncomings = incomeGroupWithIncomeSubGroupsIncludingIncomeHistories
                )
                mList.add(apd)

                val parentData = ParentData(
                    analyzeParentTitle = Constants.EXPENSE,
                    type = Constants.EXPENSES_PARENT_TYPE,
                    subParentNestedListExpenses = expenses
                )
                mList.add(parentData)

                expandableGroupAdapter = ExpandableGroupAdapter(mList)
                binding.analyzeGroupRecycler.adapter = expandableGroupAdapter
                binding.analyzeGroupRecycler.layoutManager = LinearLayoutManager(requireContext())
            }

            incomeHistoryViewModel.incomeHistoryLiveData.observe(viewLifecycleOwner) { history ->
                val totalIncomeValue = history.sumOf { it.amount }
                expenseHistoryViewModel.expenseHistoryLiveData.observe(viewLifecycleOwner) { expenseHistory ->
                    val totalExpensesValue = expenseHistory.sumOf { it.amount }
                    binding.analyzeGroupTextView.text =
                        ((totalIncomeValue.minus(totalExpensesValue) * 100.0).roundToInt() / 100.0).toString()
                }

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
