package com.romandevyatov.bestfinance.ui.adapters.viewholders

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.CardHistoryIncomeBinding
import com.romandevyatov.bestfinance.db.entities.relations.IncomeHistoryWithIncomeSubGroupAndWallet
import java.time.format.DateTimeFormatter

class IncomeHistoryItemViewHolder(
    private val binding: CardHistoryIncomeBinding
) : RecyclerView.ViewHolder(binding.root) {

    @RequiresApi(Build.VERSION_CODES.O)
    fun bindItem(incomeHistory: IncomeHistoryWithIncomeSubGroupAndWallet) {
        binding.balanceTextView.text = incomeHistory.incomeHistory.amount.toString()
        binding.incomeGroupNameOfIncomeHistoryTextView.text = incomeHistory.incomeSubGroup.name
//        val dateFormat = "yyyy-MM-dd HH:mm:ss"
        val iso8601DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        binding.dateIncomeTextView.text = incomeHistory.incomeHistory.createdDate?.format(iso8601DateTimeFormatter)


    }
}
