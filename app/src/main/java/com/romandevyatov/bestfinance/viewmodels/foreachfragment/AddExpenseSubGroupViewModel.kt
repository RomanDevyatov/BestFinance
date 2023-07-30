package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.ExpenseGroup
import com.romandevyatov.bestfinance.data.entities.ExpenseSubGroup
import com.romandevyatov.bestfinance.data.repositories.ExpenseGroupRepository
import com.romandevyatov.bestfinance.data.repositories.ExpenseSubGroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddExpenseSubGroupViewModel @Inject constructor(
    private val expenseSubGroupRepository: ExpenseSubGroupRepository,
    private val expenseGroupRepository: ExpenseGroupRepository
) : ViewModel() {

    val allExpenseGroupsNotArchivedLiveData: LiveData<List<ExpenseSubGroup>> = expenseSubGroupRepository.getAllExpenseGroupsNotArchivedLiveData()

    val expenseSubGroupsLiveData: LiveData<List<ExpenseSubGroup>> = expenseSubGroupRepository.getAllExpenseSubGroups()

    fun insertExpenseSubGroup(expenseSubGroup: ExpenseSubGroup) = viewModelScope.launch(Dispatchers.IO) {
        expenseSubGroupRepository.insertExpenseSubGroup(expenseSubGroup)
    }

    fun updateExpenseSubGroup(expenseSubGroup: ExpenseSubGroup) = viewModelScope.launch(Dispatchers.IO) {
        expenseSubGroupRepository.updateExpenseSubGroup(expenseSubGroup)
    }

    fun deleteExpenseSubGroup(expenseSubGroup: ExpenseSubGroup) = viewModelScope.launch(Dispatchers.IO) {
        expenseSubGroupRepository.deleteExpenseSubGroup(expenseSubGroup)
    }

    fun deleteAllExpenseSubGroup() = viewModelScope.launch(Dispatchers.IO) {
        expenseSubGroupRepository.deleteAllExpenseSubGroups()
    }

    val expenseSubGroupsWhereArchivedDateIsNullLiveData: LiveData<List<ExpenseSubGroup>> = expenseSubGroupRepository.getAllExpenseGroupsNotArchivedLiveData()

    fun getExpenseSubGroupByNameWhereArchivedDateIsNull(name: String): LiveData<ExpenseSubGroup> {
        return expenseSubGroupRepository.getExpenseSubGroupByNameNotArchivedLiveData(name)
    }

    fun getExpenseGroupNotArchivedByNameLiveData(selectedExpenseGroupName: String): LiveData<ExpenseGroup>  {
        return expenseGroupRepository.getExpenseGroupNotArchivedByNameLiveData(selectedExpenseGroupName)
    }

    fun getExpenseSubGroupByNameLiveData(name: String): LiveData<ExpenseSubGroup> {
        return expenseSubGroupRepository.getExpenseSubGroupByNameLiveData(name)
    }

    fun unarchiveExpenseSubGroup(expenseSubGroup: ExpenseSubGroup) = viewModelScope.launch(Dispatchers.IO) {
        val expenseSubGroupUnarchived = ExpenseSubGroup(
            id = expenseSubGroup.id,
            name = expenseSubGroup.name,
            description = expenseSubGroup.description,
            expenseGroupId = expenseSubGroup.expenseGroupId,
            archivedDate = null
        )
        updateExpenseSubGroup(expenseSubGroupUnarchived)
    }

    fun getExpenseSubGroupByNameWithExpenseGroupIdLiveData(subGroupNameBinding: String, groupId: Long?): LiveData<ExpenseSubGroup>? {
        return expenseSubGroupRepository.getExpenseSubGroupByNameWithExpenseGroupIdLiveData(subGroupNameBinding, groupId)
    }


}
