package com.romandevyatov.bestfinance.repositories

import androidx.lifecycle.LiveData
import com.romandevyatov.bestfinance.db.dao.IncomeHistoryDao
import com.romandevyatov.bestfinance.db.entities.IncomeHistory
import com.romandevyatov.bestfinance.db.entities.relations.IncomeHistoryWithIncomeSubGroupAndWallet
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IncomeHistoryRepository @Inject constructor(
    private val incomeHistoryDao: IncomeHistoryDao
) {

    fun getAllIncomeHistoryWithIncomeGroupAndWallet(): LiveData<List<IncomeHistoryWithIncomeSubGroupAndWallet>> {
        return incomeHistoryDao.getAllIncomeHistoryWithIncomeGroupAndWallet()
    }

    fun getAllIncomeHistory(): LiveData<List<IncomeHistory>> {
        return incomeHistoryDao.getAll()
    }

    suspend fun insertIncomeHistory(incomeHistory: IncomeHistory) {
        incomeHistoryDao.insert(incomeHistory)
    }

    suspend fun updateIncomeHistory(incomeHistory: IncomeHistory) {
        incomeHistoryDao.update(incomeHistory)
    }

    suspend fun deleteIncomeHistory(incomeHistory: IncomeHistory) {
        incomeHistoryDao.delete(incomeHistory)
    }

}
