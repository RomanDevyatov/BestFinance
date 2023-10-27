package com.romandevyatov.bestfinance.ui.adapters.history.bydate.model

import java.time.LocalDateTime

class TransferItem(
    val id: Long?,
    val fromName: String?,
    val toName: String,
    val amount: Double,
    val comment: String,
    val date: LocalDateTime?
)