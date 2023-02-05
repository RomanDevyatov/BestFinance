package com.romandevyatov.bestfinance.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.WalletCardBinding
import com.romandevyatov.bestfinance.db.entities.IncomeGroup
import com.romandevyatov.bestfinance.db.entities.IncomeHistory
import com.romandevyatov.bestfinance.db.entities.Wallet


class WalletAdapter(
    private val onClickListener: ItemClickListener<Wallet>
) : RecyclerView.Adapter<WalletAdapter.WalletItemViewHolder>() {

    private val differentCallback = object: DiffUtil.ItemCallback<Wallet>() {

        override fun areItemsTheSame(oldItem: Wallet, newItem: Wallet): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Wallet, newItem: Wallet): Boolean {
            return oldItem == newItem
        }
    }

    private val walletDiffer = AsyncListDiffer(this, differentCallback)


    inner class WalletItemViewHolder(
        private val binding: WalletCardBinding,
        private val clickListener: ItemClickListener<Wallet>
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(wallet: Wallet) {
            binding.walletNameTextView.text = wallet.name
            binding.balanceTextView.text = wallet.balance.toString()

            binding.removeIncomeGroupIcon.setOnClickListener{
                clickListener.deleteItem(wallet)
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletItemViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = WalletCardBinding.inflate(from, parent, false)
        return WalletItemViewHolder(binding, onClickListener)
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
