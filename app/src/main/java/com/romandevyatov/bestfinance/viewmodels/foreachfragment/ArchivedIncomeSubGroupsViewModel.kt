package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.IncomeSubGroup
import com.romandevyatov.bestfinance.data.entities.relations.IncomeGroupWithIncomeSubGroups
import com.romandevyatov.bestfinance.data.repositories.IncomeGroupRepository
import com.romandevyatov.bestfinance.data.repositories.IncomeSubGroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArchivedIncomeSubGroupsViewModel @Inject constructor(
    private val incomeGroupRepository: IncomeGroupRepository,
    private val incomeSubGroupRepository: IncomeSubGroupRepository
) : ViewModel() {

    fun updateIncomeSubGroup(incomeSubGroup: IncomeSubGroup) = viewModelScope.launch(Dispatchers.IO) {
        incomeSubGroupRepository.updateIncomeSubGroup(incomeSubGroup)
    }

    fun unarchiveIncomeSubGroupById(id: Long?) = viewModelScope.launch(Dispatchers.IO) {
        incomeSubGroupRepository.unarchiveIncomeSubGroupById(id)
    }

    fun deleteIncomeSubGroupById(id: Long?) = viewModelScope.launch(Dispatchers.IO) {
        incomeSubGroupRepository.deleteIncomeSubGroupById(id)
    }

    val allIncomeGroupsWithIncomeSubGroupsLiveData: LiveData<List<IncomeGroupWithIncomeSubGroups>>? = incomeGroupRepository.getAllIncomeGroupsWithIncomeSubGroupsLiveData()

}
