package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.ExpenseGroupEntity
import com.romandevyatov.bestfinance.data.entities.ExpenseSubGroupEntity
import com.romandevyatov.bestfinance.data.repositories.ExpenseGroupRepository
import com.romandevyatov.bestfinance.data.repositories.ExpenseSubGroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddExpenseSubGroupViewModel @Inject constructor(
    expenseGroupRepository: ExpenseGroupRepository,
    private val expenseSubGroupRepository: ExpenseSubGroupRepository
) : ViewModel() {

    val allEntityExpenseGroupsNotArchivedLiveData: LiveData<List<ExpenseGroupEntity>> = expenseGroupRepository.getAllExpenseGroupsNotArchivedLiveData()

    fun insertExpenseSubGroup(expenseSubGroupEntity: ExpenseSubGroupEntity) = viewModelScope.launch(Dispatchers.IO) {
        val existingIncomeSubGroup = expenseSubGroupRepository.getExpenseSubGroupByNameAndExpenseGroupId(expenseSubGroupEntity.name, expenseSubGroupEntity.expenseGroupId)

        if (existingIncomeSubGroup == null) {
            expenseSubGroupRepository.insertExpenseSubGroup(expenseSubGroupEntity)
        } else if (existingIncomeSubGroup.archivedDate != null) {
            expenseSubGroupRepository.unarchiveExpenseSubGroup(existingIncomeSubGroup)
        }
    }

    fun updateExpenseSubGroup(expenseSubGroupEntity: ExpenseSubGroupEntity) = viewModelScope.launch(Dispatchers.IO) {
        expenseSubGroupRepository.updateExpenseSubGroup(expenseSubGroupEntity)
    }

    fun unarchiveExpenseSubGroup(expenseSubGroupEntity: ExpenseSubGroupEntity) = viewModelScope.launch(Dispatchers.IO) {
        val expenseSubGroupUnarchived = expenseSubGroupEntity.copy(archivedDate = null)
        updateExpenseSubGroup(expenseSubGroupUnarchived)
    }

    fun getExpenseSubGroupByNameWithExpenseGroupIdLiveData(subGroupNameBinding: String, groupId: Long?): LiveData<ExpenseSubGroupEntity?> {
        return expenseSubGroupRepository.getExpenseSubGroupByNameWithExpenseGroupIdLiveData(subGroupNameBinding, groupId)
    }

}
