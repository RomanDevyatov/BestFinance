package com.romandevyatov.bestfinance.repositories

import androidx.lifecycle.LiveData
import com.romandevyatov.bestfinance.db.dao.IncomeGroupDao
import com.romandevyatov.bestfinance.db.entities.IncomeGroup
import javax.inject.Inject

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

}