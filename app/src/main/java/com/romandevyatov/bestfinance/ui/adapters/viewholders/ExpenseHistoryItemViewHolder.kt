package com.romandevyatov.bestfinance.ui.adapters.viewholders

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.CardHistoryExpenseBinding
import com.romandevyatov.bestfinance.data.entities.ExpenseGroup
import com.romandevyatov.bestfinance.data.entities.relations.ExpenseHistoryWithExpenseSubGroupAndWallet
import com.romandevyatov.bestfinance.data.roomdb.converters.LocalDateTimeRoomTypeConverter.Companion.dateTimeFormatter
import java.util.*

class ExpenseHistoryItemViewHolder (
    private val binding: CardHistoryExpenseBinding
) : RecyclerView.ViewHolder(binding.root) {

    @RequiresApi(Build.VERSION_CODES.O)
    fun bindItem(
        expenseHistory: ExpenseHistoryWithExpenseSubGroupAndWallet,
        expenseGroup: ExpenseGroup?
    ) {
        binding.balanceTextView.text = expenseHistory.expenseHistory.amount.toString()

        binding.expenseGroup.text = expenseGroup?.name.toString()

        binding.expenseSubGroupNameTextView.text = expenseHistory.expenseSubGroup.name

        binding.dateTextView.text = expenseHistory.expenseHistory.date?.format(dateTimeFormatter)
    }
}
