package com.romandevyatov.bestfinance.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
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
    suspend fun update(incomeSubGroup: IncomeSubGroup)

    @Query("DELETE FROM income_sub_group")
    suspend fun deleteAll()

    @Query("DELETE FROM income_sub_group WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM income_sub_group WHERE name = :name")
    fun getByName(name: String): IncomeSubGroup

    @Query("SELECT * FROM income_sub_group WHERE name = :name AND archived_date IS NULL")
    fun getByNameNotArchived(name: String?): IncomeSubGroup

    @Query("SELECT * FROM income_sub_group WHERE name = :name AND archived_date IS NULL")
    fun getByNameNotArchivedLiveData(name: String): LiveData<IncomeSubGroup>

    @Query("SELECT * FROM income_sub_group WHERE name = :name")
    fun getByNameLiveData(name: String?): LiveData<IncomeSubGroup>

    @Query("SELECT * FROM income_sub_group WHERE archived_date = :archivedDate")
    fun getAllWithArchivedDateLiveData(archivedDate: LocalDateTime?): LiveData<List<IncomeSubGroup>>

    @Query("SELECT * FROM income_sub_group WHERE archived_date IS NULL")
    fun getAllNotArchivedLiveData(): LiveData<List<IncomeSubGroup>>

    @Query("SELECT * FROM income_sub_group WHERE name = :name AND income_group_id = :incomeGroupId")
    fun getByNameAndIncomeGroupId(name: String, incomeGroupId: Long): IncomeSubGroup

    @Query("UPDATE income_sub_group SET archived_date = NULL WHERE income_group_id = :incomeGroupId")
    fun unarchiveIncomeSubGroupsByIncomeGroupId(incomeGroupId: Long?)

    @Query("SELECT * FROM income_sub_group WHERE name = :name AND income_group_id = :incomeGroupId")
    fun getIncomeSubGroupByNameWithIncomeGroupIdLiveData(name: String, incomeGroupId: Long?): LiveData<IncomeSubGroup>?

}
