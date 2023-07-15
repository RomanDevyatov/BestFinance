package com.romandevyatov.bestfinance.viewmodels.shared

data class TransferForm (
        val fromWalletSpinnerPosition: Int,
        val toWalletSpinnerPosition: Int,
        val amount: String,
        val comment: String? = null,
        val date: String? = null
)