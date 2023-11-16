package com.romandevyatov.bestfinance.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Delete
import androidx.room.Update
import androidx.room.Query
import androidx.room.Transaction
import com.romandevyatov.bestfinance.data.entities.IncomeHistoryEntity
import com.romandevyatov.bestfinance.data.entities.relations.IncomeHistoryWithIncomeSubGroupAndWallet

@Dao
interface IncomeHistoryDao {

    @Query("SELECT * FROM income_history order by id ASC")
    fun getAllLivedata(): LiveData<List<IncomeHistoryEntity>>

    @Query("SELECT * FROM income_history order by id ASC")
    suspend fun getAll(): List<IncomeHistoryEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(incomeHistoryEntity: IncomeHistoryEntity)

    @Delete
    suspend fun delete(incomeHistoryEntity: IncomeHistoryEntity)

    @Update
    suspend fun update(incomeHistoryEntity: IncomeHistoryEntity)

    @Query("DELETE FROM income_history")
    suspend fun deleteAll()

    @Query("DELETE FROM income_history WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Transaction
    @Query("SELECT * FROM income_history")
    fun getAllWithIncomeSubGroupAndWallet(): LiveData<List<IncomeHistoryWithIncomeSubGroupAndWallet>>

    @Transaction
    @Query("SELECT * FROM income_history WHERE id = :id LIMIT 1")
    fun getWithIncomeSubGroupAndWalletByIdLiveData(id: Long): LiveData<IncomeHistoryWithIncomeSubGroupAndWallet?>

    @Query("SELECT * FROM income_history WHERE id = :id LIMIT 1")
    fun getByIdLiveData(id: Long): LiveData<IncomeHistoryEntity?>

    @Query("SELECT * FROM income_history WHERE income_sub_group_id IS NULL")
    fun getWhereSubGroupIdIsNullLiveData(): LiveData<List<IncomeHistoryEntity>>

    @Query("SELECT * FROM income_history WHERE id = :id")
    fun getById(id: Long): IncomeHistoryEntity?
}
