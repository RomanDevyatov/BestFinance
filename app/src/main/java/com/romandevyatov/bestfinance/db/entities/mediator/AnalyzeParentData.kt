package com.romandevyatov.bestfinance.db.entities.mediator

import com.romandevyatov.bestfinance.db.entities.relations.ExpenseGroupWithExpenseSubGroupsIncludingExpenseHistories
import com.romandevyatov.bestfinance.db.entities.relations.IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories

data class AnalyzeParentData(
    val analyzeParentTitle: String? = null, // Incomes
    var type: String,
    var subParentNestedList: List<IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories>? = null,
    var subParentNestedListExpenses: List<ExpenseGroupWithExpenseSubGroupsIncludingExpenseHistories>? = null,
    var isExpanded: Boolean = false
)
