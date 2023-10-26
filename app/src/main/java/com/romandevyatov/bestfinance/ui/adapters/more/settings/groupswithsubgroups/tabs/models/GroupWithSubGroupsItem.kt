package com.romandevyatov.bestfinance.ui.adapters.more.settings.groupswithsubgroups.tabs.models

data class GroupWithSubGroupsItem(
    val id: Long? = null,
    val name: String,
    var isExist: Boolean = false,
    var subgroups: MutableList<SubGroupItem> = mutableListOf()
)
