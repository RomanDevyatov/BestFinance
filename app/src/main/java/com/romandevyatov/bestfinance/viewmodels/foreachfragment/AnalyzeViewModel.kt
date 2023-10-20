package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.romandevyatov.bestfinance.data.entities.ExpenseHistory
import com.romandevyatov.bestfinance.data.entities.IncomeHistory
import com.romandevyatov.bestfinance.data.entities.IncomeSubGroup
import com.romandevyatov.bestfinance.data.entities.relations.ExpenseGroupWithExpenseSubGroupsIncludingExpenseHistories
import com.romandevyatov.bestfinance.data.entities.relations.IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories
import com.romandevyatov.bestfinance.data.repositories.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AnalyzeViewModel @Inject constructor(
    private val incomeHistoryRepository: IncomeHistoryRepository,
    private val expenseHistoryRepository: ExpenseHistoryRepository,
    private val expenseGroupRepository: ExpenseGroupRepository,
    private val incomeGroupRepository: IncomeGroupRepository
) : ViewModel() {

    fun getIncomeHistoriesWhereSubGroupIsNullLiveData(): LiveData<List<IncomeHistory>> {
        return incomeHistoryRepository.getIncomeHistoriesWhereSubGroupIsNullLiveData()
    }

    fun getExpenseHistoriesWhereSubGroupIsNullLiveData(): LiveData<List<ExpenseHistory>> {
        return expenseHistoryRepository.getExpenseHistoriesWhereSubGroupIsNullLiveData()
    }

    val allIncomeGroupWithIncomeSubGroupsIncludingIncomeHistoryAndNotArchivedLiveData: LiveData<List<IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories>> = incomeGroupRepository.getAllIncomeGroupWithIncomeSubGroupsIncludingIncomeHistoriesNotArchivedLiveData()

    val allExpenseGroupWithExpenseSubGroupsIncludingExpenseHistoryAndLiveData: LiveData<List<ExpenseGroupWithExpenseSubGroupsIncludingExpenseHistories>> = expenseGroupRepository.getAllExpenseGroupWithExpenseSubGroupsIncludingExpenseHistoriesLiveData()

    val incomeHistoryLiveData: LiveData<List<IncomeHistory>> = incomeHistoryRepository.getAllIncomeHistory()

    val expenseHistoryLiveData: LiveData<List<ExpenseHistory>> = expenseHistoryRepository.getAllExpenseHistory()


}