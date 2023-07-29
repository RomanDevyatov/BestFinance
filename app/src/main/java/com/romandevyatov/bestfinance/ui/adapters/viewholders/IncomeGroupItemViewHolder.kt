package com.romandevyatov.bestfinance.ui.adapters.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.CardGroupIncomeBinding
import com.romandevyatov.bestfinance.data.entities.IncomeGroup
import com.romandevyatov.bestfinance.ui.adapters.cardactions.DeleteItemClickListener

class IncomeGroupItemViewHolder(
    private val binding: CardGroupIncomeBinding,
    private val clickListener: DeleteItemClickListener<IncomeGroup>
) : RecyclerView.ViewHolder(binding.root) {

    fun bindItem(incomeGroup: IncomeGroup) {
        binding.textView.text = incomeGroup.name

        binding.removeIncomeGroupIcon.setOnClickListener{
            clickListener.deleteIncomeGroupItem(incomeGroup)
        }
    }
}
