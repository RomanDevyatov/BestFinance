package com.romandevyatov.bestfinance.ui.adapters.settings.generalcategories.recyclerviewapproach.twoadapters.models

data class GroupWithSubGroups(val id: Long?, val name: String, var isExist: Boolean = false, var subgroups: List<SubGroup>)
