package com.romandevyatov.bestfinance.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Delete
import androidx.room.Update
import androidx.room.Query
import androidx.room.Transaction
import com.romandevyatov.bestfinance.data.entities.ExpenseGroupEntity
import com.romandevyatov.bestfinance.data.entities.relations.ExpenseGroupWithExpenseSubGroups
import com.romandevyatov.bestfinance.data.entities.relations.ExpenseGroupWithExpenseSubGroupsIncludingExpenseHistories
import java.time.LocalDateTime

@Dao
interface ExpenseGroupDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(expenseSubGroup: ExpenseGroupEntity)

    @Delete
    suspend fun delete(expenseSubGroup: ExpenseGroupEntity)

    @Update
    suspend fun update(expenseSubGroup: ExpenseGroupEntity)

    @Query("DELETE FROM expense_group")
    suspend fun deleteAll()

    @Query("DELETE FROM expense_group WHERE id = :id")
    suspend fun deleteById(id: Long?)

    @Query("SELECT * FROM expense_group WHERE name = :name LIMIT 1")
    fun getByNameLiveData(name: String): LiveData<ExpenseGroupEntity?>

    @Query("SELECT * FROM expense_group WHERE name = :name LIMIT 1")
    fun getExpenseGroupByName(name: String): ExpenseGroupEntity?

    @Query("SELECT * FROM expense_group ORDER BY id ASC")
    fun getAllExpenseGroupsLiveData(): LiveData<List<ExpenseGroupEntity>>

    @Transaction
    @Query("SELECT * FROM expense_group")
    fun getAllWithExpenseSubGroupsLiveData(): LiveData<List<ExpenseGroupWithExpenseSubGroups>>

    @Transaction
    @Query("SELECT * FROM expense_group WHERE name = :name LIMIT 1")
    fun getExpenseGroupWithExpenseSubGroupsByExpenseGroupName(name: String): ExpenseGroupWithExpenseSubGroups?

    @Transaction
    @Query("SELECT * FROM expense_group WHERE name = :name")
    fun getExpenseGroupWithExpenseSubGroupsByExpenseGroupNameLiveData(name: String): LiveData<ExpenseGroupWithExpenseSubGroups?>

    @Transaction
    @Query("SELECT * FROM expense_group")
    fun getAllExpenseGroupsWithExpenseSubGroupsLiveData(): LiveData<List<ExpenseGroupWithExpenseSubGroups>>

    @Transaction
    @Query("SELECT * FROM expense_group")
    fun getAllExpenseGroupWithExpenseSubGroupsWithExpenseHistoriesLiveData(): LiveData<List<ExpenseGroupWithExpenseSubGroupsIncludingExpenseHistories>>

    /*
    Not Archived
     */
    @Query("SELECT * FROM expense_group WHERE archived_date IS NULL ORDER BY id ASC")
    fun getAllNotArchivedLiveData(): LiveData<List<ExpenseGroupEntity>>

    @Query("SELECT * FROM expense_group WHERE name = :name AND archived_date IS NULL")
    fun getByNameNotArchivedLiveData(name: String): LiveData<ExpenseGroupEntity?>

    @Transaction
    @Query("SELECT * FROM expense_group WHERE name = :name AND archived_date IS NULL")
    fun getExpenseGroupWithExpenseSubGroupsByExpenseGroupNameNotArchived(name: String): ExpenseGroupWithExpenseSubGroups?

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
    fun getExpenseGroupNotArchivedWithExpenseSubGroupsNotArchivedByExpenseGroupNameLiveData(name: String): LiveData<ExpenseGroupWithExpenseSubGroups?>

    /*
    Archived
     */
    @Query("SELECT * FROM expense_group WHERE archived_date IS NOT NULL")
    fun getAllExpenseGroupsArchivedLiveData(): LiveData<List<ExpenseGroupEntity>>

    @Transaction
    @Query("SELECT * FROM expense_group WHERE archived_date = :date")
    fun getAllExpenseGroupWithExpenseSubGroupsWithExpenseHistoriesWithArchivedDate(date: LocalDateTime?): LiveData<List<ExpenseGroupWithExpenseSubGroupsIncludingExpenseHistories>>

    @Query("SELECT * FROM expense_group WHERE name = :name AND archived_date IS NOT NULL LIMIT 1")
    fun getExpenseGroupArchivedByNameLiveData(name: String): LiveData<ExpenseGroupEntity?>

    @Query("SELECT * FROM expense_group WHERE id = :id LIMIT 1")
    fun getById(id: Long): ExpenseGroupEntity?

    @Query("UPDATE expense_group SET archived_date = NULL WHERE id = :id")
    fun unarchiveExpenseGroupById(id: Long?)

    @Transaction
    @Query("SELECT * FROM expense_group WHERE id = :id")
    fun getExpenseGroupWithExpenseSubGroupsByExpenseGroupId(id: Long?): LiveData<ExpenseGroupWithExpenseSubGroups?>

    @Query("SELECT * FROM expense_group WHERE archived_date IS NULL")
    fun getAllExpenseGroupNotArchivedLiveData(): LiveData<List<ExpenseGroupEntity>>

    @Query("SELECT * FROM expense_group WHERE id = :id")
    fun getByIdLiveData(id: Long): LiveData<ExpenseGroupEntity?>

    @Transaction
    @Query("SELECT * FROM expense_group WHERE id = :id")
    fun getExpenseGroupWithExpenseSubGroupsByExpenseGroupIdNotArchived(id: Long): ExpenseGroupWithExpenseSubGroups?

    @Query("UPDATE expense_group SET archived_date = :date WHERE id = :id")
    fun updateArchivedDateById(id: Long?, date: String)
}
