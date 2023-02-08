package com.romandevyatov.bestfinance.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.db.entities.ExpenseSubGroup
import com.romandevyatov.bestfinance.repositories.ExpenseSubGroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseSubGroupViewModel @Inject constructor(
    private val expenseSubGroupRepository: ExpenseSubGroupRepository
) : ViewModel() {

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

    fun deleteExpenseSubGroupById(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        expenseSubGroupRepository.deleteExpenseSubGroupById(id)
    }

    fun deleteAllExpenseSubGroup() = viewModelScope.launch(Dispatchers.IO) {
        expenseSubGroupRepository.deleteAllExpenseSubGroups()
    }

}
