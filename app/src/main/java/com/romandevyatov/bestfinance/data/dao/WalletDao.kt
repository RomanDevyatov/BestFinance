package com.romandevyatov.bestfinance.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import androidx.room.OnConflictStrategy
import androidx.room.Delete
import androidx.room.Query
import com.romandevyatov.bestfinance.data.entities.Wallet


@Dao
interface WalletDao {

    @Query("SELECT * FROM wallet ORDER BY id ASC")
    fun getAllLiveData(): LiveData<List<Wallet>>

    @Query("SELECT * FROM wallet WHERE id = :id")
    fun getById(id: Long?): Wallet

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(wallet: Wallet)

    @Delete
    suspend fun delete(wallet: Wallet)

    @Update
    suspend fun update(wallet: Wallet)

    @Query("DELETE FROM wallet")
    suspend fun deleteAll()

    @Query("DELETE FROM wallet WHERE id = :id")
    suspend fun deleteById(id: Long?)

    @Query("SELECT * FROM wallet WHERE name = :name LIMIT 1")
    fun getByNameLiveData(name: String): LiveData<Wallet>?

    /*
    Not Archived
     */
    @Query("SELECT * FROM wallet WHERE archived_date IS NULL")
    fun getAllNotArchivedLiveData(): LiveData<List<Wallet>>

    @Query("SELECT * FROM wallet WHERE name = :name AND archived_date IS NULL LIMIT 1")
    fun getByNameNotArchivedLiveData(name: String): LiveData<Wallet>

    @Query("SELECT * FROM wallet WHERE name = :name AND archived_date IS NULL LIMIT 1")
    fun getByNameNotArchived(name: String): Wallet

    /*
    Archived
     */
    @Query("SELECT * FROM wallet WHERE archived_date IS NOT NULL")
    fun getAllArchivedLiveData(): LiveData<List<Wallet>>?

    @Query("SELECT * FROM wallet WHERE name = :name AND archived_date IS NOT NULL LIMIT 1")
    fun getByNameArchivedLiveData(name: String): LiveData<Wallet>?

}
