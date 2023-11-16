package com.romandevyatov.bestfinance.viewmodels.foreachmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.ExpenseSubGroupEntity
import com.romandevyatov.bestfinance.data.repositories.ExpenseSubGroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseSubGroupViewModel @Inject constructor(
    private val expenseSubGroupRepository: ExpenseSubGroupRepository
) : ViewModel() {

    val expenseSubGroupsLiveDataEntity: LiveData<List<ExpenseSubGroupEntity>> = expenseSubGroupRepository.getAllExpenseSubGroups()

    fun insertExpenseSubGroup(expenseSubGroupEntity: ExpenseSubGroupEntity) = viewModelScope.launch(Dispatchers.IO) {
        val existingExpenseSubGroup = expenseSubGroupRepository.getExpenseSubGroupByNameAndExpenseGroupId(expenseSubGroupEntity.name, expenseSubGroupEntity.expenseGroupId)

        if (existingExpenseSubGroup == null) {
            expenseSubGroupRepository.insertExpenseSubGroup(expenseSubGroupEntity)
        } else if (existingExpenseSubGroup.archivedDate != null) {
            expenseSubGroupRepository.unarchiveExpenseSubGroup(existingExpenseSubGroup)
        }
    }

    fun updateExpenseSubGroup(expenseSubGroupEntity: ExpenseSubGroupEntity) = viewModelScope.launch(Dispatchers.IO) {
        expenseSubGroupRepository.updateExpenseSubGroup(expenseSubGroupEntity)
    }

    fun deleteExpenseSubGroup(expenseSubGroupEntity: ExpenseSubGroupEntity) = viewModelScope.launch(Dispatchers.IO) {
        expenseSubGroupRepository.deleteExpenseSubGroup(expenseSubGroupEntity)
    }

    fun deleteExpenseSubGroupById(id: Long?) = viewModelScope.launch(Dispatchers.IO) {
        expenseSubGroupRepository.deleteExpenseSubGroupById(id)
    }

    fun deleteAllExpenseSubGroup() = viewModelScope.launch(Dispatchers.IO) {
        expenseSubGroupRepository.deleteAllExpenseSubGroups()
    }

    val expenseSubGroupsWhereArchivedDateIsNullLiveDataEntity: LiveData<List<ExpenseSubGroupEntity>> = expenseSubGroupRepository.getAllExpenseGroupsNotArchivedLiveData()


    fun getExpenseSubGroupByNameWhereArchivedDateIsNull(name: String): LiveData<ExpenseSubGroupEntity?> {
        return expenseSubGroupRepository.getExpenseSubGroupByNameNotArchivedLiveData(name)
    }



}
