package com.romandevyatov.bestfinance.db.entities.relations


import androidx.room.Embedded
import androidx.room.Relation
import com.romandevyatov.bestfinance.db.entities.ExpenseGroup
import com.romandevyatov.bestfinance.db.entities.ExpenseSubGroup

data class ExpenseGroupWithExpenseSubGroups(

    @Embedded
    val expenseGroup: ExpenseGroup,

    @Relation(
        parentColumn = "id", //ExpenseGroup
        entityColumn = "expense_group_id" // ExpenseSubGroup
    )
    val expenseSubGroups: List<ExpenseSubGroup>

)
