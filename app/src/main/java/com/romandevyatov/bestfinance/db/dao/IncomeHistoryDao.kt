package com.romandevyatov.bestfinance.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.Update
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.romandevyatov.bestfinance.db.entities.IncomeHistory


@Dao
interface IncomeHistoryDao {

    @Query("SELECT * FROM income_history order by id ASC")
    fun getAll(): LiveData<List<IncomeHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(incomeHistory: IncomeHistory)

    @Delete
    suspend fun delete(incomeHistory: IncomeHistory)

    @Update
    suspend fun update(incomeHistory: IncomeHistory)

    @Query("DELETE * FROM income_history")
    suspend fun deleteAll()

    @Query("DELETE * FROM income_history WHERE id = :id")
    suspend fun deleteById(id: Int)

}
