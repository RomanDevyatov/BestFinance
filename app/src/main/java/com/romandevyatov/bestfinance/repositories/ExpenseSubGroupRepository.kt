package com.romandevyatov.bestfinance.repositories

import androidx.lifecycle.LiveData
import com.romandevyatov.bestfinance.db.dao.ExpenseSubGroupDao
import com.romandevyatov.bestfinance.db.entities.ExpenseSubGroup
import com.romandevyatov.bestfinance.db.entities.IncomeSubGroup
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseSubGroupRepository @Inject constructor(
    private val expenseSubGroupDao: ExpenseSubGroupDao
) {

    fun getAllExpenseSubGroups(): LiveData<List<ExpenseSubGroup>> = expenseSubGroupDao.getAll()

    fun getAllExpenseSubGroupsWhereArchivedDateIsNull(): LiveData<List<ExpenseSubGroup>> = expenseSubGroupDao.getAllWhereArchivedDateIsNull()


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

    fun getExpenseSubGroupByName(name: String): LiveData<ExpenseSubGroup> = expenseSubGroupDao.getExpenseSubGroupByName(name)

    fun getExpenseSubGroupByNameWhereArchivedDateIsNull(name: String): LiveData<ExpenseSubGroup> = expenseSubGroupDao.getByNameWhereArchivedDateIsNull(name)

}
