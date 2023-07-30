package com.romandevyatov.bestfinance.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.romandevyatov.bestfinance.data.entities.ExpenseSubGroup


@Dao
interface ExpenseSubGroupDao {

    @Query("SELECT * FROM expense_sub_group order by id ASC")
    fun getAllLiveData(): LiveData<List<ExpenseSubGroup>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
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
    fun getByNameLiveData(name: String): LiveData<ExpenseSubGroup>

    @Query("SELECT * FROM expense_sub_group WHERE name = :name")
    fun getByName(name: String): ExpenseSubGroup

    @Query("SELECT * FROM expense_sub_group WHERE archived_date IS NULL")
    fun getAllNotArchivedLiveData(): LiveData<List<ExpenseSubGroup>>

    @Query("SELECT * FROM expense_sub_group WHERE name = :name AND archived_date IS NULL")
    fun getByNameNotArchivedLiveData(name: String): LiveData<ExpenseSubGroup>

    @Query("SELECT * FROM expense_sub_group WHERE name = :name AND archived_date IS NULL")
    fun getExpenseSubGroupByNameNotArchived(name: String): ExpenseSubGroup

    @Query("SELECT * FROM expense_sub_group WHERE name = :name AND expense_group_id = :expenseGroupId LIMIT 1")
    fun getByNameAndGroupId(name: String, expenseGroupId: Long): ExpenseSubGroup

    @Query("UPDATE expense_sub_group SET archived_date = NULL WHERE expense_group_id = :expenseGroupId")
    fun unarchiveExpenseSubGroupsByExpenseGroupId(expenseGroupId: Long?)

    @Query("SELECT * FROM expense_sub_group WHERE name = :name AND expense_group_id = :groupId LIMIT 1")
    fun getExpenseSubGroupByNameWithExpenseGroupIdLiveData(name: String, groupId: Long?): LiveData<ExpenseSubGroup>?

}