package com.romandevyatov.bestfinance.ui.fragments.analyze

import com.romandevyatov.bestfinance.db.entities.mediator.AnalyzeParentData

interface ChangeIsExpanded {
    fun doExpandUpdate(tempList: MutableList<AnalyzeParentData>)
}