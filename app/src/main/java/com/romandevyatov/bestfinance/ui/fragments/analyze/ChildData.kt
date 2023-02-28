package com.romandevyatov.bestfinance.ui.fragments.analyze

import com.romandevyatov.bestfinance.db.entities.relations.ExpenseSubGroupWithExpenseHistories
import com.romandevyatov.bestfinance.db.entities.relations.IncomeSubGroupWithIncomeHistories

data class ChildData(
    val incomeSubGroupWithIncomeHistories: IncomeSubGroupWithIncomeHistories?,
    val expenseSubGroupsIncludingExpenseHistories: ExpenseSubGroupWithExpenseHistories?,
    val type: String
)
