package com.romandevyatov.bestfinance.ui.adapters.more.settings.settingsgroupswithsubgroups.tabs.models

data class SettingsSubGroupItem(
    val id: Long,
    val name: String,
    val groupId: Long,
    var isExist: Boolean = false
)
