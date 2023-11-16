package com.romandevyatov.bestfinance.data.entities.relations


import androidx.room.Embedded
import androidx.room.Relation
import com.romandevyatov.bestfinance.data.entities.ExpenseGroupEntity
import com.romandevyatov.bestfinance.data.entities.ExpenseSubGroupEntity

data class ExpenseGroupWithExpenseSubGroups(

    @Embedded
    val expenseGroupEntity: ExpenseGroupEntity,

    @Relation(
        parentColumn = "id", //ExpenseGroup
        entityColumn = "expense_group_id" // ExpenseSubGroup
    )
    val expenseSubGroupEntities: List<ExpenseSubGroupEntity>
)
