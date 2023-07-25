package com.romandevyatov.bestfinance.viewmodels.shared.models

data class AddTransactionForm(
    val groupSpinnerPosition: Int? = -1,
    val subGroupSpinnerPosition: Int? = -1,
    val walletSpinnerPosition: Int? = -1,
    val amount: String? = null,
    val date: String? = null,
    val time: String? = null,
    val comment: String? = null
)
