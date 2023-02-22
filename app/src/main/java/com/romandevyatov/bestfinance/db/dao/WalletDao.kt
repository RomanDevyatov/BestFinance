package com.romandevyatov.bestfinance.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import androidx.room.OnConflictStrategy
import androidx.room.Delete
import androidx.room.Query
import com.romandevyatov.bestfinance.db.entities.Wallet
import java.time.OffsetDateTime


@Dao
interface WalletDao {

    @Query("SELECT * FROM wallet ORDER BY id ASC")
    fun getAll(): LiveData<List<Wallet>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(wallet: Wallet)

    @Delete
    suspend fun delete(wallet: Wallet)

    @Update
    suspend fun update(wallet: Wallet)

    @Query("DELETE FROM wallet")
    suspend fun deleteAll()

    @Query("DELETE FROM wallet WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM wallet WHERE archived_date IS NULL")
    fun getAllWithArchivedDate(): LiveData<List<Wallet>>

}