package com.romandevyatov.bestfinance.ui.adapters;

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.ItemRowChildBinding
import com.romandevyatov.bestfinance.db.entities.ExpenseSubGroup


class ChildExpenseSubGroupsAdapter(
        var expenseSubGroups: List<ExpenseSubGroup>
) : RecyclerView.Adapter<ChildExpenseSubGroupsAdapter.ExpenseSubGroupItemViewHolder>() {

        private val differentCallback = object: DiffUtil.ItemCallback<ExpenseSubGroup>() {
                override fun areItemsTheSame(oldItem: ExpenseSubGroup, newItem: ExpenseSubGroup): Boolean {
                        return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: ExpenseSubGroup, newItem: ExpenseSubGroup): Boolean {
                        return oldItem == newItem
                }
        }


        private val expenseSubGroupDiffer = AsyncListDiffer(this, differentCallback)

        init {
                this.expenseSubGroupDiffer.submitList(expenseSubGroups)
        }

        inner class ExpenseSubGroupItemViewHolder(
                private val binding: ItemRowChildBinding
                ) : RecyclerView.ViewHolder(binding.root) {
                fun bindItem(expenseSubGroup: ExpenseSubGroup) {
                        binding.subGroupNameTextView.text = expenseSubGroup.name
                }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseSubGroupItemViewHolder {
                val from = LayoutInflater.from(parent.context)
                val binding = ItemRowChildBinding.inflate(from, parent, false)
                return ExpenseSubGroupItemViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ExpenseSubGroupItemViewHolder, position: Int) {
                holder.bindItem(expenseSubGroupDiffer.currentList[position])
        }

        override fun getItemCount(): Int {
                return expenseSubGroupDiffer.currentList.size
        }

        fun submitList(expenseSubGroups: List<ExpenseSubGroup>) {
                expenseSubGroupDiffer.submitList(expenseSubGroups)
        }
}
