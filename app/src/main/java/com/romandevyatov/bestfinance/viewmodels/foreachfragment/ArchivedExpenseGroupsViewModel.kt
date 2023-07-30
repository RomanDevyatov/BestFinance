package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.ExpenseGroup
import com.romandevyatov.bestfinance.data.repositories.ExpenseGroupRepository
import com.romandevyatov.bestfinance.data.repositories.ExpenseSubGroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArchivedExpenseGroupsViewModel @Inject constructor(
    private val expenseGroupRepository: ExpenseGroupRepository,
    private val expenseSubGroupRepository: ExpenseSubGroupRepository
) : ViewModel() {

    fun getExpenseGroupsArchivedByNameLiveData(name: String): LiveData<ExpenseGroup>? {
        return expenseGroupRepository.getExpenseGroupArchivedByNameLiveData(name)
    }

    val allExpenseGroupsArchivedLiveData: LiveData<List<ExpenseGroup>>? = expenseGroupRepository.getAllExpenseGroupArchivedLiveData()

    suspend fun updateExpenseGroup(expenseGroup: ExpenseGroup) = viewModelScope.launch(Dispatchers.IO) {
        expenseGroupRepository.updateExpenseGroup(expenseGroup)
    }

    fun unarchiveExpenseGroup(expenseGroup: ExpenseGroup, isIncludeSubGroups: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        val expenseGroupNotArchived = ExpenseGroup(
            id = expenseGroup.id,
            name = expenseGroup.name,
            description = expenseGroup.description,
            archivedDate = null
        )
        if (isIncludeSubGroups) {
            unarchiveExpenseSubGroupsByExpenseGroupId(expenseGroupNotArchived.id)
        }
        updateExpenseGroup(expenseGroupNotArchived)
    }

    fun unarchiveExpenseSubGroupsByExpenseGroupId(expenseGroupId: Long?) = viewModelScope.launch (Dispatchers.IO) {
        expenseSubGroupRepository.unarchiveExpenseSubGroupsByExpenseGroupId(expenseGroupId)
    }

    fun deleteExpenseGroupByName(id: Long?) = viewModelScope.launch (Dispatchers.IO) {
        expenseGroupRepository.deleteExpenseGroupById(id)
    }
}
