package com.romandevyatov.bestfinance.ui.adapters.settings.groupswithsubgroups.twoadapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.CardGroupWithSubgroupsBinding
import com.romandevyatov.bestfinance.ui.adapters.settings.groupswithsubgroups.twoadapters.models.GroupWithSubGroupsItem

class GroupWithSubgroupsAdapter(
    private val groupListener: OnGroupCheckedChangeListener? = null,
    private val listener: SubGroupsAdapter.OnSubGroupCheckedChangeListener? = null
) : RecyclerView.Adapter<GroupWithSubgroupsAdapter.GroupViewHolder>() {

    interface OnGroupCheckedChangeListener {
        fun onGroupChecked(groupWithSubGroupsItem: GroupWithSubGroupsItem, isChecked: Boolean)
    }

    private val differentCallback = object: DiffUtil.ItemCallback<GroupWithSubGroupsItem>() {
        override fun areItemsTheSame(oldItem: GroupWithSubGroupsItem, newItem: GroupWithSubGroupsItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GroupWithSubGroupsItem, newItem: GroupWithSubGroupsItem): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, differentCallback)

    fun updateGroups(newGroups: List<GroupWithSubGroupsItem>) {
        differ.submitList(newGroups)
    }

    inner class GroupViewHolder(
        private val binding: CardGroupWithSubgroupsBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindGroup(groupWithSubGroupsItem: GroupWithSubGroupsItem) {
            binding.groupNameTextView.text = groupWithSubGroupsItem.name
            binding.switchCompat.isChecked = groupWithSubGroupsItem.isArchived

            binding.switchCompat.setOnCheckedChangeListener { _, isChecked ->
                groupListener?.onGroupChecked(groupWithSubGroupsItem, isChecked)
            }

            val subGroupsAdapter = SubGroupsAdapter(listener)
            binding.subGroupRecyclerView.layoutManager = LinearLayoutManager(binding.root.context)
            binding.subGroupRecyclerView.adapter = subGroupsAdapter
            subGroupsAdapter.updateSubgroups(groupWithSubGroupsItem.subgroups)
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
