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
    incomeSubGroupWithIncomeHistoriesList: List<IncomeSubGroupWithIncomeHistories>
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

    val notArchivedIncomeSubGroupWithIncomeHistoriesDiffer = AsyncListDiffer(this, differentCallback)

    init {
        this.notArchivedIncomeSubGroupWithIncomeHistoriesDiffer.submitList(incomeSubGroupWithIncomeHistoriesList)
    }

    inner class IncomeSubGroupItemViewHolder(
        private val itemRowChildBinding: ItemRowChildBinding
    ) : RecyclerView.ViewHolder(itemRowChildBinding.root) {

        fun bindItem(incomeSubGroup: IncomeSubGroupWithIncomeHistories) {
            itemRowChildBinding.subGroupNameTextView.text = incomeSubGroup.incomeSubGroup.name
            itemRowChildBinding.summaTextView.text =  incomeSubGroup.incomeHistories.sumOf { it.amount }.toString()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomeSubGroupItemViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = ItemRowChildBinding.inflate(from, parent, false)
        return IncomeSubGroupItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IncomeSubGroupItemViewHolder, position: Int) {
        holder.bindItem(notArchivedIncomeSubGroupWithIncomeHistoriesDiffer.currentList[position])
    }

    override fun getItemCount(): Int {
        return notArchivedIncomeSubGroupWithIncomeHistoriesDiffer.currentList.size
    }

    fun submitList(incomeSubGroupWithIncomeHistories: List<IncomeSubGroupWithIncomeHistories>) {
        notArchivedIncomeSubGroupWithIncomeHistoriesDiffer.submitList(incomeSubGroupWithIncomeHistories)
    }

}
