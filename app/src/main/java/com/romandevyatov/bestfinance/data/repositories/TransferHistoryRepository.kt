package com.romandevyatov.bestfinance.data.repositories

import androidx.lifecycle.LiveData
import com.romandevyatov.bestfinance.data.dao.TransferHistoryDao
import com.romandevyatov.bestfinance.data.entities.TransferHistory
import com.romandevyatov.bestfinance.data.entities.relations.TransferHistoryWithWallets
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransferHistoryRepository @Inject constructor(
    private val transferHistoryDao: TransferHistoryDao
) {

    fun getAllTransferHistories(): LiveData<List<TransferHistory>> = transferHistoryDao.getAll()

    suspend fun insertTransferHistory(transferHistory: TransferHistory) {
        transferHistoryDao.insert(transferHistory)
    }

    suspend fun deleteTransferHistory(transferHistory: TransferHistory) {
        transferHistoryDao.delete(transferHistory)
    }

    suspend fun updateTransferHistory(transferHistory: TransferHistory) {
        transferHistoryDao.update(transferHistory)
    }

    fun getWithWalletsByIdLiveData(id: Long?): LiveData<TransferHistoryWithWallets>? {
        return transferHistoryDao.getWithWalletsByIdLiveData(id)
    }

    suspend fun deleteTransferHistoryById(id: Long) {
        transferHistoryDao.deleteById(id)
    }
}
