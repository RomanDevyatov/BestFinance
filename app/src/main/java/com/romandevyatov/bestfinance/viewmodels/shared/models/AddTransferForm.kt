package com.romandevyatov.bestfinance.viewmodels.shared.models

data class AddTransferForm (
    val fromWalletSpinnerPosition: Int = -1,
    val toWalletSpinnerPosition: Int = -1,
    val amount: String? = null,
    val comment: String? = null,
    val date: String? = null,
    val time: String? = null
)