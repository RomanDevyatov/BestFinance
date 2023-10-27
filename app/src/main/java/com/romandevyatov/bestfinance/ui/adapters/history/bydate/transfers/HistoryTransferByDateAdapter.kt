package com.romandevyatov.bestfinance.ui.adapters.history.bydate.transfers

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.CardHistoryByIdBinding
import com.romandevyatov.bestfinance.ui.adapters.history.bydate.transfers.models.TransferHistoryItem

class HistoryTransferByDateAdapter(
    private val listener: TransferAdapter.OnHistoryItemListener? = null
) : RecyclerView.Adapter<HistoryTransferByDateAdapter.TransferHistoryByDateViewHolder>() {

    private val differentCallback = object: DiffUtil.ItemCallback<TransferHistoryItem>() {
        override fun areItemsTheSame(oldItem: TransferHistoryItem, newItem: TransferHistoryItem): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: TransferHistoryItem, newItem: TransferHistoryItem): Boolean {
            return oldItem == newItem
        }
    }

    private val differIncomeHistory = AsyncListDiffer(this, differentCallback)

    inner class TransferHistoryByDateViewHolder(
        val binding: CardHistoryByIdBinding,
        val listener: TransferAdapter.OnHistoryItemListener?
    ) : RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(transferHistoryItem: TransferHistoryItem) {
            binding.dateTextView.text = transferHistoryItem.date

            val transferAdapter = TransferAdapter(transferHistoryItem.transfers, listener)
            binding.historiesByDateRecyclerView.layoutManager = LinearLayoutManager(binding.root.context)
            binding.historiesByDateRecyclerView.adapter = transferAdapter
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransferHistoryByDateViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = CardHistoryByIdBinding.inflate(from, parent, false)
        return TransferHistoryByDateViewHolder(binding, listener)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TransferHistoryByDateViewHolder, position: Int) {
        holder.bind(differIncomeHistory.currentList[position])
    }

    override fun getItemCount(): Int {
        return differIncomeHistory.currentList.size
    }

    fun submitList(list: List<TransferHistoryItem>) {
        differIncomeHistory.submitList(list)
    }
}