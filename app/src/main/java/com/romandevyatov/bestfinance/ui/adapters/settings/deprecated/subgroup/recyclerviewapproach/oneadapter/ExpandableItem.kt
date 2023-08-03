package com.romandevyatov.bestfinance.ui.adapters.settings.deprecated.subgroup.recyclerviewapproach.oneadapter

sealed class ExpandableItem

data class Group3(val name: String, val subgroups: List<SubGroup3>) : ExpandableItem()

data class SubGroup3(val name: String, var isChecked: Boolean) : ExpandableItem()
