package com.romandevyatov.bestfinance.viewmodels.shared.models

data class AddWalletForm (
    val name: String? = null,
    val balance: String? = null,
    val currencyCode: String? = null,
    val description: String? = null
)
