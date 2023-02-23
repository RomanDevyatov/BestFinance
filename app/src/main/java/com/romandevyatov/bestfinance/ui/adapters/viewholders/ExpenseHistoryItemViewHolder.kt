package com.romandevyatov.bestfinance.ui.adapters.viewholders

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.ExpenseHistoryCardBinding
import com.romandevyatov.bestfinance.db.entities.relations.ExpenseHistoryWithExpenseSubGroupAndWallet
import java.time.format.DateTimeFormatter
import java.util.*

class ExpenseHistoryItemViewHolder (private val binding: ExpenseHistoryCardBinding
) : RecyclerView.ViewHolder(binding.root) {

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    fun bindItem(expenseHistory: ExpenseHistoryWithExpenseSubGroupAndWallet) {
        binding.balanceTextView.text = expenseHistory.expenseHistory.amount.toString()
        binding.expenseSubGroupNameTextView.text = expenseHistory.expenseSubGroup.name
//        val dateFormat = "yyyy-MM-dd HH:mm:ss"
//        binding.dateTextView.text = SimpleDateFormat(dateFormat).format(expenseHistory.expenseHistory.date)
        val iso8601DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        binding.dateTextView.text = expenseHistory.expenseHistory.createdDate?.format(iso8601DateTimeFormatter)

    }
}
