package com.romandevyatov.bestfinance.ui.adapters.history.bydate.transactions

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.CardItemHistoryTransactionBinding
import com.romandevyatov.bestfinance.ui.adapters.history.bydate.transactions.model.TransactionItem

class TransactionAdapter(
    private val transactionList: List<TransactionItem>,
    val onTransactionClickListener: OnHistoryItemListener? = null,
    val imageId: Int? = null
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    interface OnHistoryItemListener {
        fun navigateToUpdateTransaction(id: Long)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = CardItemHistoryTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(transactionList[position])
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }

    inner class TransactionViewHolder(
        private val binding: CardItemHistoryTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(transactionItem: TransactionItem) {

            binding.amountTextView.text = transactionItem.amount

            binding.incomeGroupNameTextView.text = transactionItem.groupName

            binding.subGroupNameTextView.text = transactionItem.subGroupGroupName

            binding.walletNameTextView.text = transactionItem.walletName

            binding.dateIncomeTextView.text = transactionItem.date?.toLocalTime().toString()

            if (imageId != null) {
                binding.imageView.setImageResource(imageId)
            }

            binding.root.setOnClickListener {
                if (transactionItem.id != null) {
                    onTransactionClickListener?.navigateToUpdateTransaction(transactionItem.id)
                }
            }
        }
    }
}
