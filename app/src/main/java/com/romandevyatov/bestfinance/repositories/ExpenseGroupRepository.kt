package com.romandevyatov.bestfinance.repositories


import androidx.lifecycle.LiveData
import com.romandevyatov.bestfinance.db.dao.ExpenseGroupDao
import com.romandevyatov.bestfinance.db.entities.ExpenseGroup
import com.romandevyatov.bestfinance.db.entities.relations.ExpenseGroupWithExpenseSubGroups
import com.romandevyatov.bestfinance.db.entities.relations.ExpenseGroupWithExpenseSubGroupsIncludingExpenseHistories
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseGroupRepository @Inject constructor(
    private val expenseGroupDao: ExpenseGroupDao
) {

    fun getAllExpenseGroupWithExpenseSubGroupLiveData() : LiveData<List<ExpenseGroupWithExpenseSubGroups>> {
        return expenseGroupDao.getAllExpenseGroupWithExpenseSubGroupsLiveData()
    }

    fun getAllExpenseGroups(): LiveData<List<ExpenseGroup>> = expenseGroupDao.getAllLiveData()

    suspend fun insertExpenseGroup(expenseGroup: ExpenseGroup) {
        expenseGroupDao.insert(expenseGroup)
    }

    suspend fun deleteExpenseGroup(expenseGroup: ExpenseGroup) {
        expenseGroupDao.delete(expenseGroup)
    }

    suspend fun updateExpenseGroup(expenseGroup: ExpenseGroup) {
        expenseGroupDao.update(expenseGroup)
    }

    suspend fun deleteExpenseGroupById(id: Int) = expenseGroupDao.deleteById(id)

    suspend fun deleteAllExpenseGroups() = expenseGroupDao.deleteAll()

    fun getExpenseGroupWithExpenseSubGroupsByExpenseGroupNameLiveData(expenseGroupName: String): LiveData<ExpenseGroupWithExpenseSubGroups> {
        return expenseGroupDao.getExpenseGroupWithExpenseSubGroupsByExpenseGroupNameLiveData(expenseGroupName)
    }

    fun getAllExpenseGroupWithExpenseSubGroupsIncludingExpenseHistoriesLiveData(): LiveData<List<ExpenseGroupWithExpenseSubGroupsIncludingExpenseHistories>> {
        return expenseGroupDao.getAllExpenseGroupWithExpenseSubGroupsWithExpenseHistoriesLiveData()
    }

    fun getExpenseGroupByNameAndNotArchivedLiveData(selectedExpenseGroupName: String): LiveData<ExpenseGroup> {
        return expenseGroupDao.getExpenseGroupByNameAndNotArchivedLiveData(selectedExpenseGroupName)
    }

}
