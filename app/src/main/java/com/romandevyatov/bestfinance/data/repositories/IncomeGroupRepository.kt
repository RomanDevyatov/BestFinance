package com.romandevyatov.bestfinance.data.repositories

import androidx.lifecycle.LiveData
import com.romandevyatov.bestfinance.data.dao.IncomeGroupDao
import com.romandevyatov.bestfinance.data.entities.IncomeGroup
import com.romandevyatov.bestfinance.data.entities.relations.IncomeGroupWithIncomeSubGroups
import com.romandevyatov.bestfinance.data.entities.relations.IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories
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

    suspend fun deleteIncomeGroupById(id: Long?) = incomeGroupDao.deleteById(id)

    suspend fun deleteAllIncomeGroups() = incomeGroupDao.deleteAll()

    fun getAllIncomeGroupWithIncomeSubGroupsIncludingIncomeHistories(): LiveData<List<IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories>> {
        return incomeGroupDao.getAllIncomeGroupWithIncomeSubGroupsIncludingIncomeHistoriesLiveData()
    }

    fun getAllIncomeGroupWithIncomeSubGroupsIncludingIncomeHistoriesNotArchivedLiveData(): LiveData<List<IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories>> {
        return incomeGroupDao.getAllNotArchivedWithIncomeSubGroupsIncludingIncomeHistoriesLiveData()
    }

    fun getIncomeGroupNotArchivedWithIncomeSubGroupsNotArchivedByIncomeGroupNameLiveData(incomeGroupName: String): LiveData<IncomeGroupWithIncomeSubGroups?> {
        return incomeGroupDao.getIncomeGroupNotArchivedWithIncomeSubGroupsNotArchivedByIncomeGroupNameLiveData(incomeGroupName)
    }

    fun getIncomeGroupNameByNameLiveData(incomeGroupName: String): LiveData<IncomeGroup?> {
        return incomeGroupDao.getByNameLiveData(incomeGroupName)
    }

    fun getIncomeGroupNotArchivedByNameLiveData(selectedIncomeGroupName: String): LiveData<IncomeGroup?> {
        return incomeGroupDao.getByNameNotArchivedLiveData(selectedIncomeGroupName)
    }

    fun getAllIncomeGroupNotArchivedLiveData(): LiveData<List<IncomeGroup>> {
        return incomeGroupDao.getAllNotArchivedLiveData()
    }

    fun getIncomeGroupByIdNotArchived(incomeGroupId: Long): IncomeGroup {
        return incomeGroupDao.getByIdNotArchived(incomeGroupId)
    }

    suspend fun unarchiveIncomeGroup(incomeGroup: IncomeGroup) {
        val incomeGroupNotArchived = IncomeGroup(
            id = incomeGroup.id,
            name = incomeGroup.name,
            isPassive = incomeGroup.isPassive,
            description = incomeGroup.description,
            archivedDate = null
        )
        updateIncomeGroup(incomeGroupNotArchived)
    }

    fun getAllIncomeGroupArchivedLiveData(): LiveData<List<IncomeGroup>> {
        return incomeGroupDao.getAllIncomeGroupArchivedLiveData()
    }

    fun getAllIncomeGroupArchivedByNameLiveData(name: String): LiveData<IncomeGroup?> {
        return incomeGroupDao.getIncomeGroupArchivedByNameLiveData(name)
    }

    fun getIncomeGroupArchivedWithIncomeSubGroupsArchivedLiveData(): LiveData<List<IncomeGroupWithIncomeSubGroups>> {
        return incomeGroupDao.getIncomeGroupArchivedWithIncomeSubGroupsArchivedLiveData()
    }

    fun getAllIncomeGroupsWhereIncomeSubGroupsArchivedLiveData(): LiveData<List<IncomeGroupWithIncomeSubGroups>> {
        return incomeGroupDao.getAllNotArchivedWithIncomeSubGroupsLiveData()
    }

    fun getAllIncomeGroupsWithIncomeSubGroupsLiveData(): LiveData<List<IncomeGroupWithIncomeSubGroups>> {
        return incomeGroupDao.getAllWithIncomeSubGroupsLiveData()
    }

    fun getIncomeGroupByIdLiveData(id: Long?): LiveData<IncomeGroup?> {
        return incomeGroupDao.getByIdLiveData(id)
    }

    fun unarchiveIncomeGroupById(id: Long?) {
        return incomeGroupDao.unarchiveByIdSpecific(id)
    }

    fun updateArchivedDateById(id: Long?, date: String) {
        return incomeGroupDao.updateArchivedDateById(id, date)
    }

    fun getIncomeGroupWithIncomeSubGroupsByIncomeGroupIdLiveData(id: Long?): LiveData<IncomeGroupWithIncomeSubGroups> {
        return incomeGroupDao.getIncomeGroupWithIncomeSubGroupsByIncomeGroupIdLiveData(id)
    }

    fun getIncomeGroupWithIncomeSubGroupsByIncomeGroupId(id: Long): IncomeGroupWithIncomeSubGroups? {
        return incomeGroupDao.getIncomeGroupWithIncomeSubGroupsByIncomeGroupId(id)
    }

    fun getIncomeGroupWithIncomeSubGroupsByIncomeGroupIdNotArchived(id: Long): IncomeGroupWithIncomeSubGroups? {
        return incomeGroupDao.getIncomeGroupWithIncomeSubGroupsByIncomeGroupIdNotArchived(id)
    }

    fun getIncomeGroupById(id: Long): IncomeGroup? {
        return incomeGroupDao.getById(id)
    }
}
