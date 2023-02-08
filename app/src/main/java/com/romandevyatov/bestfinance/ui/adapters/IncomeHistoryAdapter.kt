package com.romandevyatov.bestfinance.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.IncomeHistoryCardBinding
import com.romandevyatov.bestfinance.db.entities.IncomeHistory
import com.romandevyatov.bestfinance.ui.adapters.viewholders.IncomeHistoryItemViewHolder

class IncomeHistoryAdapter : RecyclerView.Adapter<IncomeHistoryItemViewHolder>() {

    private val differentCallback = object: DiffUtil.ItemCallback<IncomeHistory>() {
        override fun areItemsTheSame(oldItem: IncomeHistory, newItem: IncomeHistory): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: IncomeHistory, newItem: IncomeHistory): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, differentCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomeHistoryItemViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = IncomeHistoryCardBinding.inflate(from, parent, false)
        return IncomeHistoryItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IncomeHistoryItemViewHolder, position: Int) {
        holder.bindItem(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<IncomeHistory>) {
        differ.submitList(list)
    }
}