package com.romandevyatov.bestfinance.viewmodels.foreachmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.IncomeSubGroup
import com.romandevyatov.bestfinance.data.repositories.IncomeSubGroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IncomeSubGroupViewModel @Inject constructor(
    private val incomeSubGroupRepository: IncomeSubGroupRepository
) : ViewModel() {

    // GENERAL
    val incomeSubGroupsLiveData: LiveData<List<IncomeSubGroup>> = incomeSubGroupRepository.getAllIncomeSubGroups()

    fun insertIncomeSubGroup(incomeSubGroup: IncomeSubGroup) = viewModelScope.launch(Dispatchers.IO) {
        incomeSubGroupRepository.insertIncomeSubGroup(incomeSubGroup)
    }

    fun updateIncomeSubGroup(incomeSubGroup: IncomeSubGroup) = viewModelScope.launch(Dispatchers.IO) {
        incomeSubGroupRepository.updateIncomeSubGroup(incomeSubGroup)
    }

    fun deleteIncomeSubGroup(incomeSubGroup: IncomeSubGroup) = viewModelScope.launch(Dispatchers.IO) {
        incomeSubGroupRepository.deleteIncomeSubGroup(incomeSubGroup)
    }

    fun deleteIncomeSubGroupById(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        incomeSubGroupRepository.deleteIncomeSubGroupById(id)
    }

    fun deleteAllIncomeSubGroup() = viewModelScope.launch(Dispatchers.IO) {
        incomeSubGroupRepository.deleteAllIncomeSubGroups()
    }

    // WHERE ARCHIVED DATE IS NULL
    val incomeSubGroupsWhereArchivedDateIsNullLiveData: LiveData<List<IncomeSubGroup>> = incomeSubGroupRepository.getAllIncomeSubGroupsWhereArchivedDateIsNull()

    fun getIncomeSubGroupByNameNotArchivedLiveData(name: String): LiveData<IncomeSubGroup>? {
        return incomeSubGroupRepository.getIncomeSubGroupByNameNotArchivedLiveData(name)
    }

}
