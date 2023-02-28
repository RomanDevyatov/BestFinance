package com.romandevyatov.bestfinance.ui.fragments.analyze

import com.romandevyatov.bestfinance.db.entities.mediator.ParentData

interface ChangeIsExpanded {
    fun doExpandUpdate(tempList: MutableList<ParentData>)
}