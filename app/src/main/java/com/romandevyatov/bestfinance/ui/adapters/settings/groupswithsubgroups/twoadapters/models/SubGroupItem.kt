package com.romandevyatov.bestfinance.ui.adapters.settings.groupswithsubgroups.twoadapters.models

data class SubGroupItem(
    val id: Long?,
    val name: String,
    var isChecked: Boolean = false,
    var isExist: Boolean = false
)
