package com.romandevyatov.bestfinance.ui.adapters.rates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.CardItemCurrencyRateBinding

class CurrencyExchangeListAdapter : RecyclerView.Adapter<CurrencyExchangeListAdapter.CurrencyRateViewHolder>() {

    private val differentCallback = object: DiffUtil.ItemCallback<CurrencyExchangeRateItem>() {

        override fun areItemsTheSame(oldItem: CurrencyExchangeRateItem, newItem: CurrencyExchangeRateItem): Boolean {
            return oldItem.currencyCode == newItem.currencyCode
        }

        override fun areContentsTheSame(oldItem: CurrencyExchangeRateItem, newItem: CurrencyExchangeRateItem): Boolean {
            return oldItem == newItem
        }
    }

    val walletDiffer = AsyncListDiffer(this, differentCallback)

    inner class CurrencyRateViewHolder(
        private val binding: CardItemCurrencyRateBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(currency: CurrencyExchangeRateItem) {
                binding.textCurrency.text = currency.currencyCode
                binding.textRate.text = currency.rate.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyRateViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = CardItemCurrencyRateBinding.inflate(from, parent, false)
        return CurrencyRateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CurrencyRateViewHolder, position: Int) {
        holder.bind(walletDiffer.currentList[position])
    }

    override fun getItemCount(): Int {
        return walletDiffer.currentList.size
    }

    fun submitList(currencyExchangeRateItems: MutableList<CurrencyExchangeRateItem>) {
        walletDiffer.submitList(currencyExchangeRateItems)
    }
}
