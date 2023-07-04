package com.romandevyatov.bestfinance.ui.adapters.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.CardIncomeGroupBinding
import com.romandevyatov.bestfinance.db.entities.IncomeGroup
import com.romandevyatov.bestfinance.ui.adapters.transactions_deprecated.income.DeleteItemClickListener


class IncomeGroupItemViewHolder(
    private val binding: CardIncomeGroupBinding,
    private val clickListener: DeleteItemClickListener<IncomeGroup>
) : RecyclerView.ViewHolder(binding.root) {

    fun bindItem(incomeGroup: IncomeGroup) {
        binding.textView.text = incomeGroup.name

        binding.removeIncomeGroupIcon.setOnClickListener{
            clickListener.deleteItem(incomeGroup)
        }
    }
}
