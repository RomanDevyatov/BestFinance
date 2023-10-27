package com.romandevyatov.bestfinance.viewmodels.foreachfragment.deprecated

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.IncomeGroup
import com.romandevyatov.bestfinance.data.entities.relations.IncomeGroupWithIncomeSubGroups
import com.romandevyatov.bestfinance.data.repositories.IncomeGroupRepository
import com.romandevyatov.bestfinance.data.repositories.IncomeSubGroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArchivedIncomeGroupsViewModel @Inject constructor(
    private val incomeGroupRepository: IncomeGroupRepository,
    private val incomeSubGroupRepository: IncomeSubGroupRepository
) : ViewModel() {

    fun getIncomeGroupsArchivedByNameLiveData(name: String): LiveData<IncomeGroup?> {
        return incomeGroupRepository.getAllIncomeGroupArchivedByNameLiveData(name)
    }

    val allIncomeGroupsArchivedLiveData: LiveData<List<IncomeGroup>> = incomeGroupRepository.getAllIncomeGroupArchivedLiveData()

    suspend fun updateIncomeGroup(incomeGroup: IncomeGroup) = viewModelScope.launch(Dispatchers.IO) {
        incomeGroupRepository.updateIncomeGroup(incomeGroup)
    }

    fun unarchiveIncomeGroup(incomeGroup: IncomeGroup, isIncludedSubGroups: Boolean = true) = viewModelScope.launch(Dispatchers.IO) {
        val incomeGroupNotArchived = IncomeGroup(
            id = incomeGroup.id,
            name = incomeGroup.name,
            isPassive = incomeGroup.isPassive,
            description = incomeGroup.description,
            archivedDate = null
        )

        if (isIncludedSubGroups) {
            unarchiveIncomeSubGroupsByIncomeGroupId(incomeGroup.id)
        }

        updateIncomeGroup(incomeGroupNotArchived)
    }

    suspend fun unarchiveIncomeSubGroupsByIncomeGroupId(incomeGroupId: Long?) = viewModelScope.launch (Dispatchers.IO) {
        incomeSubGroupRepository.unarchiveIncomeSubGroupsByIncomeGroupId(incomeGroupId)
    }

    fun deleteIncomeGroupByName(id: Long?) = viewModelScope.launch (Dispatchers.IO) {
        incomeGroupRepository.deleteIncomeGroupById(id)
    }

    val allIncomeGroupsArchivedWithIncomeSubGroupsArchivedLiveData: LiveData<List<IncomeGroupWithIncomeSubGroups>> = incomeGroupRepository.getIncomeGroupArchivedWithIncomeSubGroupsArchivedLiveData()

    val allIncomeGroupsWhereIncomeSubGroupsArchivedLiveData: LiveData<List<IncomeGroupWithIncomeSubGroups>> = incomeGroupRepository.getAllIncomeGroupsWhereIncomeSubGroupsArchivedLiveData()
}
