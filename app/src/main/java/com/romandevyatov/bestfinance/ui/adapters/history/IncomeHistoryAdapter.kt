package com.romandevyatov.bestfinance.ui.adapters.history

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.IncomeHistoryCardBinding
import com.romandevyatov.bestfinance.db.entities.relations.IncomeHistoryWithIncomeGroupAndWallet
import com.romandevyatov.bestfinance.ui.adapters.viewholders.IncomeHistoryItemViewHolder


class IncomeHistoryAdapter : RecyclerView.Adapter<IncomeHistoryItemViewHolder>() {

    private val differentCallback = object: DiffUtil.ItemCallback<IncomeHistoryWithIncomeGroupAndWallet>() {
        override fun areItemsTheSame(oldItem: IncomeHistoryWithIncomeGroupAndWallet, newItem: IncomeHistoryWithIncomeGroupAndWallet): Boolean {
            return oldItem.incomeHistory.id == newItem.incomeHistory.id
        }

        override fun areContentsTheSame(oldItem: IncomeHistoryWithIncomeGroupAndWallet, newItem: IncomeHistoryWithIncomeGroupAndWallet): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, differentCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomeHistoryItemViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = IncomeHistoryCardBinding.inflate(from, parent, false)
        return IncomeHistoryItemViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: IncomeHistoryItemViewHolder, position: Int) {
        holder.bindItem(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<IncomeHistoryWithIncomeGroupAndWallet>) {
        differ.submitList(list)
    }
}