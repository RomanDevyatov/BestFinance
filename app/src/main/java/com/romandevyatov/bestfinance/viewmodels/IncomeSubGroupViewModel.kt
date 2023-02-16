package com.romandevyatov.bestfinance.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.db.entities.IncomeSubGroup
import com.romandevyatov.bestfinance.repositories.IncomeSubGroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class IncomeSubGroupViewModel @Inject constructor(
    private val incomeSubGroupRepository: IncomeSubGroupRepository
) : ViewModel() {

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

    fun deleteIncomeSubGroupById(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        incomeSubGroupRepository.deleteIncomeSubGroupById(id)
    }

    fun deleteAllIncomeSubGroup() = viewModelScope.launch(Dispatchers.IO) {
        incomeSubGroupRepository.deleteAllIncomeSubGroups()
    }

    // val allIncomeHistoryWithIncomeGroupAndWalletLiveData: LiveData<List<IncomeHistoryWithIncomeSubGroupAndWallet>> = expenseHistoryRepository.getAllIncomeHistoryWithIncomeGroupAndWallet()

//    fun getIncomeSubGroupByName(expenseSubGroupName: String) : IncomeSubGroup? {
//        expenseSubGroupsLiveData.value?.forEach { it ->
//            if (it.name == expenseSubGroupName) {
//                return it
//            }
//        }
//
//        return null
//    }

    fun getIncomeSubGroupByName(name: String): IncomeSubGroup {
//        return expenseSubGroupRepository.getIncomeSubGroupByName(name)
        return incomeSubGroupsLiveData.value!!.single{
            it.name == name
        }
//        expenseSubGroupsLiveData.value?.forEach{ it ->
//            if (it.name == name) {
//                return it
//            }
//        }
    }



}
