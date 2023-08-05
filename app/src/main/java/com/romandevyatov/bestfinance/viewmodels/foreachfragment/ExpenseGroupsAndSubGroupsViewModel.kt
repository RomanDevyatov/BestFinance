package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.ExpenseGroup
import com.romandevyatov.bestfinance.data.entities.ExpenseSubGroup
import com.romandevyatov.bestfinance.data.entities.relations.ExpenseGroupWithExpenseSubGroups
import com.romandevyatov.bestfinance.data.repositories.ExpenseGroupRepository
import com.romandevyatov.bestfinance.data.repositories.ExpenseSubGroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ExpenseGroupsAndSubGroupsViewModel @Inject constructor(
    private val expenseGroupRepository: ExpenseGroupRepository,
    private val expenseSubGroupRepository: ExpenseSubGroupRepository
) : ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    fun archiveExpenseSubGroup(name: String) = viewModelScope.launch(Dispatchers.IO) {
        val expenseSubGroup = expenseSubGroupRepository.getByNameNotArchived(name)

        if (expenseSubGroup != null) {
            val expenseSubGroupArchived = ExpenseSubGroup(
                id = expenseSubGroup.id,
                name = expenseSubGroup.name,
                description = expenseSubGroup.description,
                expenseGroupId = expenseSubGroup.expenseGroupId,
                archivedDate = LocalDateTime.now()
            )

            expenseSubGroupRepository.updateExpenseSubGroup(expenseSubGroupArchived)
        }
    }

    fun unarchiveExpenseSubGroupById(id: Long?) = viewModelScope.launch(Dispatchers.IO) {
        expenseSubGroupRepository.unarchiveExpenseSubGroupById(id)
    }

    fun deleteExpenseSubGroupById(id: Long?) = viewModelScope.launch(Dispatchers.IO) {
        expenseSubGroupRepository.deleteExpenseSubGroupById(id)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun archiveExpenseGroupById(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        val selectExpenseGroup = expenseGroupRepository.getExpenseGroupById(id)

        val selectedExpenseGroupArchived = ExpenseGroup(
            id = selectExpenseGroup.id,
            name = selectExpenseGroup.name,
            description = selectExpenseGroup.description,
            archivedDate = LocalDateTime.now()
        )

        expenseGroupRepository.updateExpenseGroup(selectedExpenseGroupArchived)
    }

    fun unarchiveExpenseGroupById(id: Long?) = viewModelScope.launch(Dispatchers.IO) {
        expenseGroupRepository.unarchiveExpenseGroupById(id)
    }

    fun deleteExpenseGroupById(id: Long?) = viewModelScope.launch(Dispatchers.IO) {
        expenseGroupRepository.deleteExpenseGroupById(id)
    }


    val allExpenseGroupsWithExpenseSubGroupsLiveData: LiveData<List<ExpenseGroupWithExpenseSubGroups>>? = expenseGroupRepository.getAllExpenseGroupsWithExpenseSubGroupsLiveData()

}
