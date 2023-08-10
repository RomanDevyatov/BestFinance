package com.romandevyatov.bestfinance.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.romandevyatov.bestfinance.data.entities.ExpenseHistory
import com.romandevyatov.bestfinance.data.entities.relations.ExpenseHistoryWithExpenseSubGroupAndWallet

@Dao
interface ExpenseHistoryDao {

    @Query("SELECT * FROM expense_history order by id ASC")
    fun getAllLiveData(): LiveData<List<ExpenseHistory>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
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
    fun getAllWithExpenseGroupAndWalletLiveData(): LiveData<List<ExpenseHistoryWithExpenseSubGroupAndWallet>>

    @Transaction
    @Query("SELECT * FROM expense_history WHERE id = :id LIMIT 1")
    fun getExpenseHistoryWithExpenseSubGroupAndWalletByIdLiveData(id: Long): LiveData<ExpenseHistoryWithExpenseSubGroupAndWallet>

}
