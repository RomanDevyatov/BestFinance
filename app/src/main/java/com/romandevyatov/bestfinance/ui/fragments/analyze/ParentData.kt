package com.romandevyatov.bestfinance.ui.fragments.analyze

import com.romandevyatov.bestfinance.db.entities.relations.ExpenseGroupWithExpenseSubGroupsIncludingExpenseHistories
import com.romandevyatov.bestfinance.db.entities.relations.IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories

data class ParentData(
    val analyzeParentTitle: String? = null, // Incomes
    var type: String,
    var subParentNestedList: List<IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories>? = null,
    var subParentNestedListExpenses: List<ExpenseGroupWithExpenseSubGroupsIncludingExpenseHistories>? = null,
    var isExpanded: Boolean = false
)
