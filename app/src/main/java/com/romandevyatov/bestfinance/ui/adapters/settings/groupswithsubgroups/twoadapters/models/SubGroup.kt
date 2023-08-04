package com.romandevyatov.bestfinance.ui.adapters.settings.groupswithsubgroups.twoadapters.models

data class SubGroup(
    val id: Long?,
    val name: String,
    var isChecked: Boolean = false,
    var isExist: Boolean = false
)
