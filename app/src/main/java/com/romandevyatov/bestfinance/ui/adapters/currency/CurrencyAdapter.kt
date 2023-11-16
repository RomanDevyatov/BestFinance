package com.romandevyatov.bestfinance.ui.adapters.currency

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.CardItemCurrencyBinding

class CurrencyAdapter(
    private val listener: ItemClickListener
) : RecyclerView.Adapter<CurrencyAdapter.CurrencyItemViewHolder>() {

    private val differentCallback = object: DiffUtil.ItemCallback<CurrencyItem>() {

        override fun areItemsTheSame(oldItem: CurrencyItem, newItem: CurrencyItem): Boolean {
            return oldItem.code == newItem.code
        }

        override fun areContentsTheSame(oldItem: CurrencyItem, newItem: CurrencyItem): Boolean {
            return oldItem == newItem
        }
    }

    private val currencyItemAsyncListDiffer = AsyncListDiffer(this, differentCallback)

    interface ItemClickListener {

        fun onClick(currencyItem: CurrencyItem)
    }

    inner class CurrencyItemViewHolder(
        private val binding: CardItemCurrencyBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(currencyItem: CurrencyItem) {
            binding.currencyNameTextView.text = currencyItem.name
            binding.currencyCodeTextView.text = currencyItem.code

            binding.root.setOnClickListener {
                listener.onClick(currencyItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyItemViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = CardItemCurrencyBinding.inflate(from, parent, false)
        return CurrencyItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CurrencyItemViewHolder, position: Int) {
        holder.bind(currencyItemAsyncListDiffer.currentList[position])
    }

    override fun getItemCount(): Int {
        return currencyItemAsyncListDiffer.currentList.size
    }

    fun submitList(currencyItems: MutableList<CurrencyItem>) {
        currencyItemAsyncListDiffer.submitList(currencyItems)
    }
}
