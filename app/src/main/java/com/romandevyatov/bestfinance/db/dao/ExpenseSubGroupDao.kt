package com.romandevyatov.bestfinance.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.romandevyatov.bestfinance.db.entities.ExpenseSubGroup
import com.romandevyatov.bestfinance.db.entities.relations.ExpenseGroupWithExpenseSubGroups
import com.romandevyatov.bestfinance.db.entities.relations.ExpenseSubGroupWithExpenseHistories


@Dao
interface ExpenseSubGroupDao {

    @Query("SELECT * FROM expense_sub_group order by id ASC")
    fun getAll(): LiveData<List<ExpenseSubGroup>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expenseSubGroup: ExpenseSubGroup)

    @Delete
    suspend fun delete(expenseSubGroup: ExpenseSubGroup)

    @Update
    suspend fun update(expenseSubGroup: ExpenseSubGroup)

    @Query("DELETE FROM expense_sub_group")
    suspend fun deleteAll()

    @Query("DELETE FROM expense_sub_group WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM expense_sub_group WHERE name = :name")
    fun getExpenseSubGroupByName(name: String?): LiveData<ExpenseSubGroup>

}
