package com.romandevyatov.bestfinance.db.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.romandevyatov.bestfinance.db.entities.ExpenseGroup
import com.romandevyatov.bestfinance.db.entities.ExpenseSubGroup

data class ExpenseGroupWithExpenseSubGroupsIncludingExpenseHistories(

    @Embedded
    val expenseGroup: ExpenseGroup,

    @Relation(
        entity = ExpenseSubGroup::class,
        parentColumn = "id", // ExpenseGroup
        entityColumn = "expense_group_id" // ExpenseSubGroup
    )
    val expenseSubGroupWithExpenseHistories: List<ExpenseSubGroupWithExpenseHistories>

)
