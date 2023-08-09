package com.romandevyatov.bestfinance.ui.adapters.history.income

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.CardHistoryIncomeBinding
import com.romandevyatov.bestfinance.data.entities.IncomeGroup
import com.romandevyatov.bestfinance.data.entities.relations.IncomeHistoryWithIncomeSubGroupAndWallet
import com.romandevyatov.bestfinance.data.roomdb.converters.LocalDateTimeRoomTypeConverter.Companion.dateTimeFormatter
import com.romandevyatov.bestfinance.ui.adapters.menu.wallet.WalletAdapter

class IncomeHistoryItemViewHolder(
    private val binding: CardHistoryIncomeBinding,
    private val listener: IncomeHistoryAdapter.ItemClickListener? = null
) : RecyclerView.ViewHolder(binding.root) {

    @RequiresApi(Build.VERSION_CODES.O)
    fun bindItem(
        incomeHistory: IncomeHistoryWithIncomeSubGroupAndWallet,
        incomeGroup: IncomeGroup?
    ) {
        binding.balanceTextView.text = incomeHistory.incomeHistory.amount.toString()

        binding.incomeGroupNameTextView.text = incomeGroup?.name

        binding.subGroupNameTextView.text = incomeHistory.incomeSubGroup.name

        binding.dateIncomeTextView.text = incomeHistory.incomeHistory.date?.format(dateTimeFormatter)

        binding.root.setOnClickListener {
            listener?.navigate(incomeHistory.incomeHistory.id!!)
        }
    }
}
