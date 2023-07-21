package com.romandevyatov.bestfinance.viewmodels.shared.models

data class TransferForm (
    val fromWalletSpinnerPosition: Int? = null,
    val toWalletSpinnerPosition: Int? = null,
    val amount: String? = null,
    val comment: String? = null,
    val date: String? = null
)