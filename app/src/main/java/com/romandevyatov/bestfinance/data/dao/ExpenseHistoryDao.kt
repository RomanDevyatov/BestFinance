package com.romandevyatov.bestfinance.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Delete
import androidx.room.Update
import androidx.room.Query
import androidx.room.Transaction
import com.romandevyatov.bestfinance.data.entities.ExpenseHistory
import com.romandevyatov.bestfinance.data.entities.relations.ExpenseHistoryWithExpenseSubGroupAndWallet

@Dao
interface ExpenseHistoryDao {

    @Query("SELECT * FROM expense_history order by id ASC")
    fun getAllLiveData(): LiveData<List<ExpenseHistory>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(expenseHistory: ExpenseHistory)

    @Query("SELECT * FROM expense_history WHERE id = :id")
    fun getById(id: Long): ExpenseHistory?

    @Delete
    suspend fun delete(expenseHistory: ExpenseHistory)

    @Update
    suspend fun update(expenseHistory: ExpenseHistory)

    @Query("DELETE FROM expense_history")
    suspend fun deleteAll()

    @Query("DELETE FROM expense_history WHERE id = :id")
    fun deleteById(id: Long)

    @Transaction
    @Query("SELECT * FROM expense_history")
    fun getAllWithExpenseGroupAndWalletLiveData(): LiveData<List<ExpenseHistoryWithExpenseSubGroupAndWallet>>

    @Transaction
    @Query("SELECT * FROM expense_history WHERE id = :id LIMIT 1")
    fun getExpenseHistoryWithExpenseSubGroupAndWalletByIdLiveData(id: Long): LiveData<ExpenseHistoryWithExpenseSubGroupAndWallet?>

    @Query("SELECT * FROM expense_history WHERE expense_sub_group_id IS NULL")
    fun getAllWhereSubGroupIsNullLiveData(): LiveData<List<ExpenseHistory>>

    @Query("SELECT * FROM expense_history order by id ASC")
    fun getAll(): List<ExpenseHistory>
}
