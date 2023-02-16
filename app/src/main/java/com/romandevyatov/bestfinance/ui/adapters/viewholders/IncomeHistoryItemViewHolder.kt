package com.romandevyatov.bestfinance.ui.adapters.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.IncomeHistoryCardBinding
import com.romandevyatov.bestfinance.db.entities.IncomeHistory
import com.romandevyatov.bestfinance.db.entities.relations.IncomeHistoryWithIncomeGroupAndWallet
import java.text.SimpleDateFormat


class IncomeHistoryItemViewHolder(
    private val binding: IncomeHistoryCardBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bindItem(incomeHistory: IncomeHistoryWithIncomeGroupAndWallet) {
        binding.balanceTextView.text = incomeHistory.incomeHistory.amount.toString()
        binding.incomeGroupNameOfIncomeHistoryTextView.text = incomeHistory.incomeGroup.name
        val dateFormat = "yyyy-MM-dd HH:mm:ss"
        binding.dateIncomeTextView.text = SimpleDateFormat(dateFormat).format(incomeHistory.incomeHistory.date)


    }
}