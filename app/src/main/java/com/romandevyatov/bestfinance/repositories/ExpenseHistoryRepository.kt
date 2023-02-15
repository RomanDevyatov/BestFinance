package com.romandevyatov.bestfinance.repositories


import androidx.lifecycle.LiveData
import com.romandevyatov.bestfinance.db.dao.ExpenseHistoryDao
import com.romandevyatov.bestfinance.db.entities.ExpenseHistory
import com.romandevyatov.bestfinance.db.entities.relations.ExpenseHistoryWithExpenseSubGroupAndWallet
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseHistoryRepository @Inject constructor(
    private val expenseHistoryDao: ExpenseHistoryDao
) {

    fun getAllExpenseHistoryWithExpenseGroupAndWallet(): LiveData<List<ExpenseHistoryWithExpenseSubGroupAndWallet>> {
        return expenseHistoryDao.getAllExpenseHistoryWithExpenseGroupAndWallet()
    }

    fun getAllExpenseHistory(): LiveData<List<ExpenseHistory>> {
        return expenseHistoryDao.getAll()
    }

    suspend fun insertExpenseHistory(expenseHistory: ExpenseHistory) {
        expenseHistoryDao.insert(expenseHistory)
    }

}