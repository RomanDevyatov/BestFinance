package com.romandevyatov.bestfinance.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Delete
import androidx.room.Update
import androidx.room.Query
import com.romandevyatov.bestfinance.data.entities.IncomeSubGroup
import java.time.LocalDateTime

@Dao
interface IncomeSubGroupDao {

    @Query("SELECT * FROM income_sub_group order by id ASC")
    fun getAll(): LiveData<List<IncomeSubGroup>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(incomeSubGroup: IncomeSubGroup)

    @Delete
    suspend fun delete(incomeSubGroup: IncomeSubGroup)

    @Update
    fun update(incomeSubGroup: IncomeSubGroup)

    @Query("DELETE FROM income_sub_group")
    suspend fun deleteAll()

    @Query("DELETE FROM income_sub_group WHERE id = :id")
    suspend fun deleteById(id: Long?)

    @Query("SELECT * FROM income_sub_group WHERE name = :name")
    fun getByName(name: String): IncomeSubGroup

    @Query("SELECT * FROM income_sub_group WHERE name = :name")
    fun getByNameLiveData(name: String?): LiveData<IncomeSubGroup?>

    @Query("SELECT * FROM income_sub_group WHERE name = :name AND income_group_id = :groupId")
    fun getByNameAndGroupId(name: String, groupId: Long): IncomeSubGroup?

    @Query("SELECT * FROM income_sub_group WHERE name = :name AND income_group_id = :groupId")
    fun getIncomeSubGroupByNameWithIncomeGroupIdLiveData(name: String, groupId: Long?): LiveData<IncomeSubGroup?>

    /*
    Not Archived
     */
    @Query("SELECT * FROM income_sub_group WHERE archived_date IS NULL")
    fun getAllNotArchivedLiveData(): LiveData<List<IncomeSubGroup>>

    @Query("SELECT * FROM income_sub_group WHERE name = :name AND archived_date IS NULL")
    fun getByNameNotArchived(name: String?): IncomeSubGroup?

    @Query("SELECT * FROM income_sub_group WHERE name = :name AND archived_date IS NULL")
    fun getByNameNotArchivedLiveData(name: String): LiveData<IncomeSubGroup?>

    @Query("SELECT * FROM income_sub_group WHERE id = :id AND archived_date IS NULL")
    fun getByIdNotArchived(id: Long?): IncomeSubGroup?

    /*
    Archived
     */
    @Query("SELECT * FROM income_sub_group WHERE archived_date = :archivedDate")
    fun getAllByArchivedDateLiveData(archivedDate: LocalDateTime?): LiveData<List<IncomeSubGroup>>

    @Query("UPDATE income_sub_group SET archived_date = NULL WHERE income_group_id = :incomeGroupId")
    suspend fun unarchiveIncomeSubGroupsByIncomeGroupId(incomeGroupId: Long?)

    @Query("UPDATE income_sub_group SET archived_date = NULL WHERE id = :id")
    fun unarchiveIncomeSubGroupById(id: Long?)

    @Query("SELECT * FROM income_sub_group WHERE id = :id")
    fun getByIdLiveData(id: Long?): LiveData<IncomeSubGroup?>

    @Query("UPDATE income_sub_group SET archived_date = :date WHERE id = :id")
    fun archiveById(id: Long?, date: String)

    @Query("UPDATE income_sub_group SET archived_date = :date WHERE id = :id")
    fun updateArchivedDateById(id: Long?, date: String?)

    @Query("SELECT * FROM income_sub_group WHERE id = :id")
    fun getById(id: Long): IncomeSubGroup?
}
