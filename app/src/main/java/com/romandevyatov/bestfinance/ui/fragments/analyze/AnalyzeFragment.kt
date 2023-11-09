package com.romandevyatov.bestfinance.ui.fragments.analyze

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.data.entities.ExpenseHistory
import com.romandevyatov.bestfinance.data.entities.IncomeHistory
import com.romandevyatov.bestfinance.data.entities.relations.ExpenseGroupWithExpenseSubGroupsIncludingExpenseHistories
import com.romandevyatov.bestfinance.data.entities.relations.ExpenseSubGroupWithExpenseHistories
import com.romandevyatov.bestfinance.data.entities.relations.IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories
import com.romandevyatov.bestfinance.data.entities.relations.IncomeSubGroupWithIncomeHistories
import com.romandevyatov.bestfinance.databinding.FragmentAnalyzeBinding
import com.romandevyatov.bestfinance.ui.adapters.analyze.CategoryExpandableAdapter
import com.romandevyatov.bestfinance.ui.adapters.analyze.models.CategoryItem
import com.romandevyatov.bestfinance.ui.adapters.analyze.models.GroupItem
import com.romandevyatov.bestfinance.ui.adapters.analyze.models.SubGroupNameAndSumItem
import com.romandevyatov.bestfinance.utils.BackStackLogger
import com.romandevyatov.bestfinance.utils.TextFormatter.removeTrailingZeros
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.AnalyzeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@AndroidEntryPoint
class AnalyzeFragment : Fragment() {

    private var _binding: FragmentAnalyzeBinding? = null
    private val binding get() = _binding!!

    private val analyzeViewModel: AnalyzeViewModel by viewModels()

    private lateinit var categoryExpandableAdapter: CategoryExpandableAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalyzeBinding.inflate(inflater, container, false)

        BackStackLogger.logBackStack(findNavController())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoryExpandableAdapter = CategoryExpandableAdapter(arrayListOf())
        binding.analyzeGroupRecycler.adapter = categoryExpandableAdapter
        binding.analyzeGroupRecycler.layoutManager = LinearLayoutManager(requireContext())

        setupObservers()
    }

    private fun setupObservers() {
        analyzeViewModel.allIncomeGroupWithIncomeSubGroupsIncludingIncomeHistoryAndNotArchivedLiveData.observe(viewLifecycleOwner) { incomes ->
            analyzeViewModel.allExpenseGroupWithExpenseSubGroupsIncludingExpenseHistoryAndLiveData.observe(viewLifecycleOwner) { expenses ->
                analyzeViewModel.getIncomeHistoriesWhereSubGroupIsNullLiveData().observe(viewLifecycleOwner) { incomesChangingBalance ->
                    val combinedIncomeList = incomes?.toMutableList() ?: mutableListOf()

                    val balanceIncomeChangingHistories = incomesChangingBalance?.let { getIncomeChangingBalanceRecords(it) }

                    if (balanceIncomeChangingHistories != null) {
                        combinedIncomeList += balanceIncomeChangingHistories
                    }

                    val groupIncomeDataList = convertToIncomeCategory(combinedIncomeList)

                    var incomeCategorySum = 0.0
                    for (groupData in groupIncomeDataList) {
                        val groupSumma = groupData.subGroupNameAndSumItem?.sumOf {
                            it.sumOfSubGroup
                        }

                        incomeCategorySum += groupSumma ?: 0.0
                    }

                    val incomesParentData = CategoryItem(
                        categoryName = getString(R.string.incomes),
                        categorySum = incomeCategorySum.toString() + analyzeViewModel.currentDefaultCurrencySymbol,
                        groups = groupIncomeDataList
                    )
                    addToGroupAdapter(incomesParentData)

                    analyzeViewModel.getExpenseHistoriesWhereSubGroupIsNullLiveData().observe(viewLifecycleOwner) { expenseChangingBalance ->
                        val combinedExpenseList = expenses?.toMutableList() ?: mutableListOf()

                        val balanceExpenseChangingHistories = expenseChangingBalance?.let { getExpenseChangingBalanceRecords(it) }

                        if (balanceExpenseChangingHistories != null) {
                            combinedExpenseList += balanceExpenseChangingHistories
                        }

                        val groupExpenseDataList = convertToExpenseCategory(combinedExpenseList)

                        val expenseCategorySum = 0.0
                        for (groupData in groupExpenseDataList) {
                            val groupSumma = groupData.subGroupNameAndSumItem?.sumOf {
                                it.sumOfSubGroup
                            }

                            incomeCategorySum += groupSumma ?: 0.0
                        }

                        val expensesParentData = CategoryItem(
                            categoryName = getString(R.string.expenses),
                            categorySum = expenseCategorySum.toString() + analyzeViewModel.currentDefaultCurrencySymbol,
                            groups = groupExpenseDataList
                        )
                        addToGroupAdapter(expensesParentData)

                        categoryExpandableAdapter = CategoryExpandableAdapter(
                            categoryExpandableAdapter.getList()
                        )

                        binding.analyzeGroupRecycler.adapter = categoryExpandableAdapter
                        binding.analyzeGroupRecycler.layoutManager = LinearLayoutManager(requireContext())
                    }
                }
            }
        }

        analyzeViewModel.incomeHistoryLiveData.observe(viewLifecycleOwner) { histories ->
            val totalIncomeValue = histories?.sumOf { it.amount } ?: 0.0

            analyzeViewModel.expenseHistoryLiveData.observe(viewLifecycleOwner) { expenseHistory ->
                val totalExpensesValue = expenseHistory?.sumOf { it.amount } ?: 0.0
                val totalExpensesValueAbs = totalExpensesValue.absoluteValue
                val result = (((totalIncomeValue - totalExpensesValueAbs) * 100.0).roundToInt() / 100.0).toString()
                val formattedTotalText = removeTrailingZeros(result) + analyzeViewModel.currentDefaultCurrencySymbol

                binding.totalTextView.text = formattedTotalText
            }
        }
    }

    private fun convertToIncomeCategory(incomeGroups: List<IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories>): List<GroupItem> {
        val groupItems = mutableListOf<GroupItem>()

        for (incomeGroup in incomeGroups) {
            val groupName = incomeGroup.incomeGroup?.name ?: getString(R.string.changed_balance)

            val subGroupNameAndSumItemIncomes = incomeGroup.incomeSubGroupWithIncomeHistories.map { groupWithIncomeHistories ->
                val sumOfSubGroup = groupWithIncomeHistories.incomeHistories.sumOf { it.amount }

                SubGroupNameAndSumItem(
                    sumOfSubGroup = sumOfSubGroup,
                    subGroupName = groupWithIncomeHistories.incomeSubGroup?.name ?: ""
                )
            }

            val incomeGroupSum = subGroupNameAndSumItemIncomes.sumOf { it.sumOfSubGroup }
            val formattedIncomeGroupSum = removeTrailingZeros(incomeGroupSum.toString()) + analyzeViewModel.currentDefaultCurrencySymbol

            groupItems.add(
                GroupItem(
                    groupName = groupName,
                    groupSum = formattedIncomeGroupSum,
                    subGroupNameAndSumItem = subGroupNameAndSumItemIncomes
                )
            )
        }

        return groupItems
    }

    private fun convertToExpenseCategory(expenseGroupWithExpenseSubGroupsIncludingExpenseHistories: MutableList<ExpenseGroupWithExpenseSubGroupsIncludingExpenseHistories>): List<GroupItem> {
        val categoryList = mutableListOf<GroupItem>()

        for (expenseHistories in expenseGroupWithExpenseSubGroupsIncludingExpenseHistories) {
            val groupName = expenseHistories.expenseGroupEntity?.name ?: getString(R.string.changed_balance)

            val subGroupNameAndSumItemExpenses = expenseHistories.expenseSubGroupWithExpenseHistories.map { subGroupWithIncomeHistories ->
                val sumOfSubGroup = subGroupWithIncomeHistories.expenseHistory.sumOf { it.amount }

                SubGroupNameAndSumItem(
                    sumOfSubGroup = sumOfSubGroup,
                    subGroupName = subGroupWithIncomeHistories.expenseSubGroup?.name ?: ""
                )
            }

            val expenseGroupSum = subGroupNameAndSumItemExpenses.sumOf { it.sumOfSubGroup }
            val formattedExpenseGroupSum = removeTrailingZeros(expenseGroupSum.toString()) + analyzeViewModel.currentDefaultCurrencySymbol

            categoryList.add(
                GroupItem(
                    groupName = groupName,
                    groupSum = formattedExpenseGroupSum,
                    subGroupNameAndSumItem = subGroupNameAndSumItemExpenses
                )
            )
        }

        return categoryList
    }


    private fun addToGroupAdapter(categoryItem: CategoryItem) {
        val mList = categoryExpandableAdapter.getList()
        mList.add(categoryItem)
        categoryExpandableAdapter.setList(mList)
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
