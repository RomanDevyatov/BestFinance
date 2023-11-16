package com.romandevyatov.bestfinance.ui.fragments.history.tabs.models

import com.romandevyatov.bestfinance.data.entities.TransferHistoryEntity
import java.time.LocalDate

data class TransferHistoryByDate(
    val date: LocalDate?,
    val transferHistories: List<TransferHistoryEntity>
)
