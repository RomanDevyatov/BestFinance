package com.romandevyatov.bestfinance.data.repositories


import androidx.lifecycle.LiveData
import com.romandevyatov.bestfinance.data.dao.IncomeSubGroupDao
import com.romandevyatov.bestfinance.data.entities.IncomeSubGroup
import javax.inject.Inject


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

    suspend fun updateIncomeSubGroup(incomeGroup: IncomeSubGroup) {
        incomeSubGroupDao.update(incomeGroup)
    }

    suspend fun deleteIncomeSubGroupById(id: Int) = incomeSubGroupDao.deleteById(id)

    suspend fun deleteAllIncomeSubGroups() = incomeSubGroupDao.deleteAll()

    fun getIncomeSubGroupByNameLiveData(name: String): LiveData<IncomeSubGroup> = incomeSubGroupDao.getByNameLiveData(name)

    fun getIncomeSubGroupByNameNotArchivedLiveData(name: String): LiveData<IncomeSubGroup> = incomeSubGroupDao.getByNameNotArchivedLiveData(name)

    fun getByNameNotArchived(name: String): IncomeSubGroup = incomeSubGroupDao.getByNameNotArchived(name)

    suspend fun unarchiveIncomeSubGroup(incomeSubGroup: IncomeSubGroup) {
        val incomeSubGroupNotArchived = IncomeSubGroup(
            id = incomeSubGroup.id,
            name = incomeSubGroup.name,
            description = incomeSubGroup.description,
            incomeGroupId = incomeSubGroup.incomeGroupId,
            archivedDate = null
        )
        updateIncomeSubGroup(incomeSubGroupNotArchived)
    }

    fun getIncomeSubGroupByName(name: String): IncomeSubGroup {
        return incomeSubGroupDao.getByName(name)
    }

    fun getIncomeSubGroupByNameAndIncomeGroupId(name: String, incomeGroupId: Long): IncomeSubGroup {
        return incomeSubGroupDao.getByNameAndIncomeGroupId(name, incomeGroupId)
    }

    suspend fun unarchiveIncomeSubGroupsByIncomeGroupId(incomeGroupId: Long?) {
        return incomeSubGroupDao.unarchiveIncomeSubGroupsByIncomeGroupId(incomeGroupId)
    }

    suspend fun unarchiveIncomeSubGroupById(id: Long?) {
        return incomeSubGroupDao.unarchiveIncomeSubGroupById(id)
    }

    fun getIncomeSubGroupByNameWithIncomeGroupIdLiveData(name: String, incomeGroupId: Long?): LiveData<IncomeSubGroup>? {
        return incomeSubGroupDao.getIncomeSubGroupByNameWithIncomeGroupIdLiveData(name, incomeGroupId)
    }

}
