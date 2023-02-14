package com.romandevyatov.bestfinance.ui.adapters.expense


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.ItemRowParentBinding
import com.romandevyatov.bestfinance.db.entities.relations.ExpenseGroupWithExpenseSubGroupsIncludingExpenseHistories


class ParentExpenseGroupAdapter() : RecyclerView.Adapter<ParentExpenseGroupAdapter.ExpenseGroupItemViewHolder>() {

    private val differentCallback = object: DiffUtil.ItemCallback<ExpenseGroupWithExpenseSubGroupsIncludingExpenseHistories>() {

        override fun areItemsTheSame(oldItem: ExpenseGroupWithExpenseSubGroupsIncludingExpenseHistories, newItem: ExpenseGroupWithExpenseSubGroupsIncludingExpenseHistories): Boolean {
            return oldItem.expenseGroup == newItem.expenseGroup
        }

        override fun areContentsTheSame(oldItem: ExpenseGroupWithExpenseSubGroupsIncludingExpenseHistories, newItem: ExpenseGroupWithExpenseSubGroupsIncludingExpenseHistories): Boolean {
            return oldItem == newItem
        }
    }

    private val expenseGroupWithExpenseSubGroupsWithExpenseHistoriesDiffer = AsyncListDiffer(this, differentCallback)

    private val viewPool = RecyclerView.RecycledViewPool()

    inner class ExpenseGroupItemViewHolder(
        private val binding: ItemRowParentBinding
        ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(expenseGroupWithExpenseSubGroupsIncludingExpenseHistories: ExpenseGroupWithExpenseSubGroupsIncludingExpenseHistories) {
            binding.groupNameTextView.text = expenseGroupWithExpenseSubGroupsIncludingExpenseHistories.expenseGroup.name

            val childAdapter = ChildExpenseSubGroupsAdapter(expenseGroupWithExpenseSubGroupsIncludingExpenseHistories.expenseSubGroupWithExpenseHistories)
            val lm = LinearLayoutManager(binding.childRecyclerView.context, LinearLayoutManager.VERTICAL, false)
            lm.initialPrefetchItemCount = expenseGroupWithExpenseSubGroupsIncludingExpenseHistories.expenseSubGroupWithExpenseHistories.size

            binding.childRecyclerView.layoutManager = lm
            binding.childRecyclerView.adapter = childAdapter
            binding.childRecyclerView.setRecycledViewPool(viewPool)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseGroupItemViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = ItemRowParentBinding.inflate(from, parent, false)
        return ExpenseGroupItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseGroupItemViewHolder, position: Int) {
        holder.bind(expenseGroupWithExpenseSubGroupsWithExpenseHistoriesDiffer.currentList[position])
    }

    override fun getItemCount(): Int {
        return expenseGroupWithExpenseSubGroupsWithExpenseHistoriesDiffer.currentList.size
    }

    fun submitList(expenseSubGroups: List<ExpenseGroupWithExpenseSubGroupsIncludingExpenseHistories>) {
        expenseGroupWithExpenseSubGroupsWithExpenseHistoriesDiffer.submitList(expenseSubGroups)
    }
}