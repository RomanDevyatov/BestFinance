package com.romandevyatov.bestfinance.repositories

import androidx.lifecycle.LiveData
import com.romandevyatov.bestfinance.db.dao.ExpenseSubGroupDao
import com.romandevyatov.bestfinance.db.entities.ExpenseSubGroup
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseSubGroupRepository @Inject constructor(
    private val expenseSubGroupDao: ExpenseSubGroupDao
) {

    fun getAllExpenseSubGroups(): LiveData<List<ExpenseSubGroup>> = expenseSubGroupDao.getAllLiveData()

    fun getAllExpenseGroupsNotArchivedLiveData(): LiveData<List<ExpenseSubGroup>> = expenseSubGroupDao.getAllNotArchivedLiveData()


    suspend fun insertExpenseSubGroup(expenseGroup: ExpenseSubGroup) {
        expenseSubGroupDao.insert(expenseGroup)
    }

    suspend fun deleteExpenseSubGroup(expenseGroup: ExpenseSubGroup) {
        expenseSubGroupDao.delete(expenseGroup)
    }

    suspend fun updateExpenseSubGroup(expenseGroup: ExpenseSubGroup) {
        expenseSubGroupDao.update(expenseGroup)
    }

    suspend fun deleteExpenseSubGroupById(id: Int) = expenseSubGroupDao.deleteById(id)

    suspend fun deleteAllExpenseSubGroups() = expenseSubGroupDao.deleteAll()

    fun getExpenseSubGroupByNameLiveData(name: String): LiveData<ExpenseSubGroup> = expenseSubGroupDao.getByNameLiveData(name)

    fun getExpenseSubGroupByName(name: String): ExpenseSubGroup = expenseSubGroupDao.getByName(name)

    fun getExpenseSubGroupByNameNotArchivedLiveData(name: String): LiveData<ExpenseSubGroup> = expenseSubGroupDao.getByNameNotArchivedLiveData(name)

    fun getExpenseSubGroupByNameNotArchived(name: String): ExpenseSubGroup = expenseSubGroupDao.getExpenseSubGroupByNameNotArchived(name)

    suspend fun unarchiveExpenseSubGroup(expenseSubGroup: ExpenseSubGroup) {
        val expenseSubGroupNotArchived = ExpenseSubGroup(
            id = expenseSubGroup.id,
            name = expenseSubGroup.name,
            description = expenseSubGroup.description,
            expenseGroupId = expenseSubGroup.expenseGroupId,
            archivedDate = null
        )
        updateExpenseSubGroup(expenseSubGroupNotArchived)
    }

    fun getExpenseSubGroupByNameAndExpenseGroupId(name: String, expenseGroupId: Long): ExpenseSubGroup = expenseSubGroupDao.getByNameAndGroupId(name, expenseGroupId)

}
