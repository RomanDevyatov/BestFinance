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
    @Query("SELECT * FROM income_group WHERE archived_date IS NULL")
    fun getAllIncomeGroupWithIncomeSubGroupsIncludingIncomeHistoriesNotArchivedLiveData(): LiveData<List<IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories>>

    @Transaction
    @Query("SELECT * FROM income_group WHERE name = :incomeGroupName AND archived_date IS NULL")
    fun getIncomeGroupWithIncomeSubGroupsByIncomeGroupNameNotArchivedLiveData(incomeGroupName: String): LiveData<IncomeGroupWithIncomeSubGroups>

    @Query("SELECT * FROM income_group WHERE name = :incomeGroupName")
    fun getIncomeGroupNameByNameLiveData(incomeGroupName: String): LiveData<IncomeGroup>

    @Query("SELECT * FROM income_group WHERE name = :selectedIncomeGroupName AND archived_date IS NULL")
    fun getIncomeGroupByNameAndNotArchivedLiveData(selectedIncomeGroupName: String): LiveData<IncomeGroup>

    @Query("SELECT * FROM income_group WHERE archived_date IS NULL ORDER BY id ASC")
    fun getAllNotArchivedLiveData(): LiveData<List<IncomeGroup>>

    @Transaction
    @Query("SELECT * FROM income_group WHERE name = :incomeGroupName AND archived_date IS NULL")
    fun getIncomeGroupWithIncomeSubGroupsByIncomeGroupNameNotArchived(incomeGroupName: String): IncomeGroupWithIncomeSubGroups

}
