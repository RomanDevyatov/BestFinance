package com.romandevyatov.bestfinance.repositories


import androidx.lifecycle.LiveData
import com.romandevyatov.bestfinance.db.dao.IncomeSubGroupDao
import com.romandevyatov.bestfinance.db.entities.IncomeSubGroup
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
}
