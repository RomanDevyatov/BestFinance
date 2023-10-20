package com.romandevyatov.bestfinance.ui.fragments.analyze

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.data.entities.IncomeHistory
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

        val mList: ArrayList<ParentData> = ArrayList()

        analyzeViewModel.allIncomeGroupWithIncomeSubGroupsIncludingIncomeHistoryAndNotArchivedLiveData.observe(viewLifecycleOwner) { incomes ->
            analyzeViewModel.allExpenseGroupWithExpenseSubGroupsIncludingExpenseHistoryAndLiveData.observe(
                viewLifecycleOwner
            ) { expenses ->

                analyzeViewModel.getIncomeHistoriesWhereSubGroupIsNullLiveData()
                    .observe(viewLifecycleOwner) {
                        val combinedList: MutableList<IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories> =
                            incomes.toMutableList()
                        val balanceIncomeChangingHistories = gets(it)
                        if (balanceIncomeChangingHistories != null) {
                            combinedList += balanceIncomeChangingHistories
                        }

                        val apd = ParentData(
                            analyzeParentTitle = getString(R.string.incomes),
                            type = Constants.INCOMINGS_PARENT_TYPE,
                            subParentNestedListIncomings = combinedList
                        )
                        mList.add(apd)

                        val parentData = ParentData(
                            analyzeParentTitle = getString(R.string.expenses),
                            type = Constants.EXPENSES_PARENT_TYPE,
                            subParentNestedListExpenses = expenses
                        )
                        mList.add(parentData)

                        expandableGroupAdapter = ExpandableGroupAdapter(mList)
                        binding.analyzeGroupRecycler.adapter = expandableGroupAdapter
                        binding.analyzeGroupRecycler.layoutManager =
                            LinearLayoutManager(requireContext())
                    }
            }

            analyzeViewModel.incomeHistoryLiveData.observe(viewLifecycleOwner) { history ->
                val totalIncomeValue = history.sumOf { it.amount }

                analyzeViewModel.expenseHistoryLiveData.observe(viewLifecycleOwner) { expenseHistory ->
                    val totalExpensesValue = expenseHistory.sumOf { it.amount }

                    binding.analyzeGroupTextView.text =
                        ((totalIncomeValue.minus(totalExpensesValue) * 100.0).roundToInt() / 100.0).toString()
                }
            }
        }
    }

    private fun gets(it: List<IncomeHistory>): IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories? {
        if (it.isNotEmpty()) {
            val incomeSubGroupWithIncomeHistories: ArrayList<IncomeSubGroupWithIncomeHistories> =
                ArrayList()
            incomeSubGroupWithIncomeHistories.add(
                IncomeSubGroupWithIncomeHistories(
                    null,
                    it
                )
            )
            return IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories(
                incomeGroup = null,
                incomeSubGroupWithIncomeHistories = incomeSubGroupWithIncomeHistories
            )
        }

        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
