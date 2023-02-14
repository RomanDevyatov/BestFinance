package com.romandevyatov.bestfinance.ui.adapters.income

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.IncomeGroupCardBinding
import com.romandevyatov.bestfinance.db.entities.IncomeGroup
import com.romandevyatov.bestfinance.ui.adapters.viewholders.IncomeGroupItemViewHolder


class IncomeGroupAdapter(
    private val onClickListener: DeleteItemClickListener<IncomeGroup>
) : RecyclerView.Adapter<IncomeGroupItemViewHolder>() {

    private val differentCallback = object: DiffUtil.ItemCallback<IncomeGroup>() {
        override fun areItemsTheSame(oldItem: IncomeGroup, newItem: IncomeGroup): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: IncomeGroup, newItem: IncomeGroup): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, differentCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomeGroupItemViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = IncomeGroupCardBinding.inflate(from, parent, false)
        return IncomeGroupItemViewHolder(binding, onClickListener)
    }

    override fun onBindViewHolder(holder: IncomeGroupItemViewHolder, position: Int) {
        holder.bindItem(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<IncomeGroup>) {
        differ.submitList(list)
    }
}