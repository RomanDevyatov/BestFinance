package com.romandevyatov.bestfinance.ui.adapters.more.settings.settingswallets

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.CardWalletItemBinding
import com.romandevyatov.bestfinance.ui.adapters.more.settings.settingswallets.models.SettingsWalletItem

class SettingsWalletsAdapter(
    private val onWalletItemCheckedChangeListener: OnWalletItemCheckedChangeListener? = null,
    private val walletItemDeleteListener: OnWalletItemDeleteListener? = null,
    private val walletItemClickedListener: OnWalletItemClickedListener? = null
) : RecyclerView.Adapter<SettingsWalletsAdapter.WalletItemViewHolder>() {

    interface OnWalletItemCheckedChangeListener {
        fun onWalletChecked(settingsWalletItem: SettingsWalletItem, isChecked: Boolean)
    }

    interface OnWalletItemDeleteListener {
        fun onWalletItemDelete(settingsWalletItem: SettingsWalletItem)
    }

    interface OnWalletItemClickedListener {
        fun navigateToUpdateWallet(wallet: SettingsWalletItem)
    }

    private val differentCallback = object: DiffUtil.ItemCallback<SettingsWalletItem>() {
        override fun areItemsTheSame(oldItem: SettingsWalletItem, newItem: SettingsWalletItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SettingsWalletItem, newItem: SettingsWalletItem): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, differentCallback)

    fun submitList(settingsWalletItems: List<SettingsWalletItem>) {
        differ.submitList(settingsWalletItems)
    }

    fun removeItem(settingsWalletItem: SettingsWalletItem) {
        val position = differ.currentList.indexOf(settingsWalletItem)
        if (position != -1) {
            val updatedList = differ.currentList.toMutableList()
            updatedList.removeAt(position)
            differ.submitList(updatedList)
        }
    }

    inner class WalletItemViewHolder(
        private val binding: CardWalletItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindSubgroup(settingsWalletItem: SettingsWalletItem) {
            binding.walletTextView.text = settingsWalletItem.name
            binding.walletBalanceTextView.text = settingsWalletItem.balance

            binding.deleteButton.setOnClickListener {
                walletItemDeleteListener?.onWalletItemDelete(settingsWalletItem)
            }

            binding.switchCompat.isChecked = settingsWalletItem.isExist

            binding.switchCompat.setOnCheckedChangeListener { _, isChecked ->
                onWalletItemCheckedChangeListener?.onWalletChecked(settingsWalletItem, isChecked)
            }

            binding.root.setOnClickListener {
                walletItemClickedListener?.navigateToUpdateWallet(settingsWalletItem)
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
