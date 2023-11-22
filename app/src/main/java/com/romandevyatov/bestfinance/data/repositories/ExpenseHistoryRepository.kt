package com.romandevyatov.bestfinance.data.repositories

import androidx.lifecycle.LiveData
import com.romandevyatov.bestfinance.data.dao.ExpenseHistoryDao
import com.romandevyatov.bestfinance.data.entities.ExpenseHistoryEntity
import com.romandevyatov.bestfinance.data.entities.relations.ExpenseHistoryWithExpenseSubGroupAndWallet
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseHistoryRepository
@Inject
constructor(private val expenseHistoryDao: ExpenseHistoryDao) {

    fun getAllExpenseHistoryWithExpenseGroupAndWallet(): LiveData<List<ExpenseHistoryWithExpenseSubGroupAndWallet>> {
        return expenseHistoryDao.getAllWithExpenseGroupAndWalletLiveData()
    }

    fun getAllExpenseHistoryLiveData(): LiveData<List<ExpenseHistoryEntity>> {
        return expenseHistoryDao.getAllLiveData()
    }

    fun getAllExpenseHistory(): List<ExpenseHistoryEntity> {
        return expenseHistoryDao.getAll()
    }

    suspend fun insertExpenseHistory(expenseHistoryEntity: ExpenseHistoryEntity) {
        expenseHistoryDao.insert(expenseHistoryEntity)
    }

    fun getExpenseHistoryWithExpenseSubGroupAndWalletByIdLiveData(id: Long): LiveData<ExpenseHistoryWithExpenseSubGroupAndWallet?> {
        return expenseHistoryDao.getExpenseHistoryWithExpenseSubGroupAndWalletByIdLiveData(id)
    }

    suspend fun updateExpenseHistory(expenseHistoryEntity: ExpenseHistoryEntity) {
        return expenseHistoryDao.update(expenseHistoryEntity)
    }

    fun getExpenseHistoriesWhereSubGroupIsNullLiveData(): LiveData<List<ExpenseHistoryEntity>> {
        return expenseHistoryDao.getAllWhereSubGroupIsNullLiveData()
    }

    fun deleteExpenseHistory(id: Long) {
        return expenseHistoryDao.deleteById(id)
    }

    fun getExpenseHistoryById(id: Long): ExpenseHistoryEntity? {
        return expenseHistoryDao.getById(id)
    }
}
