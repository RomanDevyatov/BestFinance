package com.romandevyatov.bestfinance.viewmodels.foreachmodel

import androidx.lifecycle.LiveData
import com.romandevyatov.bestfinance.data.entities.relations.IncomeHistoryWithIncomeSubGroupAndWallet
import com.romandevyatov.bestfinance.data.repositories.IncomeHistoryRepository
import com.romandevyatov.bestfinance.utils.sharedpreferences.Storage
import com.romandevyatov.bestfinance.viewmodels.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class IncomeHistoryViewModel @Inject constructor(
    storage: Storage,
    incomeHistoryRepository: IncomeHistoryRepository
) : BaseViewModel(storage) {

    val currentDefaultCurrencySymbol: String = getDefaultCurrencySymbol()

    val allIncomeHistoryWithIncomeSubGroupAndWalletLiveData: LiveData<List<IncomeHistoryWithIncomeSubGroupAndWallet>> = incomeHistoryRepository.getAllIncomeHistoryWithIncomeSubGroupAndWallet()

}
