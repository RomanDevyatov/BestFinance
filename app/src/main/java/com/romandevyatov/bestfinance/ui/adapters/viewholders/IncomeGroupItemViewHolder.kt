package com.romandevyatov.bestfinance.ui.adapters.viewholders

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.IncomeGroupCardBinding
import com.romandevyatov.bestfinance.db.entities.IncomeGroup
import com.romandevyatov.bestfinance.ui.adapters.IncomeGroupItemClickListener


class IncomeGroupItemViewHolder(
    private val binding: IncomeGroupCardBinding,
    private val clickListener: IncomeGroupItemClickListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bindItem(incomeGroup: IncomeGroup) {
        binding.textView.text = incomeGroup.name

        binding.removeIncomeGroupIcon.setOnClickListener{
            clickListener.deleteIncomeGroup(incomeGroup)
        }
    }
}