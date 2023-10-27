package com.romandevyatov.bestfinance.ui.adapters.analyze

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.CardExpandableChildBinding
import com.romandevyatov.bestfinance.ui.adapters.analyze.models.ChildData
import com.romandevyatov.bestfinance.utils.Constants

class NestedChildAdapter(private val mList: List<ChildData>) :
    RecyclerView.Adapter<NestedChildAdapter.NestedViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NestedViewHolder {
        val binding = CardExpandableChildBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NestedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NestedViewHolder, position: Int) {
        val currentItem = mList[position]

        when (currentItem.type) {
            Constants.INCOMINGS_PARENT_TYPE -> {
                holder.bindIncoming(currentItem)
            }

            Constants.EXPENSES_PARENT_TYPE -> {
                holder.bindExpense(currentItem)
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class NestedViewHolder(private val binding: CardExpandableChildBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindIncoming(item: ChildData) {
            binding.expandableChildTextview.text =
                item.incomeSubGroupWithIncomeHistories?.incomeSubGroup?.name

            val sumOfSubGroup =
                item.incomeSubGroupWithIncomeHistories?.incomeHistories?.sumOf { it.amount }
            binding.categorySummaOfExpandableChild.text = sumOfSubGroup.toString()
        }

        fun bindExpense(item: ChildData) {
            binding.expandableChildTextview.text =
                item.expenseSubGroupIncludingExpenseHistories?.expenseSubGroup?.name

            val sumOfSubGroup =
                item.expenseSubGroupIncludingExpenseHistories?.expenseHistory?.sumOf { it.amount }
            binding.categorySummaOfExpandableChild.text = sumOfSubGroup.toString()
        }
    }
}
