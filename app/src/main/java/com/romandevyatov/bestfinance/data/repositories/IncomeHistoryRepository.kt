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

    fun getIncomeHistoryWithIncomeSubGroupAndWalletByIdLiveData(id: Long): LiveData<IncomeHistoryWithIncomeSubGroupAndWallet?> {
        return incomeHistoryDao.getWithIncomeSubGroupAndWalletByIdLiveData(id)
    }

    fun getIncomeHistoriesWhereSubGroupIsNullLiveData(): LiveData<List<IncomeHistory>> {
        return incomeHistoryDao.getWhereSubGroupIdIsNullLiveData()
    }

    suspend fun deleteIncomeHistoryById(id: Long) {
        incomeHistoryDao.deleteById(id)
    }

    fun getIncomeHistoryById(id: Long): IncomeHistory? {
        return incomeHistoryDao.getById(id)
    }
}
