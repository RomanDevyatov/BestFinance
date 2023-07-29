package com.romandevyatov.bestfinance.ui.adapters.analyze.models

import com.romandevyatov.bestfinance.data.entities.relations.ExpenseSubGroupWithExpenseHistories
import com.romandevyatov.bestfinance.data.entities.relations.IncomeSubGroupWithIncomeHistories

data class SubParentData(
    val parentTitle: String? = null, // Category: Labor, Passive or Real Estate
    var type: String,
    var childNestedListOfIncomeSubGroup: List<IncomeSubGroupWithIncomeHistories>? = null,
    var childNestedListOfExpenseSubGroup: List<ExpenseSubGroupWithExpenseHistories>? = null,
    var isExpanded: Boolean = true
)
