package com.romandevyatov.bestfinance.viewmodels.shared

data class TransferForm (
        val fromWalletSpinnerPosition: Int = 0,
        val toWalletSpinnerPosition: Int = 0,
        val amount: String? = null,
        val comment: String? = null,
        val date: String? = null
)