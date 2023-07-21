package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.db.entities.IncomeGroup
import com.romandevyatov.bestfinance.db.entities.relations.IncomeGroupWithIncomeSubGroups
import com.romandevyatov.bestfinance.db.entities.relations.IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories
import com.romandevyatov.bestfinance.repositories.IncomeGroupRepository
import com.romandevyatov.bestfinance.repositories.IncomeSubGroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddIncomeGroupViewModel @Inject constructor(
    private val incomeGroupRepository: IncomeGroupRepository,
    private val incomeSubGroupRepository: IncomeSubGroupRepository
) : ViewModel() {

    val incomeGroupsLiveData: LiveData<List<IncomeGroup>> = incomeGroupRepository.getAllLiveData()

    fun insertIncomeGroup(incomeGroup: IncomeGroup) = viewModelScope.launch(Dispatchers.IO) {
        val name = incomeGroup.name
        val existingIncomeGroup = incomeGroupRepository.getIncomeGroupByName(name)
        if (existingIncomeGroup == null) {
            incomeGroupRepository.insertIncomeGroup(incomeGroup)
        } else if (existingIncomeGroup.archivedDate != null) {
            val incomeGroupWithIncomeSubGroups = incomeGroupRepository.getIncomeGroupWithIncomeSubGroupsByIncomeGroupName(name)
            incomeGroupRepository.unarchiveIncomeGroup(incomeGroupWithIncomeSubGroups.incomeGroup)

            incomeGroupWithIncomeSubGroups.incomeSubGroups.forEach {
                incomeSubGroupRepository.unarchiveIncomeSubGroup(it)
            }
        }
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

    suspend fun getIncomeGroupByName(name: String): IncomeGroup {
        return incomeGroupRepository.getIncomeGroupByName(name)
    }

    suspend fun getIncomeGroupByIdNotArchived(incomeGroupId: Long): IncomeGroup {
        return incomeGroupRepository.getIncomeGroupByIdNotArchived(incomeGroupId)
    }

    fun getIncomeGroupByNameAndNotArchivedLiveData(selectedExpenseGroupName: String): LiveData<IncomeGroup> {
        return incomeGroupRepository.getIncomeGroupByNameAndNotArchivedLiveData(selectedExpenseGroupName)
    }

    val allIncomeGroupWithIncomeSubGroupsIncludingIncomeHistoryAndLiveData: LiveData<List<IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories>> = incomeGroupRepository.getAllIncomeGroupWithIncomeSubGroupsIncludingIncomeHistories()

    val allIncomeGroupWithIncomeSubGroupsIncludingIncomeHistoryAndNotArchivedLiveData: LiveData<List<IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories>> = incomeGroupRepository.getAllIncomeGroupWithIncomeSubGroupsIncludingIncomeHistoriesNotArchivedLiveData()

}