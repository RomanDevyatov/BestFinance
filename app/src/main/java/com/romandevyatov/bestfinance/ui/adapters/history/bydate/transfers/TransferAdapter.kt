package com.romandevyatov.bestfinance.ui.adapters.history.bydate.transfers

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.CardItemHistoryTransferBinding
import com.romandevyatov.bestfinance.ui.adapters.history.bydate.transfers.models.TransferItem

class TransferAdapter(
    private val transactionList: List<TransferItem>,
    val onTransactionClickListener: OnHistoryItemListener? = null
) : RecyclerView.Adapter<TransferAdapter.TransferViewHolder>() {

    interface OnHistoryItemListener {
        fun navigateToUpdateTransfer(id: Long)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransferViewHolder {
        val binding = CardItemHistoryTransferBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransferViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TransferViewHolder, position: Int) {
        holder.bind(transactionList[position])
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }

    inner class TransferViewHolder(
        private val binding: CardItemHistoryTransferBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(transactionItem: TransferItem) {
            binding.amountTextView.text = transactionItem.amount

            binding.amountBaseTextView.text = transactionItem.amountBase

            binding.fromNameTextView.text = transactionItem.fromName

            binding.toNameTextView.text = transactionItem.toName

            binding.dateIncomeTextView.text = transactionItem.date?.toLocalTime().toString()

            binding.root.setOnClickListener {
                if (transactionItem.id != null) {
                    onTransactionClickListener?.navigateToUpdateTransfer(transactionItem.id)
                }
            }
        }
    }
}
