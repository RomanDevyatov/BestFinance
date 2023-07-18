package com.romandevyatov.bestfinance.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.romandevyatov.bestfinance.db.entities.TransferHistory


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
    suspend fun deleteById(id: Int)

    @Transaction
    @Query("SELECT * FROM transfer_history")
    fun getAllTransferHistoryWithTransferGroupAndWallet(): LiveData<List<TransferHistory>>
    
}
