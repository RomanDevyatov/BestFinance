package com.romandevyatov.bestfinance.ui.adapters.more.settings.settingswallets.models

data class SettingsWalletItem(
    val id: Long?,
    val name: String,
    val balance: String,
    var isExist: Boolean = false
)
