package com.romandevyatov.bestfinance.data.repositories

import androidx.lifecycle.LiveData
import com.romandevyatov.bestfinance.data.dao.IncomeSubGroupDao
import com.romandevyatov.bestfinance.data.entities.IncomeSubGroup
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IncomeSubGroupRepository @Inject constructor(
    private val incomeSubGroupDao: IncomeSubGroupDao
) {

    fun getAllIncomeSubGroups(): LiveData<List<IncomeSubGroup>> = incomeSubGroupDao.getAll()

    fun getAllIncomeSubGroupsWhereArchivedDateIsNull(): LiveData<List<IncomeSubGroup>> = incomeSubGroupDao.getAllNotArchivedLiveData()

    suspend fun insertIncomeSubGroup(incomeGroup: IncomeSubGroup) {
        incomeSubGroupDao.insert(incomeGroup)
    }

    suspend fun deleteIncomeSubGroup(incomeGroup: IncomeSubGroup) {
        incomeSubGroupDao.delete(incomeGroup)
    }

    fun updateIncomeSubGroup(incomeGroup: IncomeSubGroup) {
        incomeSubGroupDao.update(incomeGroup)
    }

    suspend fun deleteIncomeSubGroupById(id: Long?) = incomeSubGroupDao.deleteById(id)

    suspend fun deleteAllIncomeSubGroups() = incomeSubGroupDao.deleteAll()

    fun getIncomeSubGroupByNameNotArchivedLiveData(name: String): LiveData<IncomeSubGroup>? = incomeSubGroupDao.getByNameNotArchivedLiveData(name)

    fun getByNameNotArchived(name: String): IncomeSubGroup? = incomeSubGroupDao.getByNameNotArchived(name)

    fun unarchiveIncomeSubGroup(incomeSubGroup: IncomeSubGroup) {
        val incomeSubGroupNotArchived = IncomeSubGroup(
            id = incomeSubGroup.id,
            name = incomeSubGroup.name,
            description = incomeSubGroup.description,
            incomeGroupId = incomeSubGroup.incomeGroupId,
            archivedDate = null
        )
        updateIncomeSubGroup(incomeSubGroupNotArchived)
    }

    fun getIncomeSubGroupByNameAndIncomeGroupId(name: String, incomeGroupId: Long): IncomeSubGroup? {
        return incomeSubGroupDao.getByNameAndGroupId(name, incomeGroupId)
    }

    suspend fun unarchiveIncomeSubGroupsByIncomeGroupId(incomeGroupId: Long?) {
        return incomeSubGroupDao.unarchiveIncomeSubGroupsByIncomeGroupId(incomeGroupId)
    }

    fun unarchiveIncomeSubGroupById(id: Long?) {
        return incomeSubGroupDao.unarchiveIncomeSubGroupById(id)
    }

    fun getIncomeSubGroupByNameWithIncomeGroupIdLiveData(name: String, incomeGroupId: Long?): LiveData<IncomeSubGroup>? {
        return incomeSubGroupDao.getIncomeSubGroupByNameWithIncomeGroupIdLiveData(name, incomeGroupId)
    }

    fun getIncomeSubGroupByIdLiveData(id: Long?): LiveData<IncomeSubGroup>? {
        return incomeSubGroupDao.getByIdLiveData(id)
    }

    fun updateArchivedDateById(id: Long?, date: String?) {
        return incomeSubGroupDao.updateArchivedDateById(id, date)
    }

    fun getByIdNotArchived(id: Long?): IncomeSubGroup? {
        return incomeSubGroupDao.getByIdNotArchived(id)
    }

    fun getIncomeSubGroupById(id: Long): IncomeSubGroup? {
        return incomeSubGroupDao.getById(id)
    }
}
