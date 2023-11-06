package com.romandevyatov.bestfinance.data.repositories

import androidx.lifecycle.LiveData
import com.romandevyatov.bestfinance.data.dao.ExpenseGroupDao
import com.romandevyatov.bestfinance.data.entities.ExpenseGroupEntity
import com.romandevyatov.bestfinance.data.entities.relations.ExpenseGroupWithExpenseSubGroups
import com.romandevyatov.bestfinance.data.entities.relations.ExpenseGroupWithExpenseSubGroupsIncludingExpenseHistories
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseGroupRepository @Inject constructor(
    private val expenseGroupDao: ExpenseGroupDao
) {

    fun getAllExpenseGroupsLiveData(): LiveData<List<ExpenseGroupEntity>> = expenseGroupDao.getAllExpenseGroupsLiveData()

    fun getAllExpenseGroupWithExpenseSubGroupLiveData() : LiveData<List<ExpenseGroupWithExpenseSubGroups>> {
        return expenseGroupDao.getAllWithExpenseSubGroupsLiveData()
    }

    fun getAllExpenseGroupsNotArchivedLiveData(): LiveData<List<ExpenseGroupEntity>> = expenseGroupDao.getAllNotArchivedLiveData()

    suspend fun insertExpenseGroup(expenseGroupEntity: ExpenseGroupEntity) {
        expenseGroupDao.insert(expenseGroupEntity)
    }

    suspend fun deleteExpenseGroup(expenseGroupEntity: ExpenseGroupEntity) {
        expenseGroupDao.delete(expenseGroupEntity)
    }

    suspend fun updateExpenseGroup(expenseGroupEntity: ExpenseGroupEntity) {
        expenseGroupDao.update(expenseGroupEntity)
    }

    suspend fun deleteExpenseGroupById(id: Long?) = expenseGroupDao.deleteById(id)

    suspend fun deleteAllExpenseGroups() = expenseGroupDao.deleteAll()

    fun getExpenseGroupWithExpenseSubGroupsByExpenseGroupNameLiveData(expenseGroupName: String): LiveData<ExpenseGroupWithExpenseSubGroups?> {
        return expenseGroupDao.getExpenseGroupWithExpenseSubGroupsByExpenseGroupNameLiveData(expenseGroupName)
    }

    fun getExpenseGroupNotArchivedWithExpenseSubGroupsNotArchivedByExpenseGroupNameLiveData(expenseGroupName: String): LiveData<ExpenseGroupWithExpenseSubGroups?> {
        return expenseGroupDao.getExpenseGroupNotArchivedWithExpenseSubGroupsNotArchivedByExpenseGroupNameLiveData(expenseGroupName)
    }

    fun getAllExpenseGroupWithExpenseSubGroupsIncludingExpenseHistoriesLiveData(): LiveData<List<ExpenseGroupWithExpenseSubGroupsIncludingExpenseHistories>> {
        return expenseGroupDao.getAllExpenseGroupWithExpenseSubGroupsWithExpenseHistoriesLiveData()
    }

    fun getExpenseGroupNotArchivedByNameLiveData(selectedExpenseGroupName: String): LiveData<ExpenseGroupEntity?> {
        return expenseGroupDao.getByNameNotArchivedLiveData(selectedExpenseGroupName)
    }

    fun getExpenseGroupByName(name: String): ExpenseGroupEntity? {
        return expenseGroupDao.getExpenseGroupByName(name)
    }

    fun getExpenseGroupWithExpenseSubGroupsByExpenseGroupName(name: String): ExpenseGroupWithExpenseSubGroups? {
        return expenseGroupDao.getExpenseGroupWithExpenseSubGroupsByExpenseGroupName(name)
    }

    suspend fun unarchiveExpenseGroup(expenseGroupEntity: ExpenseGroupEntity) {
        val expenseGroupEntityNotArchived = ExpenseGroupEntity(
            id = expenseGroupEntity.id,
            name = expenseGroupEntity.name,
            description = expenseGroupEntity.description,
            archivedDate = null
        )
        updateExpenseGroup(expenseGroupEntityNotArchived)
    }

    fun getExpenseGroupNameByNameLiveData(groupNameBinding: String): LiveData<ExpenseGroupEntity?> {
        return expenseGroupDao.getByNameLiveData(groupNameBinding)
    }

    fun getExpenseGroupArchivedByNameLiveData(name: String): LiveData<ExpenseGroupEntity?> {
        return expenseGroupDao.getExpenseGroupArchivedByNameLiveData(name)
    }

    fun getAllExpenseGroupArchivedLiveData(): LiveData<List<ExpenseGroupEntity>> {
        return expenseGroupDao.getAllExpenseGroupsArchivedLiveData()
    }

    fun getAllExpenseGroupsWithExpenseSubGroupsLiveData(): LiveData<List<ExpenseGroupWithExpenseSubGroups>> {
        return expenseGroupDao.getAllExpenseGroupsWithExpenseSubGroupsLiveData()
    }

    fun getExpenseGroupById(id: Long): ExpenseGroupEntity? {
        return expenseGroupDao.getById(id)
    }

    fun unarchiveExpenseGroupById(id: Long?) {
        expenseGroupDao.unarchiveExpenseGroupById(id)
    }

    fun getExpenseGroupWithExpenseSubGroupsByExpenseGroupId(id: Long?): LiveData<ExpenseGroupWithExpenseSubGroups?> {
        return expenseGroupDao.getExpenseGroupWithExpenseSubGroupsByExpenseGroupId(id)
    }

    fun getAllExpenseGroupNotArchivedLiveData(): LiveData<List<ExpenseGroupEntity>> {
        return expenseGroupDao.getAllExpenseGroupNotArchivedLiveData()
    }

    fun getExpenseGroupByIdLiveData(id: Long): LiveData<ExpenseGroupEntity?> {
        return expenseGroupDao.getByIdLiveData(id)
    }

    fun getExpenseGroupWithExpenseSubGroupsByExpenseGroupIdNotArchived(id: Long): ExpenseGroupWithExpenseSubGroups? {
        return expenseGroupDao.getExpenseGroupWithExpenseSubGroupsByExpenseGroupIdNotArchived(id)
    }

    fun updateArchivedDateById(id: Long?, date: String) {
        expenseGroupDao.updateArchivedDateById(id, date)
    }
}
