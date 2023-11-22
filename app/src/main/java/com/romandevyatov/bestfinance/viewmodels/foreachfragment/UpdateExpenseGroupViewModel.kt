package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.ExpenseGroupEntity
import com.romandevyatov.bestfinance.data.repositories.ExpenseGroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateExpenseGroupViewModel @Inject constructor(
    private val expenseGroupRepository: ExpenseGroupRepository
) : ViewModel() {

    fun getExpenseGroupByNameLiveData(expenseGroupName: String): LiveData<ExpenseGroupEntity?> {
        return expenseGroupRepository.getExpenseGroupNameByNameLiveData(expenseGroupName)
    }

    fun updateExpenseGroup(updatedExpenseGroupEntity: ExpenseGroupEntity) = viewModelScope.launch(Dispatchers.IO) {
        expenseGroupRepository.updateExpenseGroup(updatedExpenseGroupEntity)
    }
}
