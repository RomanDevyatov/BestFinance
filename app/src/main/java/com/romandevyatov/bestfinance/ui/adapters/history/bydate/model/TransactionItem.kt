package com.romandevyatov.bestfinance.ui.adapters.history.bydate.model

import java.time.LocalDateTime

data class TransactionItem(
    val id: Long?,
    val groupName: String?,
    val subGroupGroupName: String,
    val amount: Double,
    val comment: String,
    val date: LocalDateTime?,
    val walletName: String
)
