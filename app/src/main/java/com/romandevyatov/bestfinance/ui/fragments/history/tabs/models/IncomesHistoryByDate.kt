package com.romandevyatov.bestfinance.ui.fragments.history.tabs.models

import com.romandevyatov.bestfinance.data.entities.IncomeHistoryEntity
import java.time.LocalDate

data class IncomesHistoryByDate(
    val date: LocalDate?,
    val incomeHistories: List<IncomeHistoryEntity>
)
