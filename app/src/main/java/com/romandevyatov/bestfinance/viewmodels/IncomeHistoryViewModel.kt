package com.romandevyatov.bestfinance.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.db.entities.IncomeHistory
import com.romandevyatov.bestfinance.db.entities.relations.IncomeHistoryWithIncomeGroupAndWallet
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

    val getAllIncomeHistoryWithIncomeGroupAndWallet: LiveData<List<IncomeHistoryWithIncomeGroupAndWallet>> = incomeHistoryRepository.getAllIncomeHistoryWithIncomeGroupAndWallet()

}
