package com.romandevyatov.bestfinance.ui.fragments.history.tabs.models

import com.romandevyatov.bestfinance.data.entities.ExpenseHistoryEntity
import java.time.LocalDate

data class ExpenseHistoryByDate(
    val date: LocalDate?,
    val incomeHistories: List<ExpenseHistoryEntity>
)
