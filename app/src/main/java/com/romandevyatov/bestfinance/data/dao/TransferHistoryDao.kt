package com.romandevyatov.bestfinance.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Delete
import androidx.room.Update
import androidx.room.Query
import androidx.room.Transaction
import com.romandevyatov.bestfinance.data.entities.TransferHistory
import com.romandevyatov.bestfinance.data.entities.relations.TransferHistoryWithWallets

@Dao
interface TransferHistoryDao {
    
    @Query("SELECT * FROM transfer_history ORDER BY id ASC")
    fun getAll(): LiveData<List<TransferHistory>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(transferHistory: TransferHistory)

    @Delete
    suspend fun delete(transferHistory: TransferHistory)

    @Update
    suspend fun update(transferHistory: TransferHistory)

    @Query("DELETE FROM transfer_history")
    suspend fun deleteAll()

    @Query("DELETE FROM transfer_history WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Transaction
    @Query("SELECT * FROM transfer_history")
    fun getAllWithTransferGroupAndWalletLiveData(): LiveData<List<TransferHistoryWithWallets>>

    @Transaction
    @Query("SELECT * FROM transfer_history WHERE id = :id LIMIT 1")
    fun getWithWalletsByIdLiveData(id: Long?): LiveData<TransferHistoryWithWallets>?
}
