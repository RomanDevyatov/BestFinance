package com.romandevyatov.bestfinance.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.romandevyatov.bestfinance.db.entities.ExpenseGroup
import com.romandevyatov.bestfinance.db.entities.relations.ExpenseGroupWithExpenseSubGroups
import com.romandevyatov.bestfinance.db.entities.relations.ExpenseGroupWithExpenseSubGroupsIncludingExpenseHistories
import java.time.OffsetDateTime


@Dao
interface ExpenseGroupDao {

    @Query("SELECT * FROM expense_group order by id ASC")
    fun getAllLiveData(): LiveData<List<ExpenseGroup>>

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
    fun getAllExpenseGroupWithExpenseSubGroupsLiveData(): LiveData<List<ExpenseGroupWithExpenseSubGroups>>

    @Transaction
    @Query("SELECT * FROM expense_group WHERE name = :expenseGroupName")
    fun getExpenseGroupWithExpenseSubGroupsByExpenseGroupNameLiveData(expenseGroupName: String): LiveData<ExpenseGroupWithExpenseSubGroups>

    @Transaction
    @Query("SELECT * FROM expense_group WHERE name = :expenseGroupName AND archived_date IS NULL")
    fun getExpenseGroupWithExpenseSubGroupsByExpenseGroupNameNotArchivedLiveData(expenseGroupName: String): LiveData<ExpenseGroupWithExpenseSubGroups>

    @Transaction
    @Query("SELECT * FROM expense_group")
    fun getAllExpenseGroupWithExpenseSubGroupsWithExpenseHistoriesLiveData(): LiveData<List<ExpenseGroupWithExpenseSubGroupsIncludingExpenseHistories>>

    @Transaction
    @Query("SELECT * FROM expense_group WHERE archived_date = :date")
    fun getAllExpenseGroupWithExpenseSubGroupsWithExpenseHistoriesWithArchivedDate(date: OffsetDateTime?): LiveData<List<ExpenseGroupWithExpenseSubGroupsIncludingExpenseHistories>>

    @Query("SELECT * FROM expense_group WHERE name = :expenseGroupName AND archived_date IS NULL")
    fun getExpenseGroupByNameAndNotArchivedLiveData(expenseGroupName: String): LiveData<ExpenseGroup>

    @Transaction
    @Query("SELECT * FROM expense_group WHERE name = :expenseGroupName AND archived_date IS NULL")
    fun getExpenseGroupWithExpenseSubGroupsByExpenseGroupNameNotArchived(expenseGroupName: String): ExpenseGroupWithExpenseSubGroups

}
