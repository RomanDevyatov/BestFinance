package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.IncomeGroup
import com.romandevyatov.bestfinance.data.repositories.IncomeGroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArchivedIncomeGroupsViewModel @Inject constructor(
    private val incomeGroupRepository: IncomeGroupRepository
) : ViewModel() {

    fun getIncomeGroupsArchivedByNameLiveData(name: String): LiveData<IncomeGroup>? {
        return incomeGroupRepository.getAllIncomeGroupArchivedByNameLiveData(name)
    }

    val allIncomeGroupsArchivedLiveData: LiveData<List<IncomeGroup>> = incomeGroupRepository.getAllIncomeGroupArchivedLiveData()

    suspend fun updateIncomeGroup(incomeGroup: IncomeGroup) = viewModelScope.launch(Dispatchers.IO) {
        incomeGroupRepository.updateIncomeGroup(incomeGroup)
    }

    fun unarchiveIncomeGroup(incomeGroup: IncomeGroup) = viewModelScope.launch(Dispatchers.IO) {
        val incomeGroupNotArchived = IncomeGroup(
            id = incomeGroup.id,
            name = incomeGroup.name,
            isPassive = incomeGroup.isPassive,
            description = incomeGroup.description,
            archivedDate = null
        )
        updateIncomeGroup(incomeGroupNotArchived)
    }
}
