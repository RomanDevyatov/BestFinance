package com.romandevyatov.bestfinance.ui.adapters.settings.groupswithsubgroups.twoadapters.models

data class GroupWithSubGroups(val id: Long?, val name: String, var isArchived: Boolean = false, var subgroups: List<SubGroup>)
