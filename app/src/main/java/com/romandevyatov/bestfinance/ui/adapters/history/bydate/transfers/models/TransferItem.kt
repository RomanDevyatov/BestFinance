package com.romandevyatov.bestfinance.ui.adapters.history.bydate.transfers.models

import java.time.LocalDateTime

class TransferItem(
    val id: Long?,
    val fromName: String?,
    val toName: String,
    val amount: String,
    val amountBase: String,
    val comment: String,
    val date: LocalDateTime?
)