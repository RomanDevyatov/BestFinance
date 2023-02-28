package com.romandevyatov.bestfinance.db.entities.mediator

import com.romandevyatov.bestfinance.db.entities.relations.IncomeSubGroupWithIncomeHistories
import com.romandevyatov.bestfinance.utils.Constants

data class ParentData(
    val parentTitle: String? = null,
    var type: Int = Constants.PARENT,
    var nestedList: MutableList<IncomeSubGroupWithIncomeHistories> = ArrayList(),
    var isExpanded: Boolean = false
)
