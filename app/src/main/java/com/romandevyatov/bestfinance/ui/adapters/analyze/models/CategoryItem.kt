package com.romandevyatov.bestfinance.ui.adapters.analyze.models

data class CategoryItem(
    val categoryName: String? = null,
    val categorySum: String? = null,
    val groups: List<GroupItem>? = null,
    var isExpanded: Boolean = false
)
