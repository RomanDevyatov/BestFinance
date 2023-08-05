package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.ExpenseGroup
import com.romandevyatov.bestfinance.data.entities.IncomeGroup
import com.romandevyatov.bestfinance.data.repositories.IncomeGroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateIncomeGroupViewModel @Inject constructor(
    private val incomeGroupRepository: IncomeGroupRepository
) : ViewModel() {

    fun getIncomeGroupByNameLiveData(expenseGroupName: String): LiveData<IncomeGroup>? {
        return incomeGroupRepository.getIncomeGroupNameByNameLiveData(expenseGroupName)
    }

    fun updateIncomeGroup(updatedExpenseGroup: IncomeGroup) = viewModelScope.launch(Dispatchers.IO) {
        incomeGroupRepository.updateIncomeGroup(updatedExpenseGroup)
    }

}