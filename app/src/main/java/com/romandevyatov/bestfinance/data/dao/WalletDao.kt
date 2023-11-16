package com.romandevyatov.bestfinance.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import androidx.room.OnConflictStrategy
import androidx.room.Delete
import androidx.room.Query
import com.romandevyatov.bestfinance.data.entities.WalletEntity
import java.time.LocalDateTime


@Dao
interface WalletDao {

    @Query("SELECT * FROM wallet ORDER BY id ASC")
    fun getAllLiveData(): LiveData<List<WalletEntity>>

    @Query("SELECT * FROM wallet WHERE id = :id")
    suspend fun getById(id: Long?): WalletEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(walletEntity: WalletEntity)

    @Delete
    suspend fun delete(walletEntity: WalletEntity)

    @Update
    suspend fun update(walletEntity: WalletEntity)

    @Query("DELETE FROM wallet")
    suspend fun deleteAll()

    @Query("DELETE FROM wallet WHERE id = :id")
    suspend fun deleteById(id: Long?)

    @Query("SELECT * FROM wallet WHERE name = :name LIMIT 1")
    fun getByNameLiveData(name: String): LiveData<WalletEntity?>

    /*
    Not Archived
     */
    @Query("SELECT * FROM wallet WHERE archived_date IS NULL")
    fun getAllNotArchivedLiveData(): LiveData<List<WalletEntity>>

    @Query("SELECT * FROM wallet WHERE name = :name AND archived_date IS NULL LIMIT 1")
    fun getByNameNotArchivedLiveData(name: String): LiveData<WalletEntity?>

    @Query("SELECT * FROM wallet WHERE name = :name AND archived_date IS NULL LIMIT 1")
    fun getByNameNotArchived(name: String): WalletEntity?

    /*
    Archived
     */
    @Query("SELECT * FROM wallet WHERE archived_date IS NOT NULL")
    fun getAllArchivedLiveData(): LiveData<List<WalletEntity>>

    @Query("SELECT * FROM wallet WHERE name = :name AND archived_date IS NOT NULL LIMIT 1")
    fun getByNameArchivedLiveData(name: String): LiveData<WalletEntity?>

    @Query("UPDATE wallet SET archived_date = NULL WHERE id = :id")
    fun unarchiveById(id: Long?)

    @Query("UPDATE wallet SET archived_date = :date WHERE id = :id")
    fun updateArchivedDateById(id: Long?, date: LocalDateTime?)

    @Query("SELECT * FROM wallet WHERE id = :id LIMIT 1")
    fun getByIdLiveData(id: Long): LiveData<WalletEntity?>
}
