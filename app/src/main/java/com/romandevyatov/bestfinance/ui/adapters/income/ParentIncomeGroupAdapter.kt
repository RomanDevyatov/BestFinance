package com.romandevyatov.bestfinance.ui.adapters.income


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.ItemRowParentBinding
import com.romandevyatov.bestfinance.db.entities.IncomeGroup
import com.romandevyatov.bestfinance.db.entities.relations.IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories
import com.romandevyatov.bestfinance.ui.adapters.utilities.AddItemClickListener


class ParentIncomeGroupAdapter(
    private val onClickListener: AddItemClickListener<IncomeGroup>
) : RecyclerView.Adapter<ParentIncomeGroupAdapter.IncomeGroupItemViewHolder>() {

    private val differentCallback = object: DiffUtil.ItemCallback<IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories>() {

        override fun areItemsTheSame(oldItem: IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories, newItem: IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories): Boolean {
            return oldItem.incomeGroup == newItem.incomeGroup
        }

        override fun areContentsTheSame(oldItem: IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories, newItem: IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories): Boolean {
            return oldItem == newItem
        }
    }

    private val incomeGroupWithIncomeSubGroupsWithIncomeHistoriesDiffer = AsyncListDiffer(this, differentCallback)

    private val viewPool = RecyclerView.RecycledViewPool()

    inner class IncomeGroupItemViewHolder(
        private val binding: ItemRowParentBinding,
        private val onClickListener: AddItemClickListener<IncomeGroup>
        ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(incomeGroupWithIncomeSubGroupsIncludingIncomeHistories: IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories) {
            binding.groupNameTextView.text = incomeGroupWithIncomeSubGroupsIncludingIncomeHistories.incomeGroup.name

            val childAdapter = ChildIncomeSubGroupsAdapter(incomeGroupWithIncomeSubGroupsIncludingIncomeHistories.incomeSubGroupWithIncomeHistories)
            val lm = LinearLayoutManager(binding.childRecyclerView.context, LinearLayoutManager.VERTICAL, false)
            lm.initialPrefetchItemCount = incomeGroupWithIncomeSubGroupsIncludingIncomeHistories.incomeSubGroupWithIncomeHistories.size

            binding.childRecyclerView.layoutManager = lm
            binding.childRecyclerView.adapter = childAdapter
            binding.childRecyclerView.setRecycledViewPool(viewPool)

            binding.addNewIncomeOfSelectedGroupIcon .setOnClickListener {
                onClickListener.addItem(incomeGroupWithIncomeSubGroupsIncludingIncomeHistories.incomeGroup)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomeGroupItemViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = ItemRowParentBinding.inflate(from, parent, false)
        return IncomeGroupItemViewHolder(binding, onClickListener)
    }

    override fun onBindViewHolder(holder: IncomeGroupItemViewHolder, position: Int) {
        holder.bind(incomeGroupWithIncomeSubGroupsWithIncomeHistoriesDiffer.currentList[position])
    }

    override fun getItemCount(): Int {
        return incomeGroupWithIncomeSubGroupsWithIncomeHistoriesDiffer.currentList.size
    }

    fun submitList(incomeSubGroups: List<IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories>) {
        incomeGroupWithIncomeSubGroupsWithIncomeHistoriesDiffer.submitList(incomeSubGroups)
    }
}
