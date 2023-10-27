package com.romandevyatov.bestfinance.ui.fragments.analyze

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.data.entities.ExpenseHistory
import com.romandevyatov.bestfinance.data.entities.IncomeHistory
import com.romandevyatov.bestfinance.data.entities.relations.ExpenseGroupWithExpenseSubGroupsIncludingExpenseHistories
import com.romandevyatov.bestfinance.data.entities.relations.ExpenseSubGroupWithExpenseHistories
import com.romandevyatov.bestfinance.data.entities.relations.IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories
import com.romandevyatov.bestfinance.data.entities.relations.IncomeSubGroupWithIncomeHistories
import com.romandevyatov.bestfinance.databinding.FragmentAnalyzeBinding
import com.romandevyatov.bestfinance.ui.adapters.analyze.ExpandableGroupAdapter
import com.romandevyatov.bestfinance.ui.adapters.analyze.models.ParentData
import com.romandevyatov.bestfinance.utils.Constants
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.AnalyzeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class AnalyzeFragment : Fragment() {

    private var _binding: FragmentAnalyzeBinding? = null
    private val binding get() = _binding!!

    private val analyzeViewModel: AnalyzeViewModel by viewModels()

    private lateinit var expandableGroupAdapter: ExpandableGroupAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalyzeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        expandableGroupAdapter = ExpandableGroupAdapter(arrayListOf(), getString(R.string.changed_balance))
        binding.analyzeGroupRecycler.adapter = expandableGroupAdapter
        binding.analyzeGroupRecycler.layoutManager = LinearLayoutManager(requireContext())

        setupObservers()
    }

    private fun setupObservers() {
        analyzeViewModel.allIncomeGroupWithIncomeSubGroupsIncludingIncomeHistoryAndNotArchivedLiveData.observe(viewLifecycleOwner) { incomes ->
            analyzeViewModel.allExpenseGroupWithExpenseSubGroupsIncludingExpenseHistoryAndLiveData.observe(viewLifecycleOwner) { expenses ->
                analyzeViewModel.getIncomeHistoriesWhereSubGroupIsNullLiveData().observe(viewLifecycleOwner) { incomesChangingBalance ->
                    val combinedList = incomes?.toMutableList() ?: mutableListOf()

                    val balanceIncomeChangingHistories = incomesChangingBalance?.let { getIncomeChangingBalanceRecords(it) }

                    if (balanceIncomeChangingHistories != null) {
                        combinedList += balanceIncomeChangingHistories
                    }

                    val incomesParentData = ParentData(
                        analyzeParentTitle = getString(R.string.incomes),
                        type = Constants.INCOMINGS_PARENT_TYPE,
                        subParentNestedListIncomings = combinedList
                    )
                    addParentData(incomesParentData)

                    analyzeViewModel.getExpenseHistoriesWhereSubGroupIsNullLiveData().observe(viewLifecycleOwner) { expenseChangingBalance ->
                        val combinedExpenseList = expenses?.toMutableList() ?: mutableListOf()

                        val balanceExpenseChangingHistories = expenseChangingBalance?.let { getExpenseChangingBalanceRecords(it) }

                        if (balanceExpenseChangingHistories != null) {
                            combinedExpenseList += balanceExpenseChangingHistories
                        }

                        val expensesParentData = ParentData(
                            analyzeParentTitle = getString(R.string.expenses),
                            type = Constants.EXPENSES_PARENT_TYPE,
                            subParentNestedListExpenses = combinedExpenseList
                        )
                        addParentData(expensesParentData)

                        expandableGroupAdapter = ExpandableGroupAdapter(
                            expandableGroupAdapter.getList(),
                            getString(R.string.changed_balance)
                        )

                        binding.analyzeGroupRecycler.adapter = expandableGroupAdapter
                        binding.analyzeGroupRecycler.layoutManager = LinearLayoutManager(requireContext())
                    }
                }
            }
        }

        analyzeViewModel.incomeHistoryLiveData.observe(viewLifecycleOwner) { histories ->
            val totalIncomeValue = histories?.sumOf { it.amount } ?: 0.0

            analyzeViewModel.expenseHistoryLiveData.observe(viewLifecycleOwner) { expenseHistory ->
                val totalExpensesValue = expenseHistory?.sumOf { it.amount } ?: 0.0

                val result = (((totalIncomeValue - totalExpensesValue) * 100.0).roundToInt() / 100.0).toString()

                binding.analyzeGroupTextView.text = result
            }
        }
    }

    private fun addParentData(data: ParentData) {
        val mList = expandableGroupAdapter.getList()
        mList.add(data)
        expandableGroupAdapter.setList(mList)
    }

    private fun getIncomeChangingBalanceRecords(it: List<IncomeHistory>): IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories? {
        if (it.isNotEmpty()) {
            val incomeSubGroupWithIncomeHistories = arrayListOf(IncomeSubGroupWithIncomeHistories(null, it))
            return IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories(null, incomeSubGroupWithIncomeHistories)
        }
        return null
    }

    private fun getExpenseChangingBalanceRecords(it: List<ExpenseHistory>): ExpenseGroupWithExpenseSubGroupsIncludingExpenseHistories? {
        if (it.isNotEmpty()) {
            val expenseSubGroupWithExpenseHistories = arrayListOf(ExpenseSubGroupWithExpenseHistories(null, it))
            return ExpenseGroupWithExpenseSubGroupsIncludingExpenseHistories(null, expenseSubGroupWithExpenseHistories)
        }
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
