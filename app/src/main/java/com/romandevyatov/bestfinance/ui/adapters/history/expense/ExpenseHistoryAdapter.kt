package com.romandevyatov.bestfinance.ui.adapters.history.expense

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.CardItemHistoryTransactionBinding
import com.romandevyatov.bestfinance.data.entities.ExpenseGroup
import com.romandevyatov.bestfinance.data.entities.relations.ExpenseHistoryWithExpenseSubGroupAndWallet

class ExpenseHistoryAdapter(
    private val expenseGroupMap: Map<Long?, ExpenseGroup>,
    private val listener: ItemClickListener? = null
    ) : RecyclerView.Adapter<ExpenseHistoryItemViewHolder>() {

    interface ItemClickListener {

        fun navigate(id: Long)
    }

    private val differentCallback = object: DiffUtil.ItemCallback<ExpenseHistoryWithExpenseSubGroupAndWallet>() {
        override fun areItemsTheSame(oldItem: ExpenseHistoryWithExpenseSubGroupAndWallet, newItem: ExpenseHistoryWithExpenseSubGroupAndWallet): Boolean {
            return oldItem.expenseHistory.id == newItem.expenseHistory.id
        }

        override fun areContentsTheSame(oldItem: ExpenseHistoryWithExpenseSubGroupAndWallet, newItem: ExpenseHistoryWithExpenseSubGroupAndWallet): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, differentCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseHistoryItemViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = CardItemHistoryTransactionBinding.inflate(from, parent, false)
        return ExpenseHistoryItemViewHolder(binding, listener)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ExpenseHistoryItemViewHolder, position: Int) {
        val currentHistoryElement = differ.currentList[position]
        val currentGroup = expenseGroupMap[currentHistoryElement.expenseSubGroup?.expenseGroupId]
        holder.bindItem(differ.currentList[position], currentGroup)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<ExpenseHistoryWithExpenseSubGroupAndWallet>) {
        differ.submitList(list)
    }
}
