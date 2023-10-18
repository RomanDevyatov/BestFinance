package com.romandevyatov.bestfinance.ui.adapters.settings.wallets

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.CardWalletItemBinding
import com.romandevyatov.bestfinance.ui.adapters.settings.wallets.models.WalletItem

class WalletsAdapter(
    private val onWalletItemCheckedChangeListener: OnWalletItemCheckedChangeListener? = null,
    private val walletItemDeleteListener: OnWalletItemDeleteListener? = null,
    private val walletItemClickedListener: OnWalletItemClickedListener? = null
) : RecyclerView.Adapter<WalletsAdapter.WalletItemViewHolder>() {

    interface OnWalletItemCheckedChangeListener {
        fun onWalletChecked(walletItem: WalletItem, isChecked: Boolean)
    }

    interface OnWalletItemDeleteListener {
        fun onWalletItemDelete(walletItem: WalletItem)
    }

    interface OnWalletItemClickedListener {
        fun navigateToUpdateWallet(wallet: WalletItem)
    }

    private val differentCallback = object: DiffUtil.ItemCallback<WalletItem>() {
        override fun areItemsTheSame(oldItem: WalletItem, newItem: WalletItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: WalletItem, newItem: WalletItem): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, differentCallback)

    fun submitList(walletItems: List<WalletItem>) {
        differ.submitList(walletItems)
    }

    fun removeItem(walletItem: WalletItem) {
        val position = differ.currentList.indexOf(walletItem)
        if (position != -1) {
            val updatedList = differ.currentList.toMutableList()
            updatedList.removeAt(position)
            differ.submitList(updatedList)
        }
    }

    inner class WalletItemViewHolder(
        private val binding: CardWalletItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindSubgroup(walletItem: WalletItem) {
            binding.walletTextView.text = walletItem.name

            binding.deleteButton.setOnClickListener {
                walletItemDeleteListener?.onWalletItemDelete(walletItem)
            }

            binding.switchCompat.isChecked = walletItem.isExist

            binding.switchCompat.setOnCheckedChangeListener { _, isChecked ->
                onWalletItemCheckedChangeListener?.onWalletChecked(walletItem, isChecked)
            }

            binding.root.setOnClickListener {
                walletItemClickedListener?.navigateToUpdateWallet(walletItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletItemViewHolder {
        val binding = CardWalletItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WalletItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WalletItemViewHolder, position: Int) {
        val walletItem = differ.currentList[position]
        holder.bindSubgroup(walletItem)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}
