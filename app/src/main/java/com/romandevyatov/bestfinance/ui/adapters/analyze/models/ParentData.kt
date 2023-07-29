package com.romandevyatov.bestfinance.ui.adapters.analyze.models

import com.romandevyatov.bestfinance.data.entities.relations.ExpenseGroupWithExpenseSubGroupsIncludingExpenseHistories
import com.romandevyatov.bestfinance.data.entities.relations.IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories

data class ParentData(
    val analyzeParentTitle: String? = null, // Incomings or Expenses
    var type: String,
    var subParentNestedListIncomings: List<IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories>? = null,
    var subParentNestedListExpenses: List<ExpenseGroupWithExpenseSubGroupsIncludingExpenseHistories>? = null,
    var isExpanded: Boolean = false
)
