package com.romandevyatov.bestfinance.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.romandevyatov.bestfinance.db.entities.IncomeSubGroup
import java.time.OffsetDateTime


@Dao
interface IncomeSubGroupDao {

    @Query("SELECT * FROM income_sub_group order by id ASC")
    fun getAll(): LiveData<List<IncomeSubGroup>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
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
    fun getByName(name: String?): IncomeSubGroup

    @Query("SELECT * FROM income_sub_group WHERE name = :name")
    fun getByNameLiveData(name: String?): LiveData<IncomeSubGroup>

    @Query("SELECT * FROM income_sub_group WHERE archived_date = :archivedDate")
    fun getAllWithArchivedDateLiveData(archivedDate: OffsetDateTime?): LiveData<List<IncomeSubGroup>>

    @Query("SELECT * FROM income_sub_group WHERE archived_date IS NULL")
    fun getAllNotArchivedLiveData(): LiveData<List<IncomeSubGroup>>

    @Query("SELECT * FROM income_sub_group WHERE name = :name AND archived_date IS NULL")
    fun getByNameNotArchivedLiveData(name: String): LiveData<IncomeSubGroup>

}
