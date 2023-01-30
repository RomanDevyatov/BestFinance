package com.romandevyatov.bestfinance.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*

import com.romandevyatov.bestfinance.db.entities.IncomeGroup


@Dao
interface IncomeGroupDao {

    @Query("SELECT * FROM income_group order by id ASC")
    fun getAll(): LiveData<List<IncomeGroup>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(incomeGroup: IncomeGroup)

    @Delete
    suspend fun delete(incomeGroup: IncomeGroup)

    @Update
    suspend fun update(incomeGroup: IncomeGroup)

    @Query("DELETE * FROM income_group")
    suspend fun deleteAll()

    @Query("DELETE * FROM income_group WHERE id = :id")
    suspend fun deleteById(id: Int)

}