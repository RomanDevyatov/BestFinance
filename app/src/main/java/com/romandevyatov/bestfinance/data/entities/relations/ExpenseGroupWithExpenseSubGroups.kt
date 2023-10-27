package com.romandevyatov.bestfinance.data.entities.relations


import androidx.room.Embedded
import androidx.room.Relation
import com.romandevyatov.bestfinance.data.entities.ExpenseGroup
import com.romandevyatov.bestfinance.data.entities.ExpenseSubGroup

data class ExpenseGroupWithExpenseSubGroups(

    @Embedded
    val expenseGroup: ExpenseGroup,

    @Relation(
        parentColumn = "id", //ExpenseGroup
        entityColumn = "expense_group_id" // ExpenseSubGroup
    )
    val expenseSubGroups: List<ExpenseSubGroup>
)
