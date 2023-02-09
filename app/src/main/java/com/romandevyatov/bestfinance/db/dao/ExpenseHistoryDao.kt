package com.romandevyatov.bestfinance.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.romandevyatov.bestfinance.db.entities.ExpenseHistory
import com.romandevyatov.bestfinance.db.entities.relations.ExpenseHistoryWithExpenseSubGroupAndWallet

@Dao
interface ExpenseHistoryDao {

    @Query("SELECT * FROM expense_history order by id ASC")
    fun getAll(): LiveData<List<ExpenseHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expenseHistory: ExpenseHistory)

    @Delete
    suspend fun delete(expenseHistory: ExpenseHistory)

    @Update
    suspend fun update(expenseHistory: ExpenseHistory)

    @Query("DELETE FROM expense_history")
    suspend fun deleteAll()

    @Query("DELETE FROM expense_history WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Transaction
    @Query("SELECT * FROM expense_history")
    fun getAllExpenseHistoryWithExpenseGroupAndWallet(): LiveData<List<ExpenseHistoryWithExpenseSubGroupAndWallet>>

}