package com.romandevyatov.bestfinance.repositories

import androidx.lifecycle.LiveData
import com.romandevyatov.bestfinance.db.dao.TransferHistoryDao
import com.romandevyatov.bestfinance.db.entities.TransferHistory
import java.time.LocalDateTime
import javax.inject.Inject


class TransferHistoryRepository @Inject constructor(
    private val transferHistoryDao: TransferHistoryDao
) {

    fun getAllTransferHistories(): LiveData<List<TransferHistory>> = transferHistoryDao.getAll()

    fun getAllTransferHistoriesByArchivedDate(archivedDate: LocalDateTime?): LiveData<List<TransferHistory>> = transferHistoryDao.getAll()

    suspend fun insertTransferHistory(transferHistory: TransferHistory) {
        transferHistoryDao.insert(transferHistory)
    }

    suspend fun deleteTransferHistory(transferHistory: TransferHistory) {
        transferHistoryDao.delete(transferHistory)
    }

    suspend fun updateTransferHistory(transferHistory: TransferHistory) {
        transferHistoryDao.update(transferHistory)
    }

}
