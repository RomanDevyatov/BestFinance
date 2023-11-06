package com.romandevyatov.bestfinance.data.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.romandevyatov.bestfinance.data.entities.ExpenseGroupEntity
import com.romandevyatov.bestfinance.data.entities.ExpenseSubGroup

data class ExpenseGroupWithExpenseSubGroupsIncludingExpenseHistories(

    @Embedded
    val expenseGroupEntity: ExpenseGroupEntity?,

    @Relation(
        entity = ExpenseSubGroup::class,
        parentColumn = "id", // ExpenseGroup
        entityColumn = "expense_group_id" // ExpenseSubGroup
    )
    val expenseSubGroupWithExpenseHistories: List<ExpenseSubGroupWithExpenseHistories>
)
