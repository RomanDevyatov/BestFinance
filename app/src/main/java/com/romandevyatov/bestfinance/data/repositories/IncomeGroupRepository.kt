package com.romandevyatov.bestfinance.data.repositories

import androidx.lifecycle.LiveData
import com.romandevyatov.bestfinance.data.dao.IncomeGroupDao
import com.romandevyatov.bestfinance.data.entities.IncomeGroupEntity
import com.romandevyatov.bestfinance.data.entities.relations.IncomeGroupWithIncomeSubGroups
import com.romandevyatov.bestfinance.data.entities.relations.IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IncomeGroupRepository
@Inject
constructor(private val incomeGroupDao: IncomeGroupDao) {

    fun getAllIncomeGroupsLiveData(): LiveData<List<IncomeGroupEntity>> = incomeGroupDao.getAllLiveData()

    suspend fun insertIncomeGroup(incomeGroupEntity: IncomeGroupEntity) {
        incomeGroupDao.insert(incomeGroupEntity)
    }

    suspend fun deleteIncomeGroup(incomeGroupEntity: IncomeGroupEntity) {
        incomeGroupDao.delete(incomeGroupEntity)
    }

    suspend fun updateIncomeGroup(incomeGroupEntity: IncomeGroupEntity) {
        incomeGroupDao.update(incomeGroupEntity)
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

    fun getIncomeGroupNameByNameLiveData(incomeGroupName: String): LiveData<IncomeGroupEntity?> {
        return incomeGroupDao.getByNameLiveData(incomeGroupName)
    }

    fun getIncomeGroupNotArchivedByNameLiveData(selectedIncomeGroupName: String): LiveData<IncomeGroupEntity?> {
        return incomeGroupDao.getByNameNotArchivedLiveData(selectedIncomeGroupName)
    }

    fun getAllIncomeGroupNotArchivedLiveData(): LiveData<List<IncomeGroupEntity>> {
        return incomeGroupDao.getAllNotArchivedLiveData()
    }

    fun getIncomeGroupByIdNotArchived(incomeGroupId: Long): IncomeGroupEntity {
        return incomeGroupDao.getByIdNotArchived(incomeGroupId)
    }

    suspend fun unarchiveIncomeGroup(incomeGroupEntity: IncomeGroupEntity) {
        val incomeGroupEntityNotArchived = IncomeGroupEntity(
            id = incomeGroupEntity.id,
            name = incomeGroupEntity.name,
            isPassive = incomeGroupEntity.isPassive,
            description = incomeGroupEntity.description,
            archivedDate = null
        )
        updateIncomeGroup(incomeGroupEntityNotArchived)
    }

    fun getAllIncomeGroupArchivedLiveData(): LiveData<List<IncomeGroupEntity>> {
        return incomeGroupDao.getAllIncomeGroupArchivedLiveData()
    }

    fun getAllIncomeGroupArchivedByNameLiveData(name: String): LiveData<IncomeGroupEntity?> {
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

    fun getIncomeGroupByIdLiveData(id: Long?): LiveData<IncomeGroupEntity?> {
        return incomeGroupDao.getByIdLiveData(id)
    }

    fun unarchiveIncomeGroupById(id: Long?) {
        incomeGroupDao.unarchiveByIdSpecific(id)
    }

    fun updateArchivedDateById(id: Long?, date: String) {
        incomeGroupDao.updateArchivedDateById(id, date)
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

    fun getIncomeGroupById(id: Long): IncomeGroupEntity? {
        return incomeGroupDao.getById(id)
    }
}
