package com.romandevyatov.bestfinance.ui.adapters.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.TransferHistoryCardBinding
import com.romandevyatov.bestfinance.db.entities.TransferHistory


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
        private val binding: TransferHistoryCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(transferHistory: TransferHistory) {
            binding.transferHistoryTextView.text = StringBuilder()
                                                        .append(transferHistory.fromWalletId.toString())
                                                        .append(" -> ")
                                                        .append(transferHistory.toWalletId.toString())

            binding.balanceTextView.text = transferHistory.balance.toString()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransferItemViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = TransferHistoryCardBinding.inflate(from, parent, false)
        return TransferItemViewHolder(binding)
    }

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
