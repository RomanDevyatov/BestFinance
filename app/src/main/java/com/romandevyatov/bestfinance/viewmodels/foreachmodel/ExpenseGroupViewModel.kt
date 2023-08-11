package com.romandevyatov.bestfinance.viewmodels.foreachmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.ExpenseGroup
import com.romandevyatov.bestfinance.data.entities.relations.ExpenseGroupWithExpenseSubGroups
import com.romandevyatov.bestfinance.data.entities.relations.ExpenseGroupWithExpenseSubGroupsIncludingExpenseHistories
import com.romandevyatov.bestfinance.data.repositories.ExpenseGroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseGroupViewModel @Inject constructor(
    private val expenseGroupRepository: ExpenseGroupRepository
) : ViewModel() {

    val allExpenseGroupLiveData: LiveData<List<ExpenseGroup>> = expenseGroupRepository.getAllExpenseGroupsLiveData()

    fun updateExpenseGroup(expenseGroup: ExpenseGroup) = viewModelScope.launch(Dispatchers.IO) {
        expenseGroupRepository.updateExpenseGroup(expenseGroup)
    }

    fun deleteExpenseGroup(expenseGroup: ExpenseGroup) = viewModelScope.launch(Dispatchers.IO) {
        expenseGroupRepository.deleteExpenseGroup(expenseGroup)
    }

    fun deleteExpenseGroupById(id: Long?) = viewModelScope.launch(Dispatchers.IO) {
        expenseGroupRepository.deleteExpenseGroupById(id)
    }

    fun deleteAllExpenseGroup() = viewModelScope.launch(Dispatchers.IO) {
        expenseGroupRepository.deleteAllExpenseGroups()
    }

    fun getExpenseGroupWithExpenseSubGroupsByExpenseGroupNameAndArchivedDateIsNullLiveData(name: String): LiveData<ExpenseGroupWithExpenseSubGroups> {
        return expenseGroupRepository.getExpenseGroupWithExpenseSubGroupsByExpenseGroupNameLiveData(name)
    }

    fun getExpenseGroupNotArchivedByNameLiveData(selectedExpenseGroupName: String): LiveData<ExpenseGroup> {
        return expenseGroupRepository.getExpenseGroupNotArchivedByNameLiveData(selectedExpenseGroupName)
    }

    val allExpenseGroupWithExpenseSubGroupsLiveData: LiveData<List<ExpenseGroupWithExpenseSubGroups>> = expenseGroupRepository.getAllExpenseGroupWithExpenseSubGroupLiveData()

    val allExpenseGroupWithExpenseSubGroupsIncludingExpenseHistoryAndLiveData: LiveData<List<ExpenseGroupWithExpenseSubGroupsIncludingExpenseHistories>> = expenseGroupRepository.getAllExpenseGroupWithExpenseSubGroupsIncludingExpenseHistoriesLiveData()

}
