package com.romandevyatov.bestfinance.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.db.entities.ExpenseGroup
import com.romandevyatov.bestfinance.db.entities.relations.ExpenseGroupWithExpenseSubGroups
import com.romandevyatov.bestfinance.db.entities.relations.ExpenseGroupWithExpenseSubGroupsIncludingExpenseHistories
import com.romandevyatov.bestfinance.repositories.ExpenseGroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ExpenseGroupViewModel @Inject constructor(
    private val expenseGroupRepository: ExpenseGroupRepository
) : ViewModel() {

    val expenseGroupsLiveData: LiveData<List<ExpenseGroup>> = expenseGroupRepository.getAllExpenseGroups()

    fun insertExpenseGroup(expenseGroup: ExpenseGroup) = viewModelScope.launch(Dispatchers.IO) {
        expenseGroupRepository.insertExpenseGroup(expenseGroup)
    }

    fun updateExpenseGroup(expenseGroup: ExpenseGroup) = viewModelScope.launch(Dispatchers.IO) {
        expenseGroupRepository.updateExpenseGroup(expenseGroup)
    }

    fun deleteExpenseGroup(expenseGroup: ExpenseGroup) = viewModelScope.launch(Dispatchers.IO) {
        expenseGroupRepository.deleteExpenseGroup(expenseGroup)
    }

    fun deleteExpenseGroupById(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        expenseGroupRepository.deleteExpenseGroupById(id)
    }

    fun deleteAllExpenseGroup() = viewModelScope.launch(Dispatchers.IO) {
        expenseGroupRepository.deleteAllExpenseGroups()
    }

    fun getExpenseGroupWithExpenseSubGroupsByExpenseGroupNameLiveData(name: String): LiveData<ExpenseGroupWithExpenseSubGroups>? {
        return expenseGroupRepository.getExpenseGroupWithExpenseSubGroupsByExpenseGroupName(name)
    }

    val allExpenseGroupWithExpenseSubGroupsLiveData: LiveData<List<ExpenseGroupWithExpenseSubGroups>> = expenseGroupRepository.getAllExpenseGroupWithExpenseSubGroup()

    val allExpenseGroupWithExpenseSubGroupsIncludingExpenseHistoryAndLiveData: LiveData<List<ExpenseGroupWithExpenseSubGroupsIncludingExpenseHistories>> = expenseGroupRepository.getAllExpenseGroupWithExpenseSubGroupsIncludingExpenseHistories()



}