package com.romandevyatov.bestfinance.ui.adapters.history.transfer

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.data.entities.TransferHistory
import com.romandevyatov.bestfinance.data.roomdb.converters.LocalDateTimeRoomTypeConverter
import com.romandevyatov.bestfinance.databinding.CardHistoryTransferBinding

class TransferItemViewHolder(
    private val binding: CardHistoryTransferBinding
) : RecyclerView.ViewHolder(binding.root) {

    @RequiresApi(Build.VERSION_CODES.O)
    fun bind(transferHistory: TransferHistory) {
        binding.fromNameTextView.text = transferHistory.fromWalletId.toString()

        binding.toNameTextView.text = transferHistory.toWalletId.toString()

        binding.balanceTextView.text = transferHistory.amount.toString()

        binding.dateIncomeTextView.text = transferHistory.createdDate?.format(
            LocalDateTimeRoomTypeConverter.dateTimeFormatter
        )
    }
}
