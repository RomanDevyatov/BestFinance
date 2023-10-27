package com.romandevyatov.bestfinance.ui.adapters.history.transfer

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.data.entities.TransferHistory
import com.romandevyatov.bestfinance.databinding.CardItemHistoryTransferBinding

class TransferHistoryAdapter(private val listener: ItemClickListener? = null) : RecyclerView.Adapter<TransferItemViewHolder>() {

    interface ItemClickListener {

        fun navigate(id: Long)
    }

    private val differentCallback = object: DiffUtil.ItemCallback<TransferHistory>() {

        override fun areItemsTheSame(oldItem: TransferHistory, newItem: TransferHistory): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TransferHistory, newItem: TransferHistory): Boolean {
            return oldItem == newItem
        }
    }

    val transferDiffer = AsyncListDiffer(this, differentCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransferItemViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = CardItemHistoryTransferBinding.inflate(from, parent, false)
        return TransferItemViewHolder(binding, listener)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TransferItemViewHolder, position: Int) {
        holder.bind(transferDiffer.currentList[position])
    }

    override fun getItemCount(): Int {
        return transferDiffer.currentList.size
    }

    fun submitList(transferHistories: List<TransferHistory>) {
        transferDiffer.submitList(transferHistories)
    }
}
