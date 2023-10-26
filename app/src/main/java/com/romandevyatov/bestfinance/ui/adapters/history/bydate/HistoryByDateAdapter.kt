package com.romandevyatov.bestfinance.ui.adapters.history.bydate

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.CardHistoryByIdBinding
import com.romandevyatov.bestfinance.ui.adapters.history.bydate.model.IncomeHistoryItem

class HistoryByDateAdapter(
    private val listener: TransactionAdapter.OnHistoryItemListener? = null
) : RecyclerView.Adapter<HistoryByDateAdapter.IncomeHistoryByDateViewHolder>() {

    private val differentCallback = object: DiffUtil.ItemCallback<IncomeHistoryItem>() {
        override fun areItemsTheSame(oldItem: IncomeHistoryItem, newItem: IncomeHistoryItem): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: IncomeHistoryItem, newItem: IncomeHistoryItem): Boolean {
            return oldItem == newItem
        }
    }

    private val differIncomeHistory = AsyncListDiffer(this, differentCallback)

    inner class IncomeHistoryByDateViewHolder(
        val binding: CardHistoryByIdBinding,
        val listener: TransactionAdapter.OnHistoryItemListener?
    ) : RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(groupWithSubGroupsItem: IncomeHistoryItem) {
            binding.dateTextView.text = groupWithSubGroupsItem.date

            val transactionAdapter = TransactionAdapter(groupWithSubGroupsItem.transactions, listener)
            binding.historiesByDateRecyclerView.layoutManager = LinearLayoutManager(binding.root.context)
            binding.historiesByDateRecyclerView.adapter = transactionAdapter
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomeHistoryByDateViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = CardHistoryByIdBinding.inflate(from, parent, false)
        return IncomeHistoryByDateViewHolder(binding, listener)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: IncomeHistoryByDateViewHolder, position: Int) {
        holder.bind(differIncomeHistory.currentList[position])
    }

    override fun getItemCount(): Int {
        return differIncomeHistory.currentList.size
    }

    fun submitList(list: List<IncomeHistoryItem>) {
        differIncomeHistory.submitList(list)
    }
}
