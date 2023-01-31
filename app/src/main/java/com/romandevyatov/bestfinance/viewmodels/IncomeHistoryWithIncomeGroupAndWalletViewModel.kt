package com.romandevyatov.bestfinance.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.repositories.IncomeHistoryWithIncomeGroupAndWalletRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class IncomeHistoryWithIncomeGroupAndWalletViewModel @Inject constructor(
    private val incomeHistoryWithIncomeGroupAndWalletRepository: IncomeHistoryWithIncomeGroupAndWalletRepository
) : ViewModel() {

//    fun getAllIncomeHistoryWithIncomeGroupAndWallet() = viewModelScope.launch(Dispatchers.IO) {
//        incomeHistoryWithIncomeGroupAndWalletRepository.getAllIncomeHistoryWithIncomeGroupAndWallet()
//    }

}