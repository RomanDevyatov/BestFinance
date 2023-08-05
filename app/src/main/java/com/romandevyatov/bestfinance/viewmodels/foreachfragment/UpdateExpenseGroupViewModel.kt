package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.ExpenseGroup
import com.romandevyatov.bestfinance.data.repositories.ExpenseGroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateExpenseGroupViewModel @Inject constructor(
    private val expenseGroupRepository: ExpenseGroupRepository
) : ViewModel() {

    fun getExpenseGroupByNameLiveData(expenseGroupName: String): LiveData<ExpenseGroup>? {
        return expenseGroupRepository.getExpenseGroupNameByNameLiveData(expenseGroupName)
    }

    fun updateExpenseGroup(updatedExpenseGroup: ExpenseGroup) = viewModelScope.launch(Dispatchers.IO) {
        expenseGroupRepository.updateExpenseGroup(updatedExpenseGroup)
    }


}