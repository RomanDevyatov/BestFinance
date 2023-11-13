package com.romandevyatov.bestfinance.viewmodels.foreachmodel

import androidx.lifecycle.LiveData
import com.romandevyatov.bestfinance.data.entities.ExpenseHistory
import com.romandevyatov.bestfinance.data.entities.relations.ExpenseHistoryWithExpenseSubGroupAndWallet
import com.romandevyatov.bestfinance.data.repositories.ExpenseHistoryRepository
import com.romandevyatov.bestfinance.utils.sharedpreferences.Storage
import com.romandevyatov.bestfinance.viewmodels.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ExpenseHistoryViewModel @Inject constructor(
    storage: Storage,
    expenseHistoryRepository: ExpenseHistoryRepository
) : BaseViewModel(storage) {

    val currentDefaultCurrencySymbol: String = getDefaultCurrencySymbol()

    val expenseHistoryListLiveData: LiveData<List<ExpenseHistory>> = expenseHistoryRepository.getAllExpenseHistoryLiveData()

    val allExpenseHistoryWithExpenseGroupAndWalletLiveData: LiveData<List<ExpenseHistoryWithExpenseSubGroupAndWallet>> = expenseHistoryRepository.getAllExpenseHistoryWithExpenseGroupAndWallet()

}
