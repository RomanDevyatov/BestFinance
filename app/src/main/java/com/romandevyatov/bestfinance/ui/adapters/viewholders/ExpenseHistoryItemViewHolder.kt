package com.romandevyatov.bestfinance.ui.adapters.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.ExpenseHistoryCardBinding
import com.romandevyatov.bestfinance.db.entities.relations.ExpenseHistoryWithExpenseSubGroupAndWallet

class ExpenseHistoryItemViewHolder (private val binding: ExpenseHistoryCardBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bindItem(expenseHistory: ExpenseHistoryWithExpenseSubGroupAndWallet) {
        binding.balanceTextView.text = expenseHistory.expenseHistory.amount.toString()
        binding.expenseSubGroupNameTextView.text = expenseHistory.expenseSubGroup.name

    }
}
