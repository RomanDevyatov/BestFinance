package com.romandevyatov.bestfinance.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.romandevyatov.bestfinance.db.entities.IncomeGroup
import com.romandevyatov.bestfinance.db.entities.relations.IncomeGroupWithIncomeSubGroups
import com.romandevyatov.bestfinance.db.entities.relations.IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories


@Dao
interface IncomeGroupDao {

    @Query("SELECT * FROM income_group ORDER BY id ASC")
    fun getAllLiveData(): LiveData<List<IncomeGroup>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(incomeGroup: IncomeGroup)

    @Delete
    suspend fun delete(incomeGroup: IncomeGroup)

    @Update
    suspend fun update(incomeGroup: IncomeGroup)

    @Query("DELETE FROM income_group")
    suspend fun deleteAll()

    @Query("DELETE FROM income_group WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Transaction
    @Query("SELECT * FROM income_group")
    fun getAllIncomeGroupWithIncomeSubGroupsIncludingIncomeHistoriesLiveData(): LiveData<List<IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories>>

    @Transaction
    @Query("SELECT * FROM income_group WHERE archived_date IS NULL LIMIT 1")
    fun getAllIncomeGroupWithIncomeSubGroupsIncludingIncomeHistoriesNotArchivedLiveData(): LiveData<List<IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories>>

    @Transaction
    @Query("SELECT * FROM income_group WHERE name = :incomeGroupName AND archived_date IS NULL LIMIT 1")
    fun getIncomeGroupWithIncomeSubGroupsByIncomeGroupNameNotArchivedLiveData(incomeGroupName: String): LiveData<IncomeGroupWithIncomeSubGroups>

    @Transaction
    @Query("SELECT * " +
            "FROM income_group " +
            "JOIN income_sub_group ON income_group.id = income_sub_group.income_group_id " +
            "WHERE income_group.name = :incomeGroupName " +
            "AND income_group.archive IS NULL " +
            "AND income_sub_group.archive IS NULL")
    fun getIncomeGroupNotArchivedWithIncomeSubGroupsNotArchivedByIncomeGroupNameLiveData(incomeGroupName: String): LiveData<IncomeGroupWithIncomeSubGroups>

    @Query("SELECT * FROM income_group WHERE name = :incomeGroupName LIMIT 1")
    fun getByNameLiveData(incomeGroupName: String): LiveData<IncomeGroup>

    @Query("SELECT * FROM income_group WHERE name = :name LIMIT 1")
    fun getByName(name: String): IncomeGroup

    @Query("SELECT * FROM income_group WHERE name = :selectedIncomeGroupName AND archived_date IS NULL")
    fun getIncomeGroupByNameAndNotArchivedLiveData(selectedIncomeGroupName: String): LiveData<IncomeGroup>

    @Query("SELECT * FROM income_group WHERE archived_date IS NULL ORDER BY id ASC")
    fun getAllNotArchivedLiveData(): LiveData<List<IncomeGroup>>

    @Transaction
    @Query("SELECT * FROM income_group WHERE name = :incomeGroupName AND archived_date IS NULL")
    fun getIncomeGroupWithIncomeSubGroupsByIncomeGroupNameNotArchived(incomeGroupName: String): IncomeGroupWithIncomeSubGroups

    @Query("SELECT * FROM income_group WHERE id = :incomeGroupId AND archived_date IS NULL")
    fun getByIdNotArchived(incomeGroupId: Long): IncomeGroup

    @Transaction
    @Query("SELECT * FROM income_group WHERE name = :incomeGroupName")
    fun getIncomeGroupWithIncomeSubGroupsByIncomeGroupName(incomeGroupName: String): IncomeGroupWithIncomeSubGroups


}
