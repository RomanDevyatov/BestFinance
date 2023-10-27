package com.romandevyatov.bestfinance.ui.adapters.more.settings.settingsgroupswithsubgroups.tabs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.CardGroupWithSubgroupsBinding
import com.romandevyatov.bestfinance.ui.adapters.more.settings.settingsgroupswithsubgroups.tabs.models.SettingsGroupWithSubGroupsItem

class SettingsGroupWithSubgroupsAdapter(
    private val groupListener: OnGroupCheckedChangeListener? = null,
    private val listener: SettingsSubGroupsAdapter.OnSubGroupListener? = null
) : RecyclerView.Adapter<SettingsGroupWithSubgroupsAdapter.GroupViewHolder>() {

    interface OnGroupCheckedChangeListener {
        fun onGroupChecked(settingsGroupWithSubGroupsItem: SettingsGroupWithSubGroupsItem, isChecked: Boolean)
        fun onGroupDelete(settingsGroupWithSubGroupsItem: SettingsGroupWithSubGroupsItem)
        fun navigateToUpdateGroup(name: String)
    }

    private val differentCallback = object: DiffUtil.ItemCallback<SettingsGroupWithSubGroupsItem>() {
        override fun areItemsTheSame(oldItem: SettingsGroupWithSubGroupsItem, newItem: SettingsGroupWithSubGroupsItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SettingsGroupWithSubGroupsItem, newItem: SettingsGroupWithSubGroupsItem): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, differentCallback)

    fun submitList(newGroups: List<SettingsGroupWithSubGroupsItem>) {
        differ.submitList(newGroups)
    }

    inner class GroupViewHolder(
        val binding: CardGroupWithSubgroupsBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindGroup(settingsGroupWithSubGroupsItem: SettingsGroupWithSubGroupsItem) {
            binding.groupNameTextView.text = settingsGroupWithSubGroupsItem.name

            binding.switchCompat.setOnCheckedChangeListener(null)
            binding.switchCompat.isChecked = settingsGroupWithSubGroupsItem.isExist
            binding.switchCompat.setOnCheckedChangeListener { _, isChecked ->
                settingsGroupWithSubGroupsItem.isExist = isChecked
                groupListener?.onGroupChecked(settingsGroupWithSubGroupsItem, isChecked)

                if (isChecked) {
                    binding.historiesByDateRecyclerView.visibility = View.VISIBLE
                } else {
                    binding.historiesByDateRecyclerView.visibility = View.GONE
                }
            }

            if (settingsGroupWithSubGroupsItem.isExist) {
                binding.historiesByDateRecyclerView.visibility = View.VISIBLE
            } else {
                binding.historiesByDateRecyclerView.visibility = View.GONE
            }

            binding.deleteButton.setOnClickListener {
                groupListener?.onGroupDelete(settingsGroupWithSubGroupsItem)
            }

            val settingsSubGroupsAdapter = SettingsSubGroupsAdapter(settingsGroupWithSubGroupsItem.subgroups, listener)
            binding.historiesByDateRecyclerView.layoutManager = LinearLayoutManager(binding.root.context)
            binding.historiesByDateRecyclerView.adapter = settingsSubGroupsAdapter

            binding.root.setOnClickListener {
                groupListener?.navigateToUpdateGroup(settingsGroupWithSubGroupsItem.name)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val binding = CardGroupWithSubgroupsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = differ.currentList[position]
        holder.bindGroup(group)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}
