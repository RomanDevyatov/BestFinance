package com.romandevyatov.bestfinance.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Delete
import androidx.room.Update
import androidx.room.Query
import com.romandevyatov.bestfinance.data.entities.ExpenseSubGroupEntity

@Dao
interface ExpenseSubGroupDao {

    @Query("SELECT * FROM expense_sub_group order by id ASC")
    fun getAllLiveData(): LiveData<List<ExpenseSubGroupEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(expenseSubGroupEntity: ExpenseSubGroupEntity)

    @Delete
    suspend fun delete(expenseSubGroupEntity: ExpenseSubGroupEntity)

    @Update
    suspend fun update(expenseSubGroupEntity: ExpenseSubGroupEntity)

    @Query("DELETE FROM expense_sub_group")
    suspend fun deleteAll()

    @Query("DELETE FROM expense_sub_group WHERE id = :id")
    suspend fun deleteById(id: Long?)

    @Query("SELECT * FROM expense_sub_group WHERE name = :name")
    fun getByNameLiveData(name: String): LiveData<ExpenseSubGroupEntity?>

    @Query("SELECT * FROM expense_sub_group WHERE name = :name")
    fun getByName(name: String): ExpenseSubGroupEntity

    @Query("SELECT * FROM expense_sub_group WHERE name = :name AND expense_group_id = :groupId LIMIT 1")
    fun getByNameAndGroupId(name: String, groupId: Long): ExpenseSubGroupEntity?

    @Query("SELECT * FROM expense_sub_group WHERE name = :name AND expense_group_id = :groupId LIMIT 1")
    fun getByNameAndGroupIdLiveData(name: String, groupId: Long?): LiveData<ExpenseSubGroupEntity?>

    @Query("SELECT * FROM expense_sub_group WHERE archived_date IS NULL")
    fun getAllNotArchivedLiveData(): LiveData<List<ExpenseSubGroupEntity>>

    @Query("SELECT * FROM expense_sub_group WHERE name = :name AND archived_date IS NULL")
    fun getByNameNotArchivedLiveData(name: String): LiveData<ExpenseSubGroupEntity?>

    @Query("SELECT * FROM expense_sub_group WHERE name = :name AND archived_date IS NULL")
    fun getByNameNotArchived(name: String): ExpenseSubGroupEntity?

    @Query("UPDATE expense_sub_group SET archived_date = NULL WHERE expense_group_id = :groupId")
    fun unarchiveByGroupId(groupId: Long?)

    @Query("UPDATE expense_sub_group SET archived_date = NULL WHERE id = :id")
    fun unarchiveById(id: Long?)

    @Query("SELECT * FROM expense_sub_group WHERE id = :id LIMIT 1")
    fun getExpenseSubGroupByIdLiveData(id: Long?): LiveData<ExpenseSubGroupEntity?>

    @Query("SELECT * FROM expense_sub_group WHERE id = :id LIMIT 1")
    fun getById(id: Long): ExpenseSubGroupEntity?

    @Query("SELECT * FROM expense_sub_group WHERE id = :id AND archived_date IS NULL")
    fun getByIdNotArchived(id: Long): ExpenseSubGroupEntity?

    @Query("UPDATE expense_sub_group SET archived_date = :date WHERE id = :id")
    fun updateArchivedDateById(id: Long?, date: String)
}
