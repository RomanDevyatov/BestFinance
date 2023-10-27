package com.romandevyatov.bestfinance.ui.adapters.more.settings.settingsgroupswithsubgroups.tabs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.CardSubGroupsBinding
import com.romandevyatov.bestfinance.ui.adapters.more.settings.settingsgroupswithsubgroups.tabs.models.SettingsSubGroupItem

class SettingsSubGroupsAdapter(
    private val mList: MutableList<SettingsSubGroupItem> = mutableListOf(),
    private val onSubGroupListener: OnSubGroupListener? = null
    ) : RecyclerView.Adapter<SettingsSubGroupsAdapter.SubGroupViewHolder>() {

    interface OnSubGroupListener {
        fun onSubgroupChecked(settingsSubGroupItem: SettingsSubGroupItem, isChecked: Boolean)
        fun onSubGroupDelete(settingsSubGroupItem: SettingsSubGroupItem)
        fun navigateToUpdateSubGroup(id: Long)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubGroupViewHolder {
        val binding = CardSubGroupsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubGroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubGroupViewHolder, position: Int) {
        val subGroupItem = mList[position]
        holder.bind(subGroupItem)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class SubGroupViewHolder(
        val binding: CardSubGroupsBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(settingsSubGroupItem: SettingsSubGroupItem) {
            binding.subgroupNameTextView.text = settingsSubGroupItem.name

            binding.switchCompat.setOnCheckedChangeListener(null)
            binding.switchCompat.isChecked = settingsSubGroupItem.isExist

            // Set the listener after setting the state to avoid triggering it unnecessarily
            binding.switchCompat.setOnCheckedChangeListener { _, isChecked ->
                settingsSubGroupItem.isExist = isChecked
                onSubGroupListener?.onSubgroupChecked(settingsSubGroupItem, isChecked)
            }

            binding.deleteButton.setOnClickListener {
                onSubGroupListener?.onSubGroupDelete(settingsSubGroupItem)
            }

            binding.root.setOnClickListener {
                onSubGroupListener?.navigateToUpdateSubGroup(settingsSubGroupItem.id)
            }
        }
    }
}
