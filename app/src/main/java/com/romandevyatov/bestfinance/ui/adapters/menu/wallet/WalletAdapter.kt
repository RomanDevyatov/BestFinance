package com.romandevyatov.bestfinance.ui.adapters.menu.wallet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.CardItemWalletBinding
import com.romandevyatov.bestfinance.ui.adapters.menu.wallet.model.WalletItem

class WalletAdapter(private val listener: ItemClickListener) : RecyclerView.Adapter<WalletAdapter.WalletItemViewHolder>() {

    private val differentCallback = object: DiffUtil.ItemCallback<WalletItem>() {

        override fun areItemsTheSame(oldItem: WalletItem, newItem: WalletItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: WalletItem, newItem: WalletItem): Boolean {
            return oldItem == newItem
        }
    }

    val walletDiffer = AsyncListDiffer(this, differentCallback)

    interface ItemClickListener {

        fun navigate(name: String)
    }

    inner class WalletItemViewHolder(
        private val binding: CardItemWalletBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(wallet: WalletItem) {
            binding.walletNameTextView.text = wallet.name
            binding.balanceTextView.text = wallet.balance.toString()

            binding.root.setOnClickListener {
                listener.navigate(wallet.name)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletItemViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = CardItemWalletBinding.inflate(from, parent, false)
        return WalletItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WalletItemViewHolder, position: Int) {
        holder.bind(walletDiffer.currentList[position])
    }

    override fun getItemCount(): Int {
        return walletDiffer.currentList.size
    }

    fun submitList(wallets: MutableList<WalletItem>) {
        walletDiffer.submitList(wallets)
    }
}
