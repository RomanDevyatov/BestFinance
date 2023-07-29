package com.romandevyatov.bestfinance.data.repositories


import androidx.lifecycle.LiveData
import com.romandevyatov.bestfinance.data.dao.ExpenseHistoryDao
import com.romandevyatov.bestfinance.data.entities.ExpenseHistory
import com.romandevyatov.bestfinance.data.entities.relations.ExpenseHistoryWithExpenseSubGroupAndWallet
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseHistoryRepository @Inject constructor(
    private val expenseHistoryDao: ExpenseHistoryDao
) {

    fun getAllExpenseHistoryWithExpenseGroupAndWallet(): LiveData<List<ExpenseHistoryWithExpenseSubGroupAndWallet>> {
        return expenseHistoryDao.getAllExpenseHistoryWithExpenseGroupAndWalletLiveData()
    }

    fun getAllExpenseHistory(): LiveData<List<ExpenseHistory>> {
        return expenseHistoryDao.getAllLiveData()
    }

    suspend fun insertExpenseHistory(expenseHistory: ExpenseHistory) {
        expenseHistoryDao.insert(expenseHistory)
    }

}
