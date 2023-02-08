package com.romandevyatov.bestfinance.ui.adapters.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.IncomeHistoryCardBinding
import com.romandevyatov.bestfinance.db.entities.IncomeHistory


class IncomeHistoryItemViewHolder(
    private val binding: IncomeHistoryCardBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bindItem(incomeHistory: IncomeHistory) {
        binding.balanceTextView.text = incomeHistory.amount.toString()
        binding.incomeGroupNameOfIncomeHistoryTextView.text = incomeHistory.incomeGroupId.toString()

    }
}