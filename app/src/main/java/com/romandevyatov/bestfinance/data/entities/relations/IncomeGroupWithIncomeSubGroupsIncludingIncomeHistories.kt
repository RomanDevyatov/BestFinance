package com.romandevyatov.bestfinance.data.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.romandevyatov.bestfinance.data.entities.IncomeGroupEntity
import com.romandevyatov.bestfinance.data.entities.IncomeSubGroup

data class IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories (

    @Embedded
    val incomeGroupEntity: IncomeGroupEntity?,

    @Relation(
        entity = IncomeSubGroup::class,
        parentColumn = "id",
        entityColumn = "income_group_id"
    )
    val incomeSubGroupWithIncomeHistories: List<IncomeSubGroupWithIncomeHistories>
)
