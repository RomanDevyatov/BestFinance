package com.romandevyatov.bestfinance.ui.adapters.history

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.CardHistoryTransferBinding
import com.romandevyatov.bestfinance.db.entities.TransferHistory
import com.romandevyatov.bestfinance.db.roomdb.converters.LocalDateTimeRoomTypeConverter.Companion.dateTimeFormatter

class TransferHistoryAdapter : RecyclerView.Adapter<TransferHistoryAdapter.TransferItemViewHolder>() {

    private val differentCallback = object: DiffUtil.ItemCallback<TransferHistory>() {

        override fun areItemsTheSame(oldItem: TransferHistory, newItem: TransferHistory): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TransferHistory, newItem: TransferHistory): Boolean {
            return oldItem == newItem
        }
    }

    val transferDiffer = AsyncListDiffer(this, differentCallback)

    inner class TransferItemViewHolder(
        private val binding: CardHistoryTransferBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(transferHistory: TransferHistory) {
            binding.fromNameTextView.text = transferHistory.fromWalletId.toString()

            binding.toNameTextView.text = transferHistory.toWalletId.toString()

            binding.balanceTextView.text = transferHistory.amount.toString()

            binding.dateIncomeTextView.text = transferHistory.createdDate?.format(dateTimeFormatter)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransferItemViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = CardHistoryTransferBinding.inflate(from, parent, false)
        return TransferItemViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TransferItemViewHolder, position: Int) {
        holder.bind(transferDiffer.currentList[position])
    }

    override fun getItemCount(): Int {
        return transferDiffer.currentList.size
    }

    fun submitList(transferHistories: List<TransferHistory>) {
        transferDiffer.submitList(transferHistories)
    }
}
