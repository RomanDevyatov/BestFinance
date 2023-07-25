package com.romandevyatov.bestfinance.viewmodels.foreachmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.db.entities.IncomeHistory
import com.romandevyatov.bestfinance.db.entities.relations.IncomeHistoryWithIncomeSubGroupAndWallet
import com.romandevyatov.bestfinance.repositories.IncomeHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IncomeHistoryViewModel @Inject constructor(
    private val incomeHistoryRepository: IncomeHistoryRepository
) : ViewModel() {

    val incomeHistoryLiveData: LiveData<List<IncomeHistory>> = incomeHistoryRepository.getAllIncomeHistory()

    fun insertIncomeHistory(incomeHistory: IncomeHistory) = viewModelScope.launch(Dispatchers.IO) {
        incomeHistoryRepository.insertIncomeHistory(incomeHistory)
    }

    fun updateIncomeHistory(incomeHistory: IncomeHistory) = viewModelScope.launch(Dispatchers.IO) {
        incomeHistoryRepository.updateIncomeHistory(incomeHistory)
    }

    fun deleteIncomeHistory(incomeHistory: IncomeHistory) = viewModelScope.launch(Dispatchers.IO) {
        incomeHistoryRepository.deleteIncomeHistory(incomeHistory)
    }

    val allIncomeHistoryWithIncomeGroupAndWalletLiveData: LiveData<List<IncomeHistoryWithIncomeSubGroupAndWallet>> = incomeHistoryRepository.getAllIncomeHistoryWithIncomeGroupAndWallet()

}
