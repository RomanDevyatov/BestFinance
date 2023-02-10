package com.romandevyatov.bestfinance.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.ItemRowParentBinding
import com.romandevyatov.bestfinance.db.entities.relations.ExpenseGroupWithExpenseSubGroups

class ParentExpenseGroupAdapter() : RecyclerView.Adapter<ParentExpenseGroupAdapter.ExpenseGroupItemViewHolder>() {

    private val differentCallback = object: DiffUtil.ItemCallback<ExpenseGroupWithExpenseSubGroups>() {

        override fun areItemsTheSame(oldItem: ExpenseGroupWithExpenseSubGroups, newItem: ExpenseGroupWithExpenseSubGroups): Boolean {
            return oldItem.expenseGroup == newItem.expenseGroup
        }

        override fun areContentsTheSame(oldItem: ExpenseGroupWithExpenseSubGroups, newItem: ExpenseGroupWithExpenseSubGroups): Boolean {
            return oldItem == newItem
        }
    }

    private val expenseGroupWithExpenseSubGroupDiffer = AsyncListDiffer(this, differentCallback)

    private val viewPool = RecyclerView.RecycledViewPool()


    inner class ExpenseGroupItemViewHolder(
        private val binding: ItemRowParentBinding
        ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(expenseGroupWithExpenseSubGroups: ExpenseGroupWithExpenseSubGroups) {
            binding.groupNameTextView.text = expenseGroupWithExpenseSubGroups.expenseGroup.name

            val childAdapter = ChildExpenseSubGroupsAdapter(expenseGroupWithExpenseSubGroups.expenseSubGroups)
            val lm = LinearLayoutManager(binding.childRecyclerView.context, LinearLayoutManager.VERTICAL, false)
            lm.initialPrefetchItemCount = expenseGroupWithExpenseSubGroups.expenseSubGroups.size

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

    fun submitList(expenseSubGroups: List<ExpenseGroupWithExpenseSubGroups>) {
        expenseGroupWithExpenseSubGroupDiffer.submitList(expenseSubGroups)
    }
}