package com.romandevyatov.bestfinance.data.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.romandevyatov.bestfinance.data.entities.IncomeHistory
import com.romandevyatov.bestfinance.data.entities.IncomeSubGroup

class IncomeSubGroupWithIncomeHistories (

    @Embedded
    val incomeSubGroup: IncomeSubGroup,

    @Relation(
        entity = IncomeHistory::class,
        parentColumn = "id",
        entityColumn = "income_sub_group_id"
    )
    val incomeHistories: List<IncomeHistory>

)