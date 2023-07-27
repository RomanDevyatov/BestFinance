package com.romandevyatov.bestfinance.viewmodels.shared.models

data class AddTransferForm (
    val fromWalletSpinnerValue: String? = null,
    val toWalletSpinnerValue: String? = null,
    val amount: String? = null,
    val comment: String? = null,
    val date: String? = null,
    val time: String? = null
)
