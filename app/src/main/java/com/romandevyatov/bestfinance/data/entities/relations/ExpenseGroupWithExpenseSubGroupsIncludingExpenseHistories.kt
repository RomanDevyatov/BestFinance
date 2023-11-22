package com.romandevyatov.bestfinance.data.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.romandevyatov.bestfinance.data.entities.ExpenseGroupEntity
import com.romandevyatov.bestfinance.data.entities.ExpenseSubGroupEntity

data class ExpenseGroupWithExpenseSubGroupsIncludingExpenseHistories(

    @Embedded
    val expenseGroupEntity: ExpenseGroupEntity?,

    @Relation(
        entity = ExpenseSubGroupEntity::class,
        parentColumn = "id", // ExpenseGroup
        entityColumn = "expense_group_id" // ExpenseSubGroup
    )
    val expenseSubGroupWithExpenseHistoriesEntity: List<ExpenseSubGroupWithExpenseHistories>
)
