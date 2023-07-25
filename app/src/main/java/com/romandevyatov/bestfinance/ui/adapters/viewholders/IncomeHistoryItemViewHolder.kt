package com.romandevyatov.bestfinance.ui.adapters.viewholders

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.CardHistoryIncomeBinding
import com.romandevyatov.bestfinance.db.entities.relations.IncomeHistoryWithIncomeSubGroupAndWallet
import com.romandevyatov.bestfinance.db.roomdb.converters.LocalDateTimeRoomTypeConverter.Companion.dateTimeFormatter

class IncomeHistoryItemViewHolder(
    private val binding: CardHistoryIncomeBinding
) : RecyclerView.ViewHolder(binding.root) {

    @RequiresApi(Build.VERSION_CODES.O)
    fun bindItem(incomeHistory: IncomeHistoryWithIncomeSubGroupAndWallet) {
        binding.balanceTextView.text = incomeHistory.incomeHistory.amount.toString()
        binding.incomeGroupNameOfIncomeHistoryTextView.text = incomeHistory.incomeSubGroup.name

        binding.dateIncomeTextView.text = incomeHistory.incomeHistory.createdDate?.format(dateTimeFormatter)
    }
}
