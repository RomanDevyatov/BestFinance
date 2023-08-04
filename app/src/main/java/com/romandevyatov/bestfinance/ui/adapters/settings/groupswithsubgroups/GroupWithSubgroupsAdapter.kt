package com.romandevyatov.bestfinance.ui.adapters.settings.groupswithsubgroups

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.CardGroupWithSubgroupsBinding
import com.romandevyatov.bestfinance.ui.adapters.settings.groupswithsubgroups.models.GroupWithSubGroupsItem
import com.romandevyatov.bestfinance.ui.adapters.settings.groupswithsubgroups.models.SubGroupItem

class GroupWithSubgroupsAdapter(
    private val groupListener: OnGroupCheckedChangeListener? = null,
    private val listener: SubGroupsAdapter.OnSubGroupCheckedChangeListener? = null
) : RecyclerView.Adapter<GroupWithSubgroupsAdapter.GroupViewHolder>() {

    interface OnGroupCheckedChangeListener {
        fun onGroupChecked(groupWithSubGroupsItem: GroupWithSubGroupsItem, isChecked: Boolean)
        fun onGroupDelete(groupWithSubGroupsItem: GroupWithSubGroupsItem)
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

    fun submitList(newGroups: List<GroupWithSubGroupsItem>) {
        differ.submitList(newGroups)
    }

    fun removeItem(groupWithSubGroupsItem: GroupWithSubGroupsItem) {
        val position = differ.currentList.indexOf(groupWithSubGroupsItem)
        if (position != -1) {
            val updatedList = differ.currentList.toMutableList()
            updatedList.removeAt(position)
            differ.submitList(updatedList)
        }
    }

    fun removeSubItem(subGroupItem: SubGroupItem) {
        val groupPosition = differ.currentList.indexOfFirst { it.id == subGroupItem.groupId }
        if (groupPosition != -1) {
            val updatedGroup = differ.currentList[groupPosition].copy(subgroups = null)
            val updatedList = differ.currentList.toMutableList()
            updatedList[groupPosition] = updatedGroup
            differ.submitList(updatedList)
        }
    }

    inner class GroupViewHolder(
        private val binding: CardGroupWithSubgroupsBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private val subGroupsAdapter = SubGroupsAdapter(listener)

        fun bindGroup(groupWithSubGroupsItem: GroupWithSubGroupsItem) {
            binding.groupNameTextView.text = groupWithSubGroupsItem.name
            binding.switchCompat.isChecked = groupWithSubGroupsItem.isArchived

            binding.switchCompat.setOnCheckedChangeListener { _, isChecked ->
                groupListener?.onGroupChecked(groupWithSubGroupsItem, isChecked)
            }

            binding.deleteButton.setOnClickListener {
                groupListener?.onGroupDelete(groupWithSubGroupsItem)
            }


            binding.subGroupRecyclerView.layoutManager = LinearLayoutManager(binding.root.context)
            binding.subGroupRecyclerView.adapter = subGroupsAdapter
            groupWithSubGroupsItem.subgroups?.let {
                subGroupsAdapter.submitList(it)
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
