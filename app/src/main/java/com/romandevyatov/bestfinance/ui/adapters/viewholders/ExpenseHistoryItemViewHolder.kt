package com.romandevyatov.bestfinance.ui.adapters.viewholders

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.ExpenseHistoryCardBinding
import com.romandevyatov.bestfinance.db.entities.relations.ExpenseHistoryWithExpenseSubGroupAndWallet
import java.text.SimpleDateFormat
import java.util.*

class ExpenseHistoryItemViewHolder (private val binding: ExpenseHistoryCardBinding
) : RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("SimpleDateFormat")
    fun bindItem(expenseHistory: ExpenseHistoryWithExpenseSubGroupAndWallet) {
        binding.balanceTextView.text = expenseHistory.expenseHistory.amount.toString()
        binding.expenseSubGroupNameTextView.text = expenseHistory.expenseSubGroup.name
        val dateFormat = "yyyy-MM-dd HH:mm:ss"
        binding.dateTextView.text = SimpleDateFormat(dateFormat).format(expenseHistory.expenseHistory.date)

    }
}
