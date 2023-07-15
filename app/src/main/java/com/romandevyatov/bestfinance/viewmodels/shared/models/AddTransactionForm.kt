package com.romandevyatov.bestfinance.viewmodels.shared.models

data class AddTransactionForm(
    val groupSpinnerPosition: Int = 0,
    val subGroupSpinnerPosition: Int = 0,
    val walletSpinnerPosition: Int = 0,
    val amount: String? = null,
    val date: String? = null,
    val comment: String? = null
)
