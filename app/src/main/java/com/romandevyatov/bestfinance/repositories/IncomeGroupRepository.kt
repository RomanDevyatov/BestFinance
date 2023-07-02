package com.romandevyatov.bestfinance.repositories

import androidx.lifecycle.LiveData
import com.romandevyatov.bestfinance.db.dao.IncomeGroupDao
import com.romandevyatov.bestfinance.db.entities.IncomeGroup
import com.romandevyatov.bestfinance.db.entities.relations.IncomeGroupWithIncomeSubGroups
import com.romandevyatov.bestfinance.db.entities.relations.IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IncomeGroupRepository @Inject constructor(
    private val incomeGroupDao: IncomeGroupDao
) {

    fun getAllLiveData(): LiveData<List<IncomeGroup>> = incomeGroupDao.getAllLiveData()

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
        return incomeGroupDao.getAllIncomeGroupWithIncomeSubGroupsIncludingIncomeHistoriesLiveData()
    }

    fun getAllIncomeGroupWithIncomeSubGroupsIncludingIncomeHistoriesNotArchivedLiveData(): LiveData<List<IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories>> {
        return incomeGroupDao.getAllIncomeGroupWithIncomeSubGroupsIncludingIncomeHistoriesNotArchivedLiveData()
    }

    fun getIncomeGroupWithIncomeSubGroupsByIncomeGroupNameAndNotArchivedLiveData(incomeGroupName: String): LiveData<IncomeGroupWithIncomeSubGroups> {
        return incomeGroupDao.getIncomeGroupWithIncomeSubGroupsByIncomeGroupNameAndNotArchivedLiveData(incomeGroupName)
    }

    fun getIncomeGroupNameByNameLiveData(incomeGroupName: String): LiveData<IncomeGroup> {
        return incomeGroupDao.getIncomeGroupNameByNameLiveData(incomeGroupName)
    }

    fun getIncomeGroupByNameAndNotArchivedLiveData(selectedIncomeGroupName: String): LiveData<IncomeGroup> {
        return incomeGroupDao.getIncomeGroupByNameAndNotArchivedLiveData(selectedIncomeGroupName)
    }

    fun getAllIncomeGroupNotArchivedLiveData(): LiveData<List<IncomeGroup>> {
        return incomeGroupDao.getAllNotArchivedLiveData()
    }


}