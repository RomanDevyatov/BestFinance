package com.romandevyatov.bestfinance.viewmodels.foreachfragment.deprecated

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.ExpenseGroupEntity
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

    fun getExpenseGroupsArchivedByNameLiveData(name: String): LiveData<ExpenseGroupEntity?> {
        return expenseGroupRepository.getExpenseGroupArchivedByNameLiveData(name)
    }

    val allEntityExpenseGroupsArchivedLiveData: LiveData<List<ExpenseGroupEntity>> = expenseGroupRepository.getAllExpenseGroupArchivedLiveData()

    suspend fun updateExpenseGroup(expenseGroupEntity: ExpenseGroupEntity) = viewModelScope.launch(Dispatchers.IO) {
        expenseGroupRepository.updateExpenseGroup(expenseGroupEntity)
    }

    fun unarchiveExpenseGroup(expenseGroupEntity: ExpenseGroupEntity, isIncludeSubGroups: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        val expenseGroupEntityNotArchived = ExpenseGroupEntity(
            id = expenseGroupEntity.id,
            name = expenseGroupEntity.name,
            description = expenseGroupEntity.description,
            archivedDate = null
        )
        if (isIncludeSubGroups) {
            unarchiveExpenseSubGroupsByExpenseGroupId(expenseGroupEntityNotArchived.id)
        }
        updateExpenseGroup(expenseGroupEntityNotArchived)
    }

    fun unarchiveExpenseSubGroupsByExpenseGroupId(expenseGroupId: Long?) = viewModelScope.launch (Dispatchers.IO) {
        expenseSubGroupRepository.unarchiveExpenseSubGroupsByExpenseGroupId(expenseGroupId)
    }

    fun deleteExpenseGroupByName(id: Long?) = viewModelScope.launch (Dispatchers.IO) {
        expenseGroupRepository.deleteExpenseGroupById(id)
    }
}
