package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.ExpenseGroupEntity
import com.romandevyatov.bestfinance.data.entities.ExpenseSubGroup
import com.romandevyatov.bestfinance.data.entities.relations.ExpenseGroupWithExpenseSubGroups
import com.romandevyatov.bestfinance.data.repositories.ExpenseGroupRepository
import com.romandevyatov.bestfinance.data.repositories.ExpenseSubGroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class UpdateExpenseSubGroupViewModel @Inject constructor(
    private val expenseGroupRepository: ExpenseGroupRepository,
    private val expenseSubGroupRepository: ExpenseSubGroupRepository
) : ViewModel() {

    fun getExpenseGroupWithExpenseSubGroupsByExpenseGroupId(id: Long?): LiveData<ExpenseGroupWithExpenseSubGroups?> {
        return expenseGroupRepository.getExpenseGroupWithExpenseSubGroupsByExpenseGroupId(id)
    }

    fun getAllExpenseGroupNotArchivedLiveData(): LiveData<List<ExpenseGroupEntity>> {
        return expenseGroupRepository.getAllExpenseGroupsNotArchivedLiveData()
    }

    fun updateExpenseSubGroup(expenseSubGroup: ExpenseSubGroup) {
        viewModelScope.launch(Dispatchers.IO) {
            expenseSubGroupRepository.updateExpenseSubGroup(expenseSubGroup)
        }
    }

    fun getExpenseSubGroupByIdLiveData(id: Long?): LiveData<ExpenseSubGroup?> {
        return expenseSubGroupRepository.getExpenseSubGroupByIdLiveData(id)
    }
}
