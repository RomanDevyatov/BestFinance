package com.romandevyatov.bestfinance.data.repositories

import androidx.lifecycle.LiveData
import com.romandevyatov.bestfinance.data.dao.TransferHistoryDao
import com.romandevyatov.bestfinance.data.entities.TransferHistoryEntity
import com.romandevyatov.bestfinance.data.entities.relations.TransferHistoryWithWallets
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransferHistoryRepository
@Inject
constructor(private val transferHistoryDao: TransferHistoryDao) {

    fun getAllTransferHistoriesLiveData(): LiveData<List<TransferHistoryEntity>> = transferHistoryDao.getAllLiveData()

    suspend fun insertTransferHistory(transferHistoryEntity: TransferHistoryEntity) {
        transferHistoryDao.insert(transferHistoryEntity)
    }

    suspend fun updateTransferHistory(transferHistoryEntity: TransferHistoryEntity) {
        transferHistoryDao.update(transferHistoryEntity)
    }

    fun getWithWalletsByIdLiveData(id: Long?): LiveData<TransferHistoryWithWallets?> {
        return transferHistoryDao.getWithWalletsByIdLiveData(id)
    }

    suspend fun deleteTransferHistoryById(id: Long) {
        transferHistoryDao.deleteById(id)
    }

    fun getTransferHistoryById(id: Long): TransferHistoryEntity? {
        return transferHistoryDao.getById(id)
    }

    fun getAllTransferHistoryWithWalletsLiveData(): LiveData<List<TransferHistoryWithWallets>> {
        return transferHistoryDao.getAllWithWallets()
    }

    fun getAllTransferHistories(): List<TransferHistoryEntity> {
        return transferHistoryDao.getAll()
    }
}
