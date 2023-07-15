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
import java.time.format.DateTimeFormatter

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
            binding.transferHistoryTextView.text = StringBuilder()
                                                        .append(transferHistory.fromWalletId.toString())
                                                        .append(" -> ")
                                                        .append(transferHistory.toWalletId.toString())

            binding.balanceTextView.text = transferHistory.amount.toString()

            val iso8601DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
            binding.dateTransferTextView.text = transferHistory.createdDate?.format(iso8601DateTimeFormatter)
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
