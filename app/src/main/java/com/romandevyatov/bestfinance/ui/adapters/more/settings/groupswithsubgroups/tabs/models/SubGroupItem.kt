package com.romandevyatov.bestfinance.ui.adapters.more.settings.groupswithsubgroups.tabs.models

data class SubGroupItem(
    val id: Long,
    val name: String,
    val groupId: Long,
    var isExist: Boolean = false
)
