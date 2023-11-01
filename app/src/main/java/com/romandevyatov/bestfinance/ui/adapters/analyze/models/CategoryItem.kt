package com.romandevyatov.bestfinance.ui.adapters.analyze.models

data class CategoryItem(
    val categoryName: String? = null, // Incomings or Expenses
    var groups: List<GroupItem>? = null,
    var isExpanded: Boolean = false
)
