package com.romandevyatov.bestfinance.ui.adapters.analyze.models

data class GroupItem (
    val groupName: String? = null, // Category: Labor, Passive or Real Estate
    var subGroupNameAndSumItem: List<SubGroupNameAndSumItem>? = null,
    var isExpanded: Boolean = true
)
