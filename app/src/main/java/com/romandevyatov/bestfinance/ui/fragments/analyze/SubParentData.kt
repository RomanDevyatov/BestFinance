package com.romandevyatov.bestfinance.ui.fragments.analyze

import com.romandevyatov.bestfinance.db.entities.relations.ExpenseSubGroupWithExpenseHistories
import com.romandevyatov.bestfinance.db.entities.relations.IncomeSubGroupWithIncomeHistories
import com.romandevyatov.bestfinance.utils.Constants

data class SubParentData(
    val parentTitle: String? = null,
    var type: String,
    var childNestedList: List<IncomeSubGroupWithIncomeHistories>? = null,
    var childNestedListExpenses: List<ExpenseSubGroupWithExpenseHistories>? = null,
    var isExpanded: Boolean = false
)
