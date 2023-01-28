package com.romandevyatov.bestfinance.db.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.romandevyatov.bestfinance.db.entities.ExpenseGroup
import com.romandevyatov.bestfinance.db.entities.ExpenseSubGroup
// and  - 1 to 1
// with - 1 to 0 or many
data class ExpenseGroupWithExpenseSubGroup(
    @Embedded
    val expenseGroup: ExpenseGroup,

    @Relation(
        parentColumn = "id", //ExpenseGroup
        entityColumn = "expense_group_id" // ExpenseSubGroup
    )
    val expenseSubGroups: List<ExpenseSubGroup>
)
