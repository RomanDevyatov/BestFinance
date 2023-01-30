package com.romandevyatov.bestfinance.db.dao.relation

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.romandevyatov.bestfinance.db.entities.relations.ExpenseGroupWithExpenseSubGroup


@Dao
interface ExpenseGroupWithExpenseSubGroupDao {

    @Transaction
    @Query("SELECT * FROM expense_group")
    fun getExpenseGroupWithExpenseSubGroup(): LiveData<List<ExpenseGroupWithExpenseSubGroup>>

}
