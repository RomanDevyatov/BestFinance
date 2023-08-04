package com.romandevyatov.bestfinance.ui.adapters.settings.groupswithsubgroups.models

data class GroupWithSubGroupsItem(val id: Long?, val name: String, var isArchived: Boolean = false, var subgroups: MutableList<SubGroupItem>?)
