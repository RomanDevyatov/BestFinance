package com.romandevyatov.bestfinance.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Delete
import androidx.room.Update
import androidx.room.Query
import androidx.room.Transaction
import com.romandevyatov.bestfinance.data.entities.IncomeGroupEntity
import com.romandevyatov.bestfinance.data.entities.relations.IncomeGroupWithIncomeSubGroups
import com.romandevyatov.bestfinance.data.entities.relations.IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories

@Dao
interface IncomeGroupDao {

    @Query("SELECT * FROM income_group ORDER BY id ASC")
    fun getAllLiveData(): LiveData<List<IncomeGroupEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(incomeGroupEntity: IncomeGroupEntity)

    @Delete
    suspend fun delete(incomeGroupEntity: IncomeGroupEntity)

    @Update
    suspend fun update(incomeGroupEntity: IncomeGroupEntity)

    @Query("DELETE FROM income_group")
    suspend fun deleteAll()

    @Query("DELETE FROM income_group WHERE id = :id")
    suspend fun deleteById(id: Long?)

    @Transaction
    @Query("SELECT * FROM income_group")
    fun getAllIncomeGroupWithIncomeSubGroupsIncludingIncomeHistoriesLiveData(): LiveData<List<IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories>>

    /*
    Not Archived
     */
    @Query("SELECT * FROM income_group WHERE name = :selectedIncomeGroupName AND archived_date IS NULL")
    fun getByNameNotArchivedLiveData(selectedIncomeGroupName: String): LiveData<IncomeGroupEntity?>

    @Query("SELECT * FROM income_group WHERE archived_date IS NULL ORDER BY id ASC")
    fun getAllNotArchivedLiveData(): LiveData<List<IncomeGroupEntity>>

    @Query("SELECT * FROM income_group WHERE id = :groupId AND archived_date IS NULL")
    fun getByIdNotArchived(groupId: Long): IncomeGroupEntity

    @Transaction
    @Query("SELECT * FROM income_group WHERE name = :name AND archived_date IS NULL")
    fun getByNameNotArchivedWithIncomeSubGroups(name: String): IncomeGroupWithIncomeSubGroups

    @Transaction
    @Query("SELECT * FROM income_group WHERE archived_date IS NULL")
    fun getAllNotArchivedWithIncomeSubGroupsIncludingIncomeHistoriesLiveData(): LiveData<List<IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories>>

    @Transaction
    @Query("SELECT " +
            "ig.id, ig.name, ig.is_passive, ig.description, ig.archived_date, " +
            "isg.id, isg.name, isg.description, isg.income_group_id, isg.archived_date " +
            "FROM income_group ig " +
            "INNER JOIN income_sub_group isg " +
            "ON ig.id = isg.income_group_id " +
            "WHERE ig.name = :incomeGroupName " +
            "AND ig.archived_date IS NULL " +
            "AND isg.archived_date IS NULL " +
            "LIMIT 1")
    fun getIncomeGroupNotArchivedWithIncomeSubGroupsNotArchivedByIncomeGroupNameLiveData(incomeGroupName: String): LiveData<IncomeGroupWithIncomeSubGroups?>

    @Query("SELECT * FROM income_group WHERE name = :name LIMIT 1")
    fun getByNameLiveData(name: String): LiveData<IncomeGroupEntity?>

    @Query("SELECT * FROM income_group WHERE name = :name LIMIT 1")
    fun getByName(name: String): IncomeGroupEntity

    @Transaction
    @Query("SELECT * FROM income_group WHERE name = :name")
    fun getIncomeGroupWithIncomeSubGroupsByIncomeGroupName(name: String): IncomeGroupWithIncomeSubGroups

    @Transaction
    @Query("SELECT * FROM income_group WHERE id = :id")
    fun getIncomeGroupWithIncomeSubGroupsByIncomeGroupIdLiveData(id: Long?): LiveData<IncomeGroupWithIncomeSubGroups>

    @Query("SELECT * FROM income_group WHERE archived_date IS NOT NULL")
    fun getAllIncomeGroupArchivedLiveData(): LiveData<List<IncomeGroupEntity>>

    @Query("SELECT * FROM income_group WHERE name = :name AND archived_date IS NOT NULL")
    fun getIncomeGroupArchivedByNameLiveData(name: String): LiveData<IncomeGroupEntity?>

    @Transaction
    @Query("SELECT " +
            "ig.id, ig.name, ig.is_passive, ig.description, ig.archived_date, " +
            "isg.id, isg.name, isg.description, isg.income_group_id, isg.archived_date " +
            "FROM income_group ig " +
            "INNER JOIN income_sub_group isg " +
            "ON ig.id = isg.income_group_id " +
            "WHERE ig.archived_date IS NOT NULL " +
            "AND isg.archived_date IS NOT NULL")
    fun getIncomeGroupArchivedWithIncomeSubGroupsArchivedLiveData(): LiveData<List<IncomeGroupWithIncomeSubGroups>>

    @Transaction
    @Query("SELECT " +
            "ig.id, ig.name, ig.is_passive, ig.description, ig.archived_date, " +
            "isg.id, isg.name, isg.description, isg.income_group_id, isg.archived_date " +
            "FROM income_group ig " +
            "INNER JOIN income_sub_group isg " +
            "ON ig.id = isg.income_group_id " +
            "WHERE isg.archived_date IS NOT NULL")
    fun getAllNotArchivedWithIncomeSubGroupsLiveData(): LiveData<List<IncomeGroupWithIncomeSubGroups>>

    @Transaction
    @Query("SELECT " +
            "ig.id, ig.name, ig.is_passive, ig.description, ig.archived_date, " +
            "isg.id, isg.name, isg.description, isg.income_group_id, isg.archived_date " +
            "FROM income_group ig " +
            "INNER JOIN income_sub_group isg " +
            "ON ig.id = isg.income_group_id " +
            "WHERE ig.id = :incomeGroupId " +
            "AND ig.archived_date IS NULL " +
            "AND isg.archived_date IS NULL " +
            "LIMIT 1")
    fun getIncomeGroupNotArchivedWithIncomeSubGroupsNotArchivedByIncomeGroupIdLiveData(incomeGroupId: Long): LiveData<IncomeGroupWithIncomeSubGroups?>

    @Transaction
    @Query("SELECT * FROM income_group WHERE id = :id")
    fun getIncomeGroupWithIncomeSubGroupsByIncomeGroupIdNotArchived(id: Long): IncomeGroupWithIncomeSubGroups?

    @Transaction
    @Query("SELECT * FROM income_group")
    fun getAllWithIncomeSubGroupsLiveData(): LiveData<List<IncomeGroupWithIncomeSubGroups>>

    @Query("SELECT * FROM income_group WHERE id = :id LIMIT 1")
    fun getById(id: Long): IncomeGroupEntity?

    @Query("SELECT * FROM income_group WHERE id = :id")
    fun getByIdLiveData(id: Long?): LiveData<IncomeGroupEntity?>

    @Query("UPDATE income_group SET archived_date = :date WHERE id = :id")
    fun updateArchivedDateById(id: Long?, date: String)

    @Query("UPDATE income_group SET archived_date = NULL WHERE id = :id")
    fun unarchiveByIdSpecific(id: Long?)

    @Query("UPDATE income_group SET archived_date = :date WHERE id = :id")
    fun archiveById(id: Long?, date: String)

    @Transaction
    @Query("SELECT * FROM income_group WHERE id = :id")
    fun getIncomeGroupWithIncomeSubGroupsByIncomeGroupId(id: Long): IncomeGroupWithIncomeSubGroups?
}
