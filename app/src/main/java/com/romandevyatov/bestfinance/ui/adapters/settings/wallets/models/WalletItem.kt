package com.romandevyatov.bestfinance.ui.adapters.settings.wallets.models

data class WalletItem(
    val id: Long?,
    val name: String,
    var isExist: Boolean = false
)
