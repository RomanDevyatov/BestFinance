package com.romandevyatov.bestfinance.data.repositories

import androidx.lifecycle.LiveData
import com.romandevyatov.bestfinance.data.dao.ExpenseSubGroupDao
import com.romandevyatov.bestfinance.data.entities.ExpenseSubGroupEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseSubGroupRepository
@Inject
constructor(private val expenseSubGroupDao: ExpenseSubGroupDao) {

    fun getAllExpenseSubGroups(): LiveData<List<ExpenseSubGroupEntity>> = expenseSubGroupDao.getAllLiveData()

    fun getAllExpenseGroupsNotArchivedLiveData(): LiveData<List<ExpenseSubGroupEntity>> = expenseSubGroupDao.getAllNotArchivedLiveData()

    suspend fun insertExpenseSubGroup(expenseGroup: ExpenseSubGroupEntity) {
        expenseSubGroupDao.insert(expenseGroup)
    }

    suspend fun deleteExpenseSubGroup(expenseGroup: ExpenseSubGroupEntity) {
        expenseSubGroupDao.delete(expenseGroup)
    }

    suspend fun updateExpenseSubGroup(expenseGroup: ExpenseSubGroupEntity) {
        expenseSubGroupDao.update(expenseGroup)
    }

    suspend fun deleteExpenseSubGroupById(id: Long?) = expenseSubGroupDao.deleteById(id)

    suspend fun deleteAllExpenseSubGroups() = expenseSubGroupDao.deleteAll()

    fun getExpenseSubGroupByNameNotArchivedLiveData(name: String): LiveData<ExpenseSubGroupEntity?> = expenseSubGroupDao.getByNameNotArchivedLiveData(name)

    fun getExpenseSubGroupByNameNotArchived(name: String): ExpenseSubGroupEntity? = expenseSubGroupDao.getByNameNotArchived(name)

    suspend fun unarchiveExpenseSubGroup(expenseSubGroupEntity: ExpenseSubGroupEntity) {
        val expenseSubGroupEntityNotArchived = ExpenseSubGroupEntity(
            id = expenseSubGroupEntity.id,
            name = expenseSubGroupEntity.name,
            description = expenseSubGroupEntity.description,
            expenseGroupId = expenseSubGroupEntity.expenseGroupId,
            archivedDate = null
        )
        updateExpenseSubGroup(expenseSubGroupEntityNotArchived)
    }

    fun getExpenseSubGroupByNameAndExpenseGroupId(name: String, expenseGroupId: Long): ExpenseSubGroupEntity? = expenseSubGroupDao.getByNameAndGroupId(name, expenseGroupId)

    fun unarchiveExpenseSubGroupsByExpenseGroupId(expenseGroupId: Long?) {
        return expenseSubGroupDao.unarchiveByGroupId(expenseGroupId)
    }

    fun getExpenseSubGroupByNameWithExpenseGroupIdLiveData(subGroupNameBinding: String, groupId: Long?): LiveData<ExpenseSubGroupEntity?> {
        return expenseSubGroupDao.getByNameAndGroupIdLiveData(subGroupNameBinding, groupId)
    }

    fun unarchiveExpenseSubGroupById(id: Long?) {
        return expenseSubGroupDao.unarchiveById(id)
    }

    fun getByNameNotArchived(name: String): ExpenseSubGroupEntity? {
        return expenseSubGroupDao.getByNameNotArchived(name)
    }

    fun getExpenseSubGroupByIdLiveData(id: Long?): LiveData<ExpenseSubGroupEntity?> {
        return expenseSubGroupDao.getExpenseSubGroupByIdLiveData(id)
    }

    fun getExpenseSubGroupById(id: Long): ExpenseSubGroupEntity? {
        return expenseSubGroupDao.getById(id)
    }

    fun getExpenseSubGroupByIdNotArchived(id: Long): ExpenseSubGroupEntity? {
        return expenseSubGroupDao.getByIdNotArchived(id)
    }

    fun updateArchivedDateById(id: Long?, date: String) {
        expenseSubGroupDao.updateArchivedDateById(id, date)
    }

}
