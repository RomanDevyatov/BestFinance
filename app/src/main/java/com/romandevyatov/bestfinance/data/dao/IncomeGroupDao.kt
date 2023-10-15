package com.romandevyatov.bestfinance.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.romandevyatov.bestfinance.data.entities.IncomeGroup
import com.romandevyatov.bestfinance.data.entities.relations.IncomeGroupWithIncomeSubGroups
import com.romandevyatov.bestfinance.data.entities.relations.IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories

@Dao
interface IncomeGroupDao {

    @Query("SELECT * FROM income_group ORDER BY id ASC")
    fun getAllLiveData(): LiveData<List<IncomeGroup>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(incomeGroup: IncomeGroup)

    @Delete
    suspend fun delete(incomeGroup: IncomeGroup)

    @Update
    suspend fun update(incomeGroup: IncomeGroup)

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
    fun getByNameNotArchivedLiveData(selectedIncomeGroupName: String): LiveData<IncomeGroup>

    @Query("SELECT * FROM income_group WHERE archived_date IS NULL ORDER BY id ASC")
    fun getAllNotArchivedLiveData(): LiveData<List<IncomeGroup>>

    @Query("SELECT * FROM income_group WHERE id = :groupId AND archived_date IS NULL")
    fun getByIdNotArchived(groupId: Long): IncomeGroup

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
    fun getIncomeGroupNotArchivedWithIncomeSubGroupsNotArchivedByIncomeGroupNameLiveData(incomeGroupName: String): LiveData<IncomeGroupWithIncomeSubGroups>?

    @Query("SELECT * FROM income_group WHERE name = :name LIMIT 1")
    fun getByNameLiveData(name: String): LiveData<IncomeGroup>?

    @Query("SELECT * FROM income_group WHERE name = :name LIMIT 1")
    fun getByName(name: String): IncomeGroup

    @Transaction
    @Query("SELECT * FROM income_group WHERE name = :name")
    fun getIncomeGroupWithIncomeSubGroupsByIncomeGroupName(name: String): IncomeGroupWithIncomeSubGroups

    @Transaction
    @Query("SELECT * FROM income_group WHERE id = :id")
    fun getIncomeGroupWithIncomeSubGroupsByIncomeGroupIdLiveData(id: Long?): LiveData<IncomeGroupWithIncomeSubGroups>

    @Query("SELECT * FROM income_group WHERE archived_date IS NOT NULL")
    fun getAllIncomeGroupArchivedLiveData(): LiveData<List<IncomeGroup>>

    @Query("SELECT * FROM income_group WHERE name = :name AND archived_date IS NOT NULL")
    fun getIncomeGroupArchivedByNameLiveData(name: String): LiveData<IncomeGroup>?

    @Transaction
    @Query("SELECT " +
            "ig.id, ig.name, ig.is_passive, ig.description, ig.archived_date, " +
            "isg.id, isg.name, isg.description, isg.income_group_id, isg.archived_date " +
            "FROM income_group ig " +
            "INNER JOIN income_sub_group isg " +
            "ON ig.id = isg.income_group_id " +
            "WHERE ig.archived_date IS NOT NULL " +
            "AND isg.archived_date IS NOT NULL")
    fun getIncomeGroupArchivedWithIncomeSubGroupsArchivedLiveData(): LiveData<List<IncomeGroupWithIncomeSubGroups>>?

    @Transaction
    @Query("SELECT " +
            "ig.id, ig.name, ig.is_passive, ig.description, ig.archived_date, " +
            "isg.id, isg.name, isg.description, isg.income_group_id, isg.archived_date " +
            "FROM income_group ig " +
            "INNER JOIN income_sub_group isg " +
            "ON ig.id = isg.income_group_id " +
            "WHERE isg.archived_date IS NOT NULL")
    fun getAllNotArchivedWithIncomeSubGroupsLiveData(): LiveData<List<IncomeGroupWithIncomeSubGroups>>?

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
    fun getIncomeGroupNotArchivedWithIncomeSubGroupsNotArchivedByIncomeGroupIdLiveData(incomeGroupId: Long): LiveData<IncomeGroupWithIncomeSubGroups>?

    @Transaction
    @Query("SELECT * FROM income_group WHERE id = :id")
    fun getIncomeGroupWithIncomeSubGroupsByIncomeGroupIdNotArchived(id: Long): IncomeGroupWithIncomeSubGroups?

    @Transaction
    @Query("SELECT * FROM income_group")
    fun getAllWithIncomeSubGroupsLiveData(): LiveData<List<IncomeGroupWithIncomeSubGroups>>?

    @Query("SELECT * FROM income_group WHERE id = :id")
    fun getById(id: Long): IncomeGroup

    @Query("SELECT * FROM income_group WHERE id = :id")
    fun getByIdLiveData(id: Long?): LiveData<IncomeGroup>?

    @Query("UPDATE income_group SET archived_date = :date WHERE id = :id")
    fun updateArchivedDateById(id: Long?, date: String)

    @Query("UPDATE income_group SET archived_date = NULL WHERE id = :id")
    fun unarchiveByIdSpecific(id: Long?)

    @Query("UPDATE income_group SET archived_date = :date WHERE id = :id")
    fun archiveById(id: Long?, date: String)


}
