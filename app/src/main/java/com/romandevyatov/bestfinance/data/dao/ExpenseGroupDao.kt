package com.romandevyatov.bestfinance.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.romandevyatov.bestfinance.data.entities.ExpenseGroup
import com.romandevyatov.bestfinance.data.entities.relations.ExpenseGroupWithExpenseSubGroups
import com.romandevyatov.bestfinance.data.entities.relations.ExpenseGroupWithExpenseSubGroupsIncludingExpenseHistories
import java.time.LocalDateTime

@Dao
interface ExpenseGroupDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(expenseSubGroup: ExpenseGroup)

    @Delete
    suspend fun delete(expenseSubGroup: ExpenseGroup)

    @Update
    suspend fun update(expenseSubGroup: ExpenseGroup)

    @Query("DELETE FROM expense_group")
    suspend fun deleteAll()

    @Query("DELETE FROM expense_group WHERE id = :id")
    suspend fun deleteById(id: Long?)

    @Query("SELECT * FROM expense_group WHERE name = :name LIMIT 1")
    fun getByNameLiveData(name: String): LiveData<ExpenseGroup>?

    @Query("SELECT * FROM expense_group WHERE name = :name LIMIT 1")
    fun getExpenseGroupByName(name: String): ExpenseGroup

    @Query("SELECT * FROM expense_group ORDER BY id ASC")
    fun getAllExpenseGroupsLiveData(): LiveData<List<ExpenseGroup>>

    @Transaction
    @Query("SELECT * FROM expense_group")
    fun getAllWithExpenseSubGroupsLiveData(): LiveData<List<ExpenseGroupWithExpenseSubGroups>>

    @Transaction
    @Query("SELECT * FROM expense_group WHERE name = :name LIMIT 1")
    fun getExpenseGroupWithExpenseSubGroupsByExpenseGroupName(name: String): ExpenseGroupWithExpenseSubGroups

    @Transaction
    @Query("SELECT * FROM expense_group WHERE name = :name")
    fun getExpenseGroupWithExpenseSubGroupsByExpenseGroupNameLiveData(name: String): LiveData<ExpenseGroupWithExpenseSubGroups>

    @Transaction
    @Query("SELECT * FROM expense_group")
    fun getAllExpenseGroupsWithExpenseSubGroupsLiveData(): LiveData<List<ExpenseGroupWithExpenseSubGroups>>?

    @Transaction
    @Query("SELECT * FROM expense_group")
    fun getAllExpenseGroupWithExpenseSubGroupsWithExpenseHistoriesLiveData(): LiveData<List<ExpenseGroupWithExpenseSubGroupsIncludingExpenseHistories>>

    /*
    Not Archived
     */
    @Query("SELECT * FROM expense_group WHERE archived_date IS NULL ORDER BY id ASC")
    fun getAllNotArchivedLiveData(): LiveData<List<ExpenseGroup>>

    @Query("SELECT * FROM expense_group WHERE name = :name AND archived_date IS NULL")
    fun getByNameNotArchivedLiveData(name: String): LiveData<ExpenseGroup>

    @Transaction
    @Query("SELECT * FROM expense_group WHERE name = :name AND archived_date IS NULL")
    fun getExpenseGroupWithExpenseSubGroupsByExpenseGroupNameNotArchived(name: String): ExpenseGroupWithExpenseSubGroups

    @Transaction
    @Query("SELECT " +
            "eg.id, eg.name, eg.description, eg.archived_date, " +
            "esg.id, esg.name, esg.description, esg.expense_group_id, esg.archived_date " +
            "FROM expense_group eg " +
            "INNER JOIN expense_sub_group esg " +
            "ON eg.id = esg.expense_group_id " +
            "WHERE eg.name = :name " +
            "AND eg.archived_date IS NULL " +
            "AND esg.archived_date IS NULL " +
            "LIMIT 1")
    fun getExpenseGroupNotArchivedWithExpenseSubGroupsNotArchivedByExpenseGroupNameLiveData(name: String): LiveData<ExpenseGroupWithExpenseSubGroups>

    /*
    Archived
     */
    @Query("SELECT * FROM expense_group WHERE archived_date IS NOT NULL")
    fun getAllExpenseGroupsArchivedLiveData(): LiveData<List<ExpenseGroup>>?

    @Transaction
    @Query("SELECT * FROM expense_group WHERE archived_date = :date")
    fun getAllExpenseGroupWithExpenseSubGroupsWithExpenseHistoriesWithArchivedDate(date: LocalDateTime?): LiveData<List<ExpenseGroupWithExpenseSubGroupsIncludingExpenseHistories>>

    @Query("SELECT * FROM expense_group WHERE name = :name AND archived_date IS NOT NULL LIMIT 1")
    fun getExpenseGroupArchivedByNameLiveData(name: String): LiveData<ExpenseGroup>?

    @Query("SELECT * FROM expense_group WHERE id = :id")
    fun getById(id: Long): ExpenseGroup

    @Query("UPDATE expense_group SET archived_date = NULL WHERE id = :id")
    fun unarchiveExpenseGroupById(id: Long?)

    @Transaction
    @Query("SELECT * FROM expense_group WHERE id = :id")
    fun getExpenseGroupWithExpenseSubGroupsByExpenseGroupId(id: Long?): LiveData<ExpenseGroupWithExpenseSubGroups>

}
