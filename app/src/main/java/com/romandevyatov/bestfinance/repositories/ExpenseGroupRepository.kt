package com.romandevyatov.bestfinance.repositories

import androidx.lifecycle.LiveData
import com.romandevyatov.bestfinance.db.dao.ExpenseGroupDao
import com.romandevyatov.bestfinance.db.entities.ExpenseGroup
import com.romandevyatov.bestfinance.db.entities.ExpenseSubGroup
import com.romandevyatov.bestfinance.db.entities.relations.ExpenseGroupWithExpenseSubGroup
import javax.inject.Inject

class ExpenseGroupRepository @Inject constructor(
    private val expenseGroupDao: ExpenseGroupDao
) {
//    fun getAllExpenseGroups(): LiveData<List<ExpenseGroup>> = expenseGroupDao.getAll()

    fun getAllExpenseGroups(): LiveData<List<ExpenseGroup>> = expenseGroupDao.getAll()

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

    fun getAllExpenseGroupWithExpenseSubGroup() : LiveData<List<ExpenseGroupWithExpenseSubGroup>> {
        return expenseGroupDao.getExpenseGroupWithExpenseSubGroup()
    }

}
