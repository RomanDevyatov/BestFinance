package com.romandevyatov.bestfinance.db.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy

interface ExpenseSubGroupDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expenseSubGroupDao: ExpenseSubGroupDao)

}