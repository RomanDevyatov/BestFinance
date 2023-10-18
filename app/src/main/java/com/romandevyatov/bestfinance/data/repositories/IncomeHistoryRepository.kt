package com.romandevyatov.bestfinance.data.repositories

import androidx.lifecycle.LiveData
import com.romandevyatov.bestfinance.data.dao.IncomeHistoryDao
import com.romandevyatov.bestfinance.data.entities.IncomeHistory
import com.romandevyatov.bestfinance.data.entities.relations.IncomeHistoryWithIncomeSubGroupAndWallet
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IncomeHistoryRepository @Inject constructor(
    private val incomeHistoryDao: IncomeHistoryDao
) {

    fun getAllIncomeHistoryWithIncomeSubGroupAndWallet(): LiveData<List<IncomeHistoryWithIncomeSubGroupAndWallet>> {
        return incomeHistoryDao.getAllWithIncomeSubGroupAndWallet()
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

    fun getIncomeHistoryByIdLiveData(id: Long): LiveData<IncomeHistory> {
        return incomeHistoryDao.getByIdLiveData(id)
    }

    fun getIncomeHistoryWithIncomeSubGroupAndWalletByIdLiveData(id: Long): LiveData<IncomeHistoryWithIncomeSubGroupAndWallet>? {
        return incomeHistoryDao.getWithIncomeSubGroupAndWalletByIdLiveData(id)
    }
}
