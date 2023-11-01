package com.romandevyatov.bestfinance.ui.adapters.history.bydate.transactions

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.CardHistoryByIdBinding
import com.romandevyatov.bestfinance.ui.adapters.history.bydate.transactions.model.TransactionHistoryItem
import com.romandevyatov.bestfinance.utils.Constants

class HistoryTransactionByDateAdapter(
    private val imageId: Int,
    private val listener: TransactionAdapter.OnHistoryItemListener? = null
) : RecyclerView.Adapter<HistoryTransactionByDateAdapter.TransactionHistoryByDateViewHolder>() {

    private val differentCallback = object: DiffUtil.ItemCallback<TransactionHistoryItem>() {
        override fun areItemsTheSame(oldItem: TransactionHistoryItem, newItem: TransactionHistoryItem): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: TransactionHistoryItem, newItem: TransactionHistoryItem): Boolean {
            return oldItem == newItem
        }
    }

    private val differIncomeHistory = AsyncListDiffer(this, differentCallback)

    inner class TransactionHistoryByDateViewHolder(
        val binding: CardHistoryByIdBinding,
        val listener: TransactionAdapter.OnHistoryItemListener?
    ) : RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(groupWithSubGroupsItem: TransactionHistoryItem) {
            binding.dateTextView.text = groupWithSubGroupsItem.date

            val transactionAdapter = TransactionAdapter(groupWithSubGroupsItem.transactions, listener, imageId)
            binding.historiesByDateRecyclerView.layoutManager = LinearLayoutManager(binding.root.context)
            binding.historiesByDateRecyclerView.adapter = transactionAdapter
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionHistoryByDateViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = CardHistoryByIdBinding.inflate(from, parent, false)
        return TransactionHistoryByDateViewHolder(binding, listener)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TransactionHistoryByDateViewHolder, position: Int) {
        holder.bind(differIncomeHistory.currentList[position])
    }

    override fun getItemCount(): Int {
        return differIncomeHistory.currentList.size
    }

    fun submitList(list: List<TransactionHistoryItem>) {
        differIncomeHistory.submitList(list)
    }
}
