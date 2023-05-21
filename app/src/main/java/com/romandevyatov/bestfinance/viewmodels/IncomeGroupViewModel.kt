package com.romandevyatov.bestfinance.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.db.entities.ExpenseGroup
import com.romandevyatov.bestfinance.db.entities.IncomeGroup
import com.romandevyatov.bestfinance.db.entities.relations.IncomeGroupWithIncomeSubGroups
import com.romandevyatov.bestfinance.db.entities.relations.IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories
import com.romandevyatov.bestfinance.repositories.IncomeGroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class IncomeGroupViewModel @Inject constructor(
    private val incomeGroupRepository: IncomeGroupRepository
) : ViewModel() {

    val incomeGroupsLiveData: LiveData<List<IncomeGroup>> = incomeGroupRepository.getAllIncomeGroups()


    fun insertIncomeGroup(incomeGroup: IncomeGroup) = viewModelScope.launch(Dispatchers.IO) {
        incomeGroupRepository.insertIncomeGroup(incomeGroup)
    }

    fun updateIncomeGroup(incomeGroup: IncomeGroup) = viewModelScope.launch(Dispatchers.IO) {
            incomeGroupRepository.updateIncomeGroup(incomeGroup)
    }

    fun deleteIncomeGroup(incomeGroup: IncomeGroup) = viewModelScope.launch(Dispatchers.IO) {
            incomeGroupRepository.deleteIncomeGroup(incomeGroup)
    }


    fun deleteIncomeGroupById(id: Int) = viewModelScope.launch(Dispatchers.IO) {
            incomeGroupRepository.deleteIncomeGroupById(id)
    }

    fun deleteAllIncomeGroup() = viewModelScope.launch(Dispatchers.IO) {
            incomeGroupRepository.deleteAllIncomeGroups()
    }

    fun getIncomeGroupWithIncomeSubGroupsByIncomeGroupNameAndArchivedDateIsNull(name: String): LiveData<IncomeGroupWithIncomeSubGroups> {
        return incomeGroupRepository.getIncomeGroupWithIncomeSubGroupsByIncomeGroupNameAndArchivedDateIsNull(name)
    }

    fun getIncomeGroupNameByName(incomeGroupName: String): LiveData<IncomeGroup> {
        return incomeGroupRepository.getIncomeGroupNameByName(incomeGroupName)
    }

    fun getIncomeGroupByNameAndArchivedDateIsNull(selectedExpenseGroupName: String): LiveData<IncomeGroup> {
        return incomeGroupRepository.getIncomeGroupByNameAndArchivedDateIsNull(selectedExpenseGroupName)
    }

    val allIncomeGroupWithIncomeSubGroupsIncludingIncomeHistoryAndLiveData: LiveData<List<IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories>> = incomeGroupRepository.getAllIncomeGroupWithIncomeSubGroupsIncludingIncomeHistories()

    val allNotArchivedIncomeGroupWithIncomeSubGroupsIncludingIncomeHistoryAndLiveData: LiveData<List<IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories>> = incomeGroupRepository.getAllIncomeGroupWithIncomeSubGroupsIncludingIncomeHistoriesByArchivedDate(null)

}
