package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.IncomeGroupEntity
import com.romandevyatov.bestfinance.data.repositories.IncomeGroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddIncomeGroupViewModel @Inject constructor(
    private val incomeGroupRepository: IncomeGroupRepository
) : ViewModel() {

    fun insertIncomeGroup(incomeGroupEntity: IncomeGroupEntity) = viewModelScope.launch(Dispatchers.IO) {
        incomeGroupRepository.insertIncomeGroup(incomeGroupEntity)
    }

    fun getAllIncomeGroupNotArchivedLiveData(): LiveData<List<IncomeGroupEntity>> {
        return incomeGroupRepository.getAllIncomeGroupNotArchivedLiveData()
    }

    fun getIncomeGroupNameByNameLiveData(incomeGroupName: String): LiveData<IncomeGroupEntity?> {
        return incomeGroupRepository.getIncomeGroupNameByNameLiveData(incomeGroupName)
    }

    fun unarchiveIncomeGroup(incomeGroupEntity: IncomeGroupEntity) = viewModelScope.launch(Dispatchers.IO) {
        incomeGroupRepository.unarchiveIncomeGroup(incomeGroupEntity)
    }
}
