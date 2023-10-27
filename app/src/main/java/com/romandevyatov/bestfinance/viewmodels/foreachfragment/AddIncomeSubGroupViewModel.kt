package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.IncomeGroup
import com.romandevyatov.bestfinance.data.entities.IncomeSubGroup
import com.romandevyatov.bestfinance.data.repositories.IncomeGroupRepository
import com.romandevyatov.bestfinance.data.repositories.IncomeSubGroupRepository
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
    val incomeGroupsNotArchivedLiveData: LiveData<List<IncomeGroup>> = incomeGroupRepository.getAllIncomeGroupNotArchivedLiveData()

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

    fun getIncomeSubGroupByNameWithIncomeGroupIdLiveData(name: String, incomeGroupId: Long?): LiveData<IncomeSubGroup?> {
        return incomeSubGroupRepository.getIncomeSubGroupByNameWithIncomeGroupIdLiveData(name, incomeGroupId)
    }

    fun unarchiveIncomeSubGroup(incomeSubGroup: IncomeSubGroup) = viewModelScope.launch(Dispatchers.IO) {
        val incomeSubGroupUnarchived = incomeSubGroup.copy(archivedDate = null)
        updateIncomeSubGroup(incomeSubGroupUnarchived)
    }
}
