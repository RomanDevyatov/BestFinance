package com.romandevyatov.bestfinance.ui.adapters.settings.groupswithsubgroups.models

data class SubGroupItem(
    val id: Long,
    val name: String,
    val groupId: Long,
    var isExist: Boolean = false
)
