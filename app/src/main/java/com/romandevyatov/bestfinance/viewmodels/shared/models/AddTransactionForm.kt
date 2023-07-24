package com.romandevyatov.bestfinance.viewmodels.shared.models

data class AddTransactionForm(
    val groupSpinnerPosition: Int? = null,
    val subGroupSpinnerPosition: Int? = null,
    val walletSpinnerPosition: Int? = null,
    val amount: String? = null,
    val date: String? = null,
    val time: String? = null,
    val comment: String? = null
)
