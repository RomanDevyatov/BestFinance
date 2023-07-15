package com.romandevyatov.bestfinance.ui.adapters.history

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.CardHistoryExpenseBinding
import com.romandevyatov.bestfinance.db.entities.relations.ExpenseHistoryWithExpenseSubGroupAndWallet
import com.romandevyatov.bestfinance.ui.adapters.viewholders.ExpenseHistoryItemViewHolder

class ExpenseHistoryAdapter : RecyclerView.Adapter<ExpenseHistoryItemViewHolder>() {

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
        val binding = CardHistoryExpenseBinding.inflate(from, parent, false)
        return ExpenseHistoryItemViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ExpenseHistoryItemViewHolder, position: Int) {
        holder.bindItem(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<ExpenseHistoryWithExpenseSubGroupAndWallet>) {
        differ.submitList(list)
    }
}