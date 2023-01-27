package com.romandevyatov.bestfinance.ui.adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.IncomeGroupCardBinding
import com.romandevyatov.bestfinance.db.entities.IncomeGroup

class IncomeGroupItemViewHolder  (
    private val context: Context,
    private val binding: IncomeGroupCardBinding,
    private val clickListener: IncomeGroupItemClickListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bindItem(incomeGroup: IncomeGroup) {
        binding.textView.text = incomeGroup.name

        binding.removeIncomeGroupIcon.setOnClickListener{
            clickListener.deleteIncomeGroup(incomeGroup)
        }
//        binding.taskCellContainer.setOnClickListener{
//            clickListener.editTaskItem(incomeGroup)
//        }
    }
}