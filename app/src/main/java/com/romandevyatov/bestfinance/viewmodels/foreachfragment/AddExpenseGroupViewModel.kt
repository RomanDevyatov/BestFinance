package com.romandevyatov.bestfinance.viewmodels.foreachfragment

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
class AddExpenseGroupViewModel @Inject constructor(
    private val expenseGroupRepository: ExpenseGroupRepository,
    private val expenseSubGroupRepository: ExpenseSubGroupRepository
) : ViewModel() {

    fun insertExpenseGroup(expenseGroupEntity: ExpenseGroupEntity) = viewModelScope.launch(Dispatchers.IO) {
        val name = expenseGroupEntity.name
        val existingExpenseGroup = expenseGroupRepository.getExpenseGroupByName(name)
        if (existingExpenseGroup == null) {
            expenseGroupRepository.insertExpenseGroup(expenseGroupEntity)
        } else if (existingExpenseGroup.archivedDate != null) {
            val expenseGroupWithExpenseSubGroups = expenseGroupRepository.getExpenseGroupWithExpenseSubGroupsByExpenseGroupName(name)
            if (expenseGroupWithExpenseSubGroups != null) {
                expenseGroupRepository.unarchiveExpenseGroup(expenseGroupWithExpenseSubGroups.expenseGroupEntity)

                expenseGroupWithExpenseSubGroups.expenseSubGroupEntities.forEach {
                    expenseSubGroupRepository.unarchiveExpenseSubGroup(it)
                }
            }
        }
    }

    fun updateExpenseGroup(expenseGroupEntity: ExpenseGroupEntity) = viewModelScope.launch(Dispatchers.IO) {
        expenseGroupRepository.updateExpenseGroup(expenseGroupEntity)
    }

    fun getExpenseGroupByNameLiveData(groupNameBinding: String): LiveData<ExpenseGroupEntity?> {
        return expenseGroupRepository.getExpenseGroupNameByNameLiveData(groupNameBinding)
    }

    fun unarchiveExpenseGroup(expenseGroupEntity: ExpenseGroupEntity) = viewModelScope.launch(Dispatchers.IO) {
        val unarchivedExpenseGroupEntity = ExpenseGroupEntity(
            id = expenseGroupEntity.id,
            name = expenseGroupEntity.name,
            description = expenseGroupEntity.description,
            archivedDate = null
        )
        updateExpenseGroup(unarchivedExpenseGroupEntity)
    }

}
