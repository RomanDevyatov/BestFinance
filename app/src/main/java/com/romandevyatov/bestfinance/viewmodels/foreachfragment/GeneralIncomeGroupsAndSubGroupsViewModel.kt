package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.IncomeGroup
import com.romandevyatov.bestfinance.data.entities.IncomeSubGroup
import com.romandevyatov.bestfinance.data.entities.Wallet
import com.romandevyatov.bestfinance.data.entities.relations.IncomeGroupWithIncomeSubGroups
import com.romandevyatov.bestfinance.data.repositories.IncomeGroupRepository
import com.romandevyatov.bestfinance.data.repositories.IncomeSubGroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class GeneralIncomeGroupsAndSubGroupsViewModel @Inject constructor(
    private val incomeGroupRepository: IncomeGroupRepository,
    private val incomeSubGroupRepository: IncomeSubGroupRepository
) : ViewModel() {

    fun getIncomeGroupByNameLiveData(selectedExpenseGroupName: String): LiveData<IncomeGroup>? {
        return incomeGroupRepository.getIncomeGroupNameByNameLiveData(selectedExpenseGroupName)
    }

    fun getIncomeGroupsArchivedByNameLiveData(name: String): LiveData<IncomeGroup>? {
        return incomeGroupRepository.getAllIncomeGroupArchivedByNameLiveData(name)
    }

    val allIncomeGroupsArchivedLiveData: LiveData<List<IncomeGroup>> = incomeGroupRepository.getAllIncomeGroupArchivedLiveData()

    suspend fun updateIncomeGroup(incomeGroup: IncomeGroup) = viewModelScope.launch(Dispatchers.IO) {
        incomeGroupRepository.updateIncomeGroup(incomeGroup)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun archiveIncomeSubGroup(name: String) = viewModelScope.launch(Dispatchers.IO) {
        val incomeSubGroup = incomeSubGroupRepository.getByNameNotArchived(name)

        if (incomeSubGroup != null) {
            val incomeSubGroupArchived = IncomeSubGroup(
                id = incomeSubGroup.id,
                name = incomeSubGroup.name,
                description = incomeSubGroup.description,
                incomeGroupId = incomeSubGroup.incomeGroupId,
                archivedDate = LocalDateTime.now()
            )

            incomeSubGroupRepository.updateIncomeSubGroup(incomeSubGroupArchived)
        }
    }

    fun unarchiveIncomeGroup(incomeGroup: IncomeGroup, isIncludedSubGroups: Boolean = false) = viewModelScope.launch(Dispatchers.IO) {
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

    fun updateIncomeSubGroup(incomeSubGroup: IncomeSubGroup) = viewModelScope.launch(Dispatchers.IO) {
        incomeSubGroupRepository.updateIncomeSubGroup(incomeSubGroup)
    }

    fun unarchiveIncomeSubGroupById(id: Long?) = viewModelScope.launch(Dispatchers.IO) {
        incomeSubGroupRepository.unarchiveIncomeSubGroupById(id)
    }

    fun deleteIncomeSubGroupById(id: Long?) = viewModelScope.launch(Dispatchers.IO) {
        incomeSubGroupRepository.deleteIncomeSubGroupById(id)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun archiveIncomeGroupById(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        val selectIncomeGroup = incomeGroupRepository.getIncomeGroupById(id)

        val selectedIncomeGroupArchived = IncomeGroup(
            id = selectIncomeGroup.id,
            name = selectIncomeGroup.name,
            isPassive = selectIncomeGroup.isPassive,
            description = selectIncomeGroup.description,
            archivedDate = LocalDateTime.now()
        )

        incomeGroupRepository.updateIncomeGroup(selectedIncomeGroupArchived)
    }

    fun getIncomeGroupByIdLiveData(id: Long?): LiveData<IncomeGroup>? {
        return incomeGroupRepository.getIncomeGroupByIdLiveData(id)
    }

    fun getIncomeSubGroupByIdLiveData(id: Long?): LiveData<IncomeSubGroup>? {
        return incomeSubGroupRepository.getIncomeSubGroupByIdLiveData(id)
    }

    fun unarchiveIncomeGroupById(id: Long?) = viewModelScope.launch(Dispatchers.IO) {
        incomeGroupRepository.unarchiveIncomeGroupById(id)
    }


    val allIncomeGroupsWithIncomeSubGroupsLiveData: LiveData<List<IncomeGroupWithIncomeSubGroups>>? = incomeGroupRepository.getAllIncomeGroupsWithIncomeSubGroupsLiveData()

}
