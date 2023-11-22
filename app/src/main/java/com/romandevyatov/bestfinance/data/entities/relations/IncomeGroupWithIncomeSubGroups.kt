package com.romandevyatov.bestfinance.data.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.romandevyatov.bestfinance.data.entities.IncomeGroupEntity
import com.romandevyatov.bestfinance.data.entities.IncomeSubGroup

data class IncomeGroupWithIncomeSubGroups(

    @Embedded
    val incomeGroupEntity: IncomeGroupEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "income_group_id"
    )
    val incomeSubGroups: List<IncomeSubGroup>
)
