package com.romandevyatov.bestfinance.ui.adapters.analyze.models

data class GroupItem (
    val groupName: String? = null, // Category: Labor, Passive or Real Estate
    val groupSum: String? = null,
    var subGroupNameAndSumItem: List<SubGroupNameAndSumItem>? = null,
    var isExpanded: Boolean = true
)
