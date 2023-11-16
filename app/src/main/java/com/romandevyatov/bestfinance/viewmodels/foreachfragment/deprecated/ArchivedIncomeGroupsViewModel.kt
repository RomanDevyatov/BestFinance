package com.romandevyatov.bestfinance.viewmodels.foreachfragment.deprecated

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.IncomeGroupEntity
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

    fun getIncomeGroupsArchivedByNameLiveData(name: String): LiveData<IncomeGroupEntity?> {
        return incomeGroupRepository.getAllIncomeGroupArchivedByNameLiveData(name)
    }

    val allEntityIncomeGroupsArchivedLiveData: LiveData<List<IncomeGroupEntity>> = incomeGroupRepository.getAllIncomeGroupArchivedLiveData()

    suspend fun updateIncomeGroup(incomeGroupEntity: IncomeGroupEntity) = viewModelScope.launch(Dispatchers.IO) {
        incomeGroupRepository.updateIncomeGroup(incomeGroupEntity)
    }

    fun unarchiveIncomeGroup(incomeGroupEntity: IncomeGroupEntity, isIncludedSubGroups: Boolean = true) = viewModelScope.launch(Dispatchers.IO) {
        val incomeGroupEntityNotArchived = IncomeGroupEntity(
            id = incomeGroupEntity.id,
            name = incomeGroupEntity.name,
            isPassive = incomeGroupEntity.isPassive,
            description = incomeGroupEntity.description,
            archivedDate = null
        )

        if (isIncludedSubGroups) {
            unarchiveIncomeSubGroupsByIncomeGroupId(incomeGroupEntity.id)
        }

        updateIncomeGroup(incomeGroupEntityNotArchived)
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
