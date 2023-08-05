package com.romandevyatov.bestfinance.data.repositories


import androidx.lifecycle.LiveData
import com.romandevyatov.bestfinance.data.dao.ExpenseGroupDao
import com.romandevyatov.bestfinance.data.entities.ExpenseGroup
import com.romandevyatov.bestfinance.data.entities.relations.ExpenseGroupWithExpenseSubGroups
import com.romandevyatov.bestfinance.data.entities.relations.ExpenseGroupWithExpenseSubGroupsIncludingExpenseHistories
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseGroupRepository @Inject constructor(
    private val expenseGroupDao: ExpenseGroupDao
) {

    fun getAllExpenseGroupsLiveData(): LiveData<List<ExpenseGroup>> = expenseGroupDao.getAllExpenseGroupsLiveData()

    fun getAllExpenseGroupWithExpenseSubGroupLiveData() : LiveData<List<ExpenseGroupWithExpenseSubGroups>> {
        return expenseGroupDao.getAllWithExpenseSubGroupsLiveData()
    }

    fun getAllExpenseGroupsNotArchivedLiveData(): LiveData<List<ExpenseGroup>> = expenseGroupDao.getAllNotArchivedLiveData()

    suspend fun insertExpenseGroup(expenseGroup: ExpenseGroup) {
        expenseGroupDao.insert(expenseGroup)
    }

    suspend fun deleteExpenseGroup(expenseGroup: ExpenseGroup) {
        expenseGroupDao.delete(expenseGroup)
    }

    suspend fun updateExpenseGroup(expenseGroup: ExpenseGroup) {
        expenseGroupDao.update(expenseGroup)
    }

    suspend fun deleteExpenseGroupById(id: Long?) = expenseGroupDao.deleteById(id)

    suspend fun deleteAllExpenseGroups() = expenseGroupDao.deleteAll()

    fun getExpenseGroupWithExpenseSubGroupsByExpenseGroupNameLiveData(expenseGroupName: String): LiveData<ExpenseGroupWithExpenseSubGroups> {
        return expenseGroupDao.getExpenseGroupWithExpenseSubGroupsByExpenseGroupNameLiveData(expenseGroupName)
    }

    fun getExpenseGroupNotArchivedWithExpenseSubGroupsNotArchivedByExpenseGroupNameLiveData(expenseGroupName: String): LiveData<ExpenseGroupWithExpenseSubGroups> {
        return expenseGroupDao.getExpenseGroupNotArchivedWithExpenseSubGroupsNotArchivedByExpenseGroupNameLiveData(expenseGroupName)
    }

    fun getAllExpenseGroupWithExpenseSubGroupsIncludingExpenseHistoriesLiveData(): LiveData<List<ExpenseGroupWithExpenseSubGroupsIncludingExpenseHistories>> {
        return expenseGroupDao.getAllExpenseGroupWithExpenseSubGroupsWithExpenseHistoriesLiveData()
    }

    fun getExpenseGroupNotArchivedByNameLiveData(selectedExpenseGroupName: String): LiveData<ExpenseGroup> {
        return expenseGroupDao.getByNameNotArchivedLiveData(selectedExpenseGroupName)
    }

    fun getExpenseGroupWithExpenseSubGroupsByExpenseGroupNameNotArchived(name: String): ExpenseGroupWithExpenseSubGroups {
        return expenseGroupDao.getExpenseGroupWithExpenseSubGroupsByExpenseGroupNameNotArchived(name)
    }

    fun getExpenseGroupByName(name: String): ExpenseGroup {
        return expenseGroupDao.getExpenseGroupByName(name)
    }

    fun getExpenseGroupWithExpenseSubGroupsByExpenseGroupName(name: String): ExpenseGroupWithExpenseSubGroups {
        return expenseGroupDao.getExpenseGroupWithExpenseSubGroupsByExpenseGroupName(name)
    }

    suspend fun unarchiveExpenseGroup(expenseGroup: ExpenseGroup) {
        val expenseGroupNotArchived = ExpenseGroup(
            id = expenseGroup.id,
            name = expenseGroup.name,
            description = expenseGroup.description,
            archivedDate = null
        )
        updateExpenseGroup(expenseGroupNotArchived)
    }

    fun getExpenseGroupNameByNameLiveData(groupNameBinding: String): LiveData<ExpenseGroup>? {
        return expenseGroupDao.getByNameLiveData(groupNameBinding)
    }

    fun getExpenseGroupArchivedByNameLiveData(name: String): LiveData<ExpenseGroup>? {
        return expenseGroupDao.getExpenseGroupArchivedByNameLiveData(name)
    }

    fun getAllExpenseGroupArchivedLiveData(): LiveData<List<ExpenseGroup>>? {
        return expenseGroupDao.getAllExpenseGroupsArchivedLiveData()
    }

    fun getAllExpenseGroupsWithExpenseSubGroupsLiveData(): LiveData<List<ExpenseGroupWithExpenseSubGroups>>? {
        return expenseGroupDao.getAllExpenseGroupsWithExpenseSubGroupsLiveData()
    }

    fun getExpenseGroupById(id: Long): ExpenseGroup {
        return expenseGroupDao.getById(id)
    }

    fun unarchiveExpenseGroupById(id: Long?) {
        expenseGroupDao.unarchiveExpenseGroupById(id)
    }


}
