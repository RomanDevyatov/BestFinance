package com.romandevyatov.bestfinance.ui.adapters.menu.expense;

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.ItemRowChildBinding
import com.romandevyatov.bestfinance.db.entities.relations.ExpenseSubGroupWithExpenseHistories


class ChildExpenseSubGroupsAdapter(
        var expenseSubGroupWithExpenseHistories: List<ExpenseSubGroupWithExpenseHistories>
) : RecyclerView.Adapter<ChildExpenseSubGroupsAdapter.ExpenseSubGroupItemViewHolder>() {

        private val differentCallback = object: DiffUtil.ItemCallback<ExpenseSubGroupWithExpenseHistories>() {
                override fun areItemsTheSame(oldItem: ExpenseSubGroupWithExpenseHistories, newItem: ExpenseSubGroupWithExpenseHistories): Boolean {
                        return oldItem.expenseSubGroup == newItem.expenseSubGroup
                }

                override fun areContentsTheSame(oldItem: ExpenseSubGroupWithExpenseHistories, newItem: ExpenseSubGroupWithExpenseHistories): Boolean {
                        return oldItem == newItem
                }
        }


        private val expenseSubGroupWithExpenseHistoriesDiffer = AsyncListDiffer(this, differentCallback)

        init {
                this.expenseSubGroupWithExpenseHistoriesDiffer.submitList(expenseSubGroupWithExpenseHistories)
        }

        inner class ExpenseSubGroupItemViewHolder(
                private val itemRowChildBinding: ItemRowChildBinding
                ) : RecyclerView.ViewHolder(itemRowChildBinding.root) {

                fun bindItem(expenseSubGroup: ExpenseSubGroupWithExpenseHistories) {
                        itemRowChildBinding.subGroupNameTextView.text = expenseSubGroup.expenseSubGroup.name
                        itemRowChildBinding.summaTextView.text =  expenseSubGroup.expenseHistory.sumOf { it.amount }.toString()
                }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseSubGroupItemViewHolder {
                val from = LayoutInflater.from(parent.context)
                val binding = ItemRowChildBinding.inflate(from, parent, false)
                return ExpenseSubGroupItemViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ExpenseSubGroupItemViewHolder, position: Int) {
                holder.bindItem(expenseSubGroupWithExpenseHistoriesDiffer.currentList[position])
        }

        override fun getItemCount(): Int {
                return expenseSubGroupWithExpenseHistoriesDiffer.currentList.size
        }

        fun submitList(expenseSubGroupWithExpenseHistories: List<ExpenseSubGroupWithExpenseHistories>) {
                expenseSubGroupWithExpenseHistoriesDiffer.submitList(expenseSubGroupWithExpenseHistories)
        }
}
