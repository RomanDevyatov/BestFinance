package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.IncomeGroupEntity
import com.romandevyatov.bestfinance.data.entities.IncomeSubGroup
import com.romandevyatov.bestfinance.data.entities.relations.IncomeGroupWithIncomeSubGroups
import com.romandevyatov.bestfinance.data.repositories.IncomeGroupRepository
import com.romandevyatov.bestfinance.data.repositories.IncomeSubGroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateIncomeSubGroupViewModel @Inject constructor(
    private val incomeGroupRepository: IncomeGroupRepository,
    private val incomeSubGroupRepository: IncomeSubGroupRepository
) : ViewModel() {

    fun getIncomeGroupWithIncomeSubGroupsByIncomeGroupId(id: Long?): LiveData<IncomeGroupWithIncomeSubGroups> {
        return incomeGroupRepository.getIncomeGroupWithIncomeSubGroupsByIncomeGroupIdLiveData(id)
    }

    fun getAllIncomeGroupNotArchivedLiveData(): LiveData<List<IncomeGroupEntity>> {
        return incomeGroupRepository.getAllIncomeGroupNotArchivedLiveData()
    }

    fun updateIncomeSubGroup(incomeSubGroup: IncomeSubGroup) = viewModelScope.launch(Dispatchers.IO) {
        incomeSubGroupRepository.updateIncomeSubGroup(incomeSubGroup)
    }

    fun getIncomeSubGroupByIdLiveData(id: Long?): LiveData<IncomeSubGroup?> {
        return incomeSubGroupRepository.getIncomeSubGroupByIdLiveData(id)
    }
}
