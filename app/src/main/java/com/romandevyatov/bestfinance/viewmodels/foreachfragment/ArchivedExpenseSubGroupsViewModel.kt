package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.relations.ExpenseGroupWithExpenseSubGroups
import com.romandevyatov.bestfinance.data.repositories.ExpenseGroupRepository
import com.romandevyatov.bestfinance.data.repositories.ExpenseSubGroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArchivedExpenseSubGroupsViewModel @Inject constructor(
    expenseGroupRepository: ExpenseGroupRepository,
    private val expenseSubGroupRepository: ExpenseSubGroupRepository
) : ViewModel() {

    fun unarchiveExpenseSubGroupById(id: Long?) = viewModelScope.launch(Dispatchers.IO) {
        expenseSubGroupRepository.unarchiveExpenseSubGroupById(id)
    }

    val allExpenseGroupsWithExpenseSubGroupsLiveData: LiveData<List<ExpenseGroupWithExpenseSubGroups>>? = expenseGroupRepository.getAllExpenseGroupsWithExpenseSubGroupsLiveData()

}
