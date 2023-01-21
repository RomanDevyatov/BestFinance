package com.romandevyatov.bestfinance.repositories

import com.romandevyatov.bestfinance.db.dao.ExpenseGroupDao
import javax.inject.Inject

class ExpenseGroupRepository @Inject constructor(
    private val expenseGroupDao: ExpenseGroupDao
) {
//    fun getAllExpenseGroups(): LiveData<List<ExpenseGroup>> = expenseGroupDao.getAll()
}