package com.romandevyatov.bestfinance.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.ItemRowParentBinding
import com.romandevyatov.bestfinance.db.entities.relations.ExpenseGroupWithExpenseSubGroup

class ParentExpenseGroupAdapter() : RecyclerView.Adapter<ParentExpenseGroupAdapter.ExpenseGroupItemViewHolder>() {

    private val differentCallback = object: DiffUtil.ItemCallback<ExpenseGroupWithExpenseSubGroup>() {

        override fun areItemsTheSame(oldItem: ExpenseGroupWithExpenseSubGroup, newItem: ExpenseGroupWithExpenseSubGroup): Boolean {
            return oldItem.expenseGroup == newItem.expenseGroup
        }

        override fun areContentsTheSame(oldItem: ExpenseGroupWithExpenseSubGroup, newItem: ExpenseGroupWithExpenseSubGroup): Boolean {
            return oldItem == newItem
        }
    }

    private val expenseGroupWithExpenseSubGroupDiffer = AsyncListDiffer(this, differentCallback)

    private val viewPool = RecyclerView.RecycledViewPool()


    inner class ExpenseGroupItemViewHolder(
        private val binding: ItemRowParentBinding
        ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(expenseGroupWithExpenseSubGroup: ExpenseGroupWithExpenseSubGroup) {
            binding.groupNameTextView.text = expenseGroupWithExpenseSubGroup.expenseGroup.name

            val childAdapter = ChildExpenseSubGroupsAdapter(expenseGroupWithExpenseSubGroup.expenseSubGroups)
            val lm = LinearLayoutManager(binding.childRecyclerView.context, LinearLayoutManager.VERTICAL, false)
            lm.initialPrefetchItemCount = expenseGroupWithExpenseSubGroup.expenseSubGroups.size

            binding.childRecyclerView.layoutManager = lm
            binding.childRecyclerView.adapter = childAdapter
            binding.childRecyclerView.setRecycledViewPool(viewPool)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentExpenseGroupAdapter.ExpenseGroupItemViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = ItemRowParentBinding.inflate(from, parent, false)
        return ExpenseGroupItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ParentExpenseGroupAdapter.ExpenseGroupItemViewHolder, position: Int) {
        holder.bind(expenseGroupWithExpenseSubGroupDiffer.currentList[position])
    }

    override fun getItemCount(): Int {
        return expenseGroupWithExpenseSubGroupDiffer.currentList.size
    }

    fun submitList(expenseSubGroups: List<ExpenseGroupWithExpenseSubGroup>) {
        expenseGroupWithExpenseSubGroupDiffer.submitList(expenseSubGroups)
    }
}