package com.romandevyatov.bestfinance.viewmodels.shared.models

data class AddTransactionForm(
    val groupSpinnerValue: String? = null,
    val subGroupSpinnerValue: String? = null,
    val walletSpinnerValue: String? = null,
    val amount: String? = null,
    val date: String? = null,
    val time: String? = null,
    val comment: String? = null
)
