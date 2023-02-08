package com.romandevyatov.bestfinance.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.romandevyatov.bestfinance.db.entities.ExpenseGroup
import com.romandevyatov.bestfinance.db.entities.ExpenseSubGroup
import com.romandevyatov.bestfinance.db.entities.relations.ExpenseGroupWithExpenseSubGroup


@Dao
interface ExpenseGroupDao {

    @Query("SELECT * FROM expense_group order by id ASC")
    fun getAll(): LiveData<List<ExpenseGroup>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expenseSubGroup: ExpenseGroup)

    @Delete
    suspend fun delete(expenseSubGroup: ExpenseGroup)

    @Update
    suspend fun update(expenseSubGroup: ExpenseGroup)

    @Query("DELETE FROM expense_group")
    suspend fun deleteAll()

    @Query("DELETE FROM expense_group WHERE id = :id")
    suspend fun deleteById(id: Int)


    @Transaction
    @Query("SELECT * FROM expense_group")
    fun getExpenseGroupWithExpenseSubGroup(): LiveData<List<ExpenseGroupWithExpenseSubGroup>>


}
