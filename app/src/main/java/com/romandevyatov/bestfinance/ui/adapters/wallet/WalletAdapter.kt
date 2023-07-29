package com.romandevyatov.bestfinance.ui.adapters.wallet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.CardItemWalletBinding
import com.romandevyatov.bestfinance.data.entities.Wallet

class WalletAdapter(private val listener: ItemClickListener) : RecyclerView.Adapter<WalletAdapter.WalletItemViewHolder>() {

    private val differentCallback = object: DiffUtil.ItemCallback<Wallet>() {

        override fun areItemsTheSame(oldItem: Wallet, newItem: Wallet): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Wallet, newItem: Wallet): Boolean {
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

        fun bind(wallet: Wallet) {
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

    fun submitList(wallets: List<Wallet>) {
        walletDiffer.submitList(wallets)
    }
}
