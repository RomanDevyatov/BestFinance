package com.romandevyatov.bestfinance.ui.adapters

import com.romandevyatov.bestfinance.db.entities.IncomeGroup

interface IncomeGroupItemClickListener {
    fun deleteIncomeGroup(incomeGroup: IncomeGroup)
//    fun editTaskItem(taskItem: TaskItem)
}