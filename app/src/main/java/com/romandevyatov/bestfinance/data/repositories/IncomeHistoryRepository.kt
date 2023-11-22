package com.romandevyatov.bestfinance.data.repositories

import androidx.lifecycle.LiveData
import com.romandevyatov.bestfinance.data.dao.IncomeHistoryDao
import com.romandevyatov.bestfinance.data.entities.IncomeHistoryEntity
import com.romandevyatov.bestfinance.data.entities.relations.IncomeHistoryWithIncomeSubGroupAndWallet
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IncomeHistoryRepository
@Inject
constructor(private val incomeHistoryDao: IncomeHistoryDao) {

    fun getAllIncomeHistoryWithIncomeSubGroupAndWallet(): LiveData<List<IncomeHistoryWithIncomeSubGroupAndWallet>> {
        return incomeHistoryDao.getAllWithIncomeSubGroupAndWallet()
    }

    fun getAllIncomeHistoryLiveData(): LiveData<List<IncomeHistoryEntity>> {
        return incomeHistoryDao.getAllLivedata()
    }

    suspend fun getAllIncomeHistory(): List<IncomeHistoryEntity> {
        return incomeHistoryDao.getAll()
    }

    suspend fun insertIncomeHistory(incomeHistoryEntity: IncomeHistoryEntity) {
        incomeHistoryDao.insert(incomeHistoryEntity)
    }

    suspend fun updateIncomeHistory(incomeHistoryEntity: IncomeHistoryEntity) {
        incomeHistoryDao.update(incomeHistoryEntity)
    }

    suspend fun deleteIncomeHistory(incomeHistoryEntity: IncomeHistoryEntity) {
        incomeHistoryDao.delete(incomeHistoryEntity)
    }

    fun getIncomeHistoryWithIncomeSubGroupAndWalletByIdLiveData(id: Long): LiveData<IncomeHistoryWithIncomeSubGroupAndWallet?> {
        return incomeHistoryDao.getWithIncomeSubGroupAndWalletByIdLiveData(id)
    }

    fun getIncomeHistoriesWhereSubGroupIsNullLiveData(): LiveData<List<IncomeHistoryEntity>> {
        return incomeHistoryDao.getWhereSubGroupIdIsNullLiveData()
    }

    suspend fun deleteIncomeHistoryById(id: Long) {
        incomeHistoryDao.deleteById(id)
    }

    fun getIncomeHistoryById(id: Long): IncomeHistoryEntity? {
        return incomeHistoryDao.getById(id)
    }
}
