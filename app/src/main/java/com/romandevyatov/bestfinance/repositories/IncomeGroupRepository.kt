package com.romandevyatov.bestfinance.repositories

import androidx.lifecycle.LiveData
import com.romandevyatov.bestfinance.db.dao.IncomeGroupDao
import com.romandevyatov.bestfinance.db.entities.IncomeGroup
import com.romandevyatov.bestfinance.db.entities.relations.IncomeGroupWithIncomeSubGroups
import com.romandevyatov.bestfinance.db.entities.relations.IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IncomeGroupRepository @Inject constructor(
    private val incomeGroupDao: IncomeGroupDao
) {

    fun getAllIncomeGroups(): LiveData<List<IncomeGroup>> = incomeGroupDao.getAll()

    suspend fun insertIncomeGroup(incomeGroup: IncomeGroup) {
        incomeGroupDao.insert(incomeGroup)
    }

    suspend fun deleteIncomeGroup(incomeGroup: IncomeGroup) {
        incomeGroupDao.delete(incomeGroup)
    }

    suspend fun updateIncomeGroup(incomeGroup: IncomeGroup) {
        incomeGroupDao.update(incomeGroup)
    }

    suspend fun deleteIncomeGroupById(id: Int) = incomeGroupDao.deleteById(id)

    suspend fun deleteAllIncomeGroups() = incomeGroupDao.deleteAll()

    fun getAllIncomeGroupWithIncomeSubGroupsIncludingIncomeHistories(): LiveData<List<IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories>> {
        return incomeGroupDao.getAllIncomeGroupWithIncomeSubGroupsIncludingIncomeHistories()
    }

    fun getAllIncomeGroupWithIncomeSubGroupsIncludingIncomeHistoriesByArchivedDate(archivedDate: OffsetDateTime?): LiveData<List<IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories>> {
        return incomeGroupDao.getAllIncomeGroupWithIncomeSubGroupsIncludingIncomeHistoriesWhereArchivedDateIsNull()
    }

    fun getIncomeGroupWithIncomeSubGroupsByIncomeGroupNameAndNotArchived(incomeGroupName: String): LiveData<IncomeGroupWithIncomeSubGroups> {
        return incomeGroupDao.getIncomeGroupWithIncomeSubGroupsByIncomeGroupNameAndNotArchived(incomeGroupName)
    }

    fun getIncomeGroupNameByName(incomeGroupName: String): LiveData<IncomeGroup> {
        return incomeGroupDao.getIncomeGroupNameByName(incomeGroupName)
    }

    fun getIncomeGroupByNameAndArchivedDateIsNull(selectedIncomeGroupName: String): LiveData<IncomeGroup> {
        return incomeGroupDao.getIncomeGroupByNameAndNotArchived(selectedIncomeGroupName)
    }

    fun getAllIncomeGroupNotArchived(): LiveData<List<IncomeGroup>> {
        return incomeGroupDao.getAllWhereArchivedDateIsNull()
    }


}