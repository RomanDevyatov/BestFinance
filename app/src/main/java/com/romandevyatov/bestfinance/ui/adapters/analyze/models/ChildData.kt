package com.romandevyatov.bestfinance.ui.adapters.analyze.models

import com.romandevyatov.bestfinance.db.entities.relations.ExpenseSubGroupWithExpenseHistories
import com.romandevyatov.bestfinance.db.entities.relations.IncomeSubGroupWithIncomeHistories

data class ChildData(
    val incomeSubGroupWithIncomeHistories: IncomeSubGroupWithIncomeHistories?,
    val expenseSubGroupIncludingExpenseHistories: ExpenseSubGroupWithExpenseHistories?,
    val type: String
)
