package com.romandevyatov.bestfinance.data.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.romandevyatov.bestfinance.data.entities.ExpenseHistoryEntity
import com.romandevyatov.bestfinance.data.entities.ExpenseSubGroupEntity

data class ExpenseSubGroupWithExpenseHistories(

    @Embedded
    var expenseSubGroupEntity: ExpenseSubGroupEntity?,

    @Relation(
        entity = ExpenseHistoryEntity::class,
        parentColumn = "id",
        entityColumn = "expense_sub_group_id"
    )
    var expenseHistoryEntities: List<ExpenseHistoryEntity>
)
