package com.romandevyatov.bestfinance.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.romandevyatov.bestfinance.data.entities.IncomeHistory
import com.romandevyatov.bestfinance.data.entities.relations.IncomeHistoryWithIncomeSubGroupAndWallet


@Dao
interface IncomeHistoryDao {

    @Query("SELECT * FROM income_history order by id ASC")
    fun getAll(): LiveData<List<IncomeHistory>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(incomeHistory: IncomeHistory)

    @Delete
    suspend fun delete(incomeHistory: IncomeHistory)

    @Update
    suspend fun update(incomeHistory: IncomeHistory)

    @Query("DELETE FROM income_history")
    suspend fun deleteAll()

    @Query("DELETE FROM income_history WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Transaction
    @Query("SELECT * FROM income_history")
    fun getAllIncomeHistoryWithIncomeGroupAndWallet(): LiveData<List<IncomeHistoryWithIncomeSubGroupAndWallet>>

}
