package com.romandevyatov.bestfinance.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy


@Dao
interface ExpenseSubGroupDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expenseSubGroupDao: ExpenseSubGroupDao)

}