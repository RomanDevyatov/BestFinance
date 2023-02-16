package com.romandevyatov.bestfinance.ui.adapters.income

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.ItemRowChildBinding
import com.romandevyatov.bestfinance.db.entities.relations.IncomeSubGroupWithIncomeHistories

class ChildIncomeSubGroupsAdapter (
    incomeSubGroupWithIncomeHistories: List<IncomeSubGroupWithIncomeHistories>
) : RecyclerView.Adapter<ChildIncomeSubGroupsAdapter.IncomeSubGroupItemViewHolder>() {

    private val differentCallback = object: DiffUtil.ItemCallback<IncomeSubGroupWithIncomeHistories>() {

        override fun areItemsTheSame(oldItem: IncomeSubGroupWithIncomeHistories, newItem: IncomeSubGroupWithIncomeHistories): Boolean {
            return oldItem.incomeSubGroup == newItem.incomeSubGroup
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: IncomeSubGroupWithIncomeHistories, newItem: IncomeSubGroupWithIncomeHistories): Boolean {
            return oldItem == newItem
        }
    }

    private val incomeSubGroupWithIncomeHistoriesDiffer = AsyncListDiffer(this, differentCallback)

    init {
        this.incomeSubGroupWithIncomeHistoriesDiffer.submitList(incomeSubGroupWithIncomeHistories)
    }

    inner class IncomeSubGroupItemViewHolder(
        private val itemRowChildBinding: ItemRowChildBinding
    ) : RecyclerView.ViewHolder(itemRowChildBinding.root) {

        fun bindItem(incomeSubGroup: IncomeSubGroupWithIncomeHistories) {
            itemRowChildBinding.subGroupNameTextView.text = incomeSubGroup.incomeSubGroup.name
            itemRowChildBinding.summaTextView.text =  incomeSubGroup.incomeHistory.sumOf { it.amount }.toString()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomeSubGroupItemViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = ItemRowChildBinding.inflate(from, parent, false)
        return IncomeSubGroupItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IncomeSubGroupItemViewHolder, position: Int) {
        holder.bindItem(incomeSubGroupWithIncomeHistoriesDiffer.currentList[position])
    }

    override fun getItemCount(): Int {
        return incomeSubGroupWithIncomeHistoriesDiffer.currentList.size
    }

    fun submitList(incomeSubGroupWithIncomeHistories: List<IncomeSubGroupWithIncomeHistories>) {
        incomeSubGroupWithIncomeHistoriesDiffer.submitList(incomeSubGroupWithIncomeHistories)
    }

}
