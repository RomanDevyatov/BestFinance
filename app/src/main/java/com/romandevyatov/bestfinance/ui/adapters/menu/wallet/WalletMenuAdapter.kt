package com.romandevyatov.bestfinance.ui.adapters.menu.wallet

import android.annotation.SuppressLint
import android.text.Layout
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.CardItemWalletBinding
import com.romandevyatov.bestfinance.ui.adapters.menu.wallet.model.WalletItem
import com.romandevyatov.bestfinance.utils.Constants

class WalletMenuAdapter(
    private val listener: ItemClickListener,
    private val addItemText: String? = null
) : RecyclerView.Adapter<WalletMenuAdapter.WalletItemViewHolder>() {

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
        fun navigateToAddNewWallet()
    }

    inner class WalletItemViewHolder(
        private val binding: CardItemWalletBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("RtlHardcoded")
        fun bind(wallet: WalletItem) {
            binding.walletNameTextView.text = wallet.name

            if (wallet.id == null && wallet.name == addItemText && wallet.balance == null) {
//                binding.walletNameTextView.layout.width = "ma"
                binding.walletNameTextView.gravity = Gravity.CENTER
                binding.amountTextView.text = ""
            } else {
                binding.amountTextView.text = wallet.balance.toString()
            }

            binding.root.setOnClickListener {
                if (wallet.id == null &&
                    wallet.name == addItemText &&
                    wallet.balance == null) {
                    listener.navigateToAddNewWallet()
                } else {
                    listener.navigate(wallet.name)
                }
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
