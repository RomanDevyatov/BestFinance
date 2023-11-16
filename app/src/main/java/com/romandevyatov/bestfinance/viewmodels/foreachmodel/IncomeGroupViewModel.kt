package com.romandevyatov.bestfinance.viewmodels.foreachmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.romandevyatov.bestfinance.data.entities.IncomeGroupEntity
import com.romandevyatov.bestfinance.data.repositories.IncomeGroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class IncomeGroupViewModel @Inject constructor(
    private val incomeGroupRepository: IncomeGroupRepository
) : ViewModel() {

    val allEntityIncomeGroupsLiveData: LiveData<List<IncomeGroupEntity>> = incomeGroupRepository.getAllIncomeGroupsLiveData()

    fun getAllIncomeGroupNotArchivedLiveData(): LiveData<List<IncomeGroupEntity>> {
        return incomeGroupRepository.getAllIncomeGroupNotArchivedLiveData()
    }

}
