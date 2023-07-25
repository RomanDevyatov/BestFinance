package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.db.entities.IncomeGroup
import com.romandevyatov.bestfinance.db.entities.IncomeSubGroup
import com.romandevyatov.bestfinance.db.entities.relations.IncomeGroupWithIncomeSubGroups
import com.romandevyatov.bestfinance.db.entities.relations.IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories
import com.romandevyatov.bestfinance.repositories.IncomeGroupRepository
import com.romandevyatov.bestfinance.repositories.IncomeSubGroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddIncomeSubGroupViewModel @Inject constructor(
    private val incomeSubGroupRepository: IncomeSubGroupRepository,
    private val incomeGroupRepository: IncomeGroupRepository
) : ViewModel() {

    // GENERAL
    val incomeSubGroupsLiveData: LiveData<List<IncomeSubGroup>> = incomeSubGroupRepository.getAllIncomeSubGroups()

    fun insertIncomeSubGroup(incomeSubGroup: IncomeSubGroup) = viewModelScope.launch(Dispatchers.IO) {
        val existingIncomeSubGroup = incomeSubGroupRepository.getIncomeSubGroupByNameAndIncomeGroupId(incomeSubGroup.name, incomeSubGroup.incomeGroupId)

        if (existingIncomeSubGroup == null) {
            incomeSubGroupRepository.insertIncomeSubGroup(incomeSubGroup)
        } else if (existingIncomeSubGroup.archivedDate != null) {
            incomeSubGroupRepository.unarchiveIncomeSubGroup(existingIncomeSubGroup)
        }
    }

    fun updateIncomeSubGroup(incomeSubGroup: IncomeSubGroup) = viewModelScope.launch(Dispatchers.IO) {
        incomeSubGroupRepository.updateIncomeSubGroup(incomeSubGroup)
    }

    fun deleteIncomeSubGroup(incomeSubGroup: IncomeSubGroup) = viewModelScope.launch(Dispatchers.IO) {
        incomeSubGroupRepository.deleteIncomeSubGroup(incomeSubGroup)
    }

    fun deleteIncomeSubGroupById(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        incomeSubGroupRepository.deleteIncomeSubGroupById(id)
    }

    fun deleteAllIncomeSubGroup() = viewModelScope.launch(Dispatchers.IO) {
        incomeSubGroupRepository.deleteAllIncomeSubGroups()
    }

    // WHERE ARCHIVED DATE IS NULL
    val incomeSubGroupsWhereArchivedDateIsNullLiveData: LiveData<List<IncomeSubGroup>> = incomeSubGroupRepository.getAllIncomeSubGroupsWhereArchivedDateIsNull()

    fun getIncomeSubGroupByNameNotArchivedLiveData(name: String): LiveData<IncomeSubGroup> {
        return incomeSubGroupRepository.getIncomeSubGroupByNameNotArchivedLiveData(name)
    }

    fun getIncomeSubGroupByNameLiveData(name: String): LiveData<IncomeSubGroup> {
        return incomeSubGroupRepository.getIncomeSubGroupByNameLiveData(name)
    }







    val incomeGroupsLiveData: LiveData<List<IncomeGroup>> = incomeGroupRepository.getAllLiveData()

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

    fun getIncomeGroupNotArchivedByNameLiveData(selectedIncomeGroupName: String): LiveData<IncomeGroup> {
        return incomeGroupRepository.getIncomeGroupNotArchivedByNameLiveData(selectedIncomeGroupName)
    }

    fun unarchiveIncomeSubGroup(incomeSubGroup: IncomeSubGroup) = viewModelScope.launch(Dispatchers.IO) {
        incomeSubGroupRepository.unarchiveIncomeSubGroup(incomeSubGroup)
    }

    val allIncomeGroupWithIncomeSubGroupsIncludingIncomeHistoryAndLiveData: LiveData<List<IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories>> = incomeGroupRepository.getAllIncomeGroupWithIncomeSubGroupsIncludingIncomeHistories()

    val allIncomeGroupWithIncomeSubGroupsIncludingIncomeHistoryAndNotArchivedLiveData: LiveData<List<IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories>> = incomeGroupRepository.getAllIncomeGroupWithIncomeSubGroupsIncludingIncomeHistoriesNotArchivedLiveData()





}
