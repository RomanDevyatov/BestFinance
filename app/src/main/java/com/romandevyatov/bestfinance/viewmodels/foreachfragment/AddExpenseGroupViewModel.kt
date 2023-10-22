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
class AddExpenseGroupViewModel @Inject constructor(
    private val expenseGroupRepository: ExpenseGroupRepository,
    private val expenseSubGroupRepository: ExpenseSubGroupRepository
) : ViewModel() {

    fun insertExpenseGroup(expenseGroup: ExpenseGroup) = viewModelScope.launch(Dispatchers.IO) {
        val name = expenseGroup.name
        val existingExpenseGroup = expenseGroupRepository.getExpenseGroupByName(name)
        if (existingExpenseGroup == null) {
            expenseGroupRepository.insertExpenseGroup(expenseGroup)
        } else if (existingExpenseGroup.archivedDate != null) {
            val expenseGroupWithExpenseSubGroups = expenseGroupRepository.getExpenseGroupWithExpenseSubGroupsByExpenseGroupName(name)
            if (expenseGroupWithExpenseSubGroups != null) {
                expenseGroupRepository.unarchiveExpenseGroup(expenseGroupWithExpenseSubGroups.expenseGroup)

                expenseGroupWithExpenseSubGroups.expenseSubGroups.forEach {
                    expenseSubGroupRepository.unarchiveExpenseSubGroup(it)
                }
            }
        }
    }

    fun updateExpenseGroup(expenseGroup: ExpenseGroup) = viewModelScope.launch(Dispatchers.IO) {
        expenseGroupRepository.updateExpenseGroup(expenseGroup)
    }

    fun getExpenseGroupByNameLiveData(groupNameBinding: String): LiveData<ExpenseGroup?> {
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

}
