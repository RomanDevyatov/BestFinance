package com.romandevyatov.bestfinance.ui.adapters.more.settings.settingsgroupswithsubgroups.tabs.models

data class SettingsGroupWithSubGroupsItem(
    val id: Long? = null,
    val name: String,
    var isExist: Boolean = false,
    var subgroups: MutableList<SettingsSubGroupItem> = mutableListOf()
)
