package com.romandevyatov.bestfinance.db.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.romandevyatov.bestfinance.db.entities.IncomeGroup
import com.romandevyatov.bestfinance.db.entities.IncomeSubGroup


data class IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories (

    @Embedded
    val incomeGroup: IncomeGroup,

    @Relation(
        entity = IncomeSubGroup::class,
        parentColumn = "id",
        entityColumn = "income_group_id"
    )
    val incomeSubGroupWithIncomeHistories: List<IncomeSubGroupWithIncomeHistories>

)
