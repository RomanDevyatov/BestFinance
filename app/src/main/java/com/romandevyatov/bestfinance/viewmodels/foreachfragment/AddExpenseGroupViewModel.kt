package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.ExpenseGroup
import com.romandevyatov.bestfinance.data.entities.relations.ExpenseGroupWithExpenseSubGroups
import com.romandevyatov.bestfinance.data.entities.relations.ExpenseGroupWithExpenseSubGroupsIncludingExpenseHistories
import com.romandevyatov.bestfinance.data.repositories.ExpenseGroupRepository
import com.romandevyatov.bestfinance.data.repositories.ExpenseSubGroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddExpenseGroupViewModel @Inject constructor(
    private val expenseGroupRepository: ExpenseGroupRepository,
    private val expenseSubGroupRepository: ExpenseSubGroupRepository
) : ViewModel() {

    val allExpenseGroupsNotArchivedLiveData: LiveData<List<ExpenseGroup>> = expenseGroupRepository.getAllExpenseGroupsNotArchivedLiveData()

    fun insertExpenseGroup(expenseGroup: ExpenseGroup) = viewModelScope.launch(Dispatchers.IO) {
        val name = expenseGroup.name
        val existingExpenseGroup = expenseGroupRepository.getExpenseGroupByName(name)
        if (existingExpenseGroup == null) {
            expenseGroupRepository.insertExpenseGroup(expenseGroup)
        } else if (existingExpenseGroup.archivedDate != null) {
            val expenseGroupWithExpenseSubGroups = expenseGroupRepository.getExpenseGroupWithExpenseSubGroupsByExpenseGroupName(name)
            expenseGroupRepository.unarchiveExpenseGroup(expenseGroupWithExpenseSubGroups.expenseGroup)

            expenseGroupWithExpenseSubGroups.expenseSubGroups.forEach {
                expenseSubGroupRepository.unarchiveExpenseSubGroup(it)
            }
        }
    }

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

    fun getExpenseGroupByNameAndArchivedDateIsNull(selectedExpenseGroupName: String): LiveData<ExpenseGroup> {
        return expenseGroupRepository.getExpenseGroupNotArchivedByNameLiveData(selectedExpenseGroupName)
    }

    fun getExpenseGroupByNameLiveData(groupNameBinding: String): LiveData<ExpenseGroup>? {
        return expenseGroupRepository.getExpenseGroupNameByNameLiveData(groupNameBinding)
    }

    fun unarchiveExpenseGroup(expenseGroup: ExpenseGroup) = viewModelScope.launch(Dispatchers.IO) {
        val unarchivedExpenseGroup = ExpenseGroup(
            id = expenseGroup.id,
            name = expenseGroup.name,
            description = expenseGroup.description,
            archivedDate = null
        )
        updateExpenseGroup(unarchivedExpenseGroup)
    }

    val allExpenseGroupWithExpenseSubGroupsLiveData: LiveData<List<ExpenseGroupWithExpenseSubGroups>> = expenseGroupRepository.getAllExpenseGroupWithExpenseSubGroupLiveData()

    val allExpenseGroupWithExpenseSubGroupsIncludingExpenseHistoryAndLiveData: LiveData<List<ExpenseGroupWithExpenseSubGroupsIncludingExpenseHistories>> = expenseGroupRepository.getAllExpenseGroupWithExpenseSubGroupsIncludingExpenseHistoriesLiveData()

}
