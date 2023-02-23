package com.romandevyatov.bestfinance.db.entities.relations


import androidx.room.Embedded
import androidx.room.Relation
import com.romandevyatov.bestfinance.db.entities.ExpenseHistory
import com.romandevyatov.bestfinance.db.entities.ExpenseSubGroup


data class ExpenseSubGroupWithExpenseHistories (

    @Embedded
    var expenseSubGroup: ExpenseSubGroup,

    @Relation(
        entity = ExpenseHistory::class,
        parentColumn = "id",
        entityColumn = "expense_sub_group_id"
    )
    var expenseHistory: List<ExpenseHistory>

)
