package com.romandevyatov.bestfinance.viewmodels.foreachmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    val allIncomeGroupsLiveData: LiveData<List<IncomeGroup>> = incomeGroupRepository.getAllLiveData()

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

    fun getAllIncomeGroupNotArchivedLiveData(): LiveData<List<IncomeGroup>> {
        return incomeGroupRepository.getAllIncomeGroupNotArchivedLiveData()
    }

    fun getAllIncomeGroupWithIncomeSubGroupsByIncomeGroupNameAndNotArchivedLiveData(name: String): LiveData<IncomeGroupWithIncomeSubGroups> {
        return incomeGroupRepository.getIncomeGroupNotArchivedWithIncomeSubGroupsNotArchivedByIncomeGroupNameLiveData(name)
    }

    fun getIncomeGroupNameByNameLiveData(incomeGroupName: String): LiveData<IncomeGroup> {
        return incomeGroupRepository.getIncomeGroupNameByNameLiveData(incomeGroupName)
    }

    suspend fun getIncomeGroupByIdNotArchived(incomeGroupId: Long): IncomeGroup {
        return incomeGroupRepository.getIncomeGroupByIdNotArchived(incomeGroupId)
    }

    fun getIncomeGroupByNameAndNotArchivedLiveData(selectedExpenseGroupName: String): LiveData<IncomeGroup> {
        return incomeGroupRepository.getIncomeGroupNotArchivedByNameLiveData(selectedExpenseGroupName)
    }

    val allIncomeGroupWithIncomeSubGroupsIncludingIncomeHistoryAndLiveData: LiveData<List<IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories>> = incomeGroupRepository.getAllIncomeGroupWithIncomeSubGroupsIncludingIncomeHistories()

    val allIncomeGroupWithIncomeSubGroupsIncludingIncomeHistoryAndNotArchivedLiveData: LiveData<List<IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories>> = incomeGroupRepository.getAllIncomeGroupWithIncomeSubGroupsIncludingIncomeHistoriesNotArchivedLiveData()

}
