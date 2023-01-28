package com.romandevyatov.bestfinance.repositories

import androidx.lifecycle.LiveData
import com.romandevyatov.bestfinance.db.dao.ExpenseGroupDao
import com.romandevyatov.bestfinance.db.entities.relations.ExpenseGroupWithExpenseSubGroup
import javax.inject.Inject

class ExpenseGroupRepository @Inject constructor(
    private val expenseGroupDao: ExpenseGroupDao
) {
//    fun getAllExpenseGroups(): LiveData<List<ExpenseGroup>> = expenseGroupDao.getAll()

    fun getAllExpenseGroupWithExpenseSubGroup() : LiveData<List<ExpenseGroupWithExpenseSubGroup>> {
        return expenseGroupDao.getExpenseGroupWithExpenseSubGroupsId()
    }
}
