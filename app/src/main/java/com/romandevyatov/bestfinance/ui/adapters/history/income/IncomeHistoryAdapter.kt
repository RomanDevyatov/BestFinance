package com.romandevyatov.bestfinance.ui.adapters.history.income

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.CardHistoryIncomeBinding
import com.romandevyatov.bestfinance.data.entities.IncomeGroup
import com.romandevyatov.bestfinance.data.entities.relations.IncomeHistoryWithIncomeSubGroupAndWallet

class IncomeHistoryAdapter(private val incomeGroupsMap: Map<Long?, IncomeGroup>) : RecyclerView.Adapter<IncomeHistoryItemViewHolder>() {

    private val differentCallback = object: DiffUtil.ItemCallback<IncomeHistoryWithIncomeSubGroupAndWallet>() {
        override fun areItemsTheSame(oldItem: IncomeHistoryWithIncomeSubGroupAndWallet, newItem: IncomeHistoryWithIncomeSubGroupAndWallet): Boolean {
            return oldItem.incomeHistory.id == newItem.incomeHistory.id
        }

        override fun areContentsTheSame(oldItem: IncomeHistoryWithIncomeSubGroupAndWallet, newItem: IncomeHistoryWithIncomeSubGroupAndWallet): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, differentCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomeHistoryItemViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = CardHistoryIncomeBinding.inflate(from, parent, false)
        return IncomeHistoryItemViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: IncomeHistoryItemViewHolder, position: Int) {
        val currentHistoryElement = differ.currentList[position]
        val currentGroup = incomeGroupsMap[currentHistoryElement.incomeSubGroup.incomeGroupId]
        holder.bindItem(differ.currentList[position], currentGroup)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<IncomeHistoryWithIncomeSubGroupAndWallet>) {
        differ.submitList(list)
    }
}