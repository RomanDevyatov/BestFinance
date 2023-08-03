package com.romandevyatov.bestfinance.ui.adapters.settings.generalcategories.recyclerviewapproach.twoadapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.CardArchivedGroupWithSubgroupsBinding
import com.romandevyatov.bestfinance.ui.adapters.settings.generalcategories.recyclerviewapproach.twoadapters.models.GroupWithSubGroups

class GroupWithSubgroupsAdapter(
    private val groupListener: OnGroupCheckedChangeListener? = null,
    private val listener: SubGroupsAdapter.OnSubGroupCheckedChangeListener? = null
) : RecyclerView.Adapter<GroupWithSubgroupsAdapter.GroupViewHolder>() {

    interface OnGroupCheckedChangeListener {
        fun onGroupChecked(group: GroupWithSubGroups, isChecked: Boolean)
    }

    private val differ = AsyncListDiffer(this, GroupDiffCallback())

    fun updateGroups(newGroups: List<GroupWithSubGroups>) {
        differ.submitList(newGroups)
    }

    inner class GroupViewHolder(
        private val binding: CardArchivedGroupWithSubgroupsBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindGroup(groupWithSubGroups: GroupWithSubGroups) {
            binding.groupNameTextView.text = groupWithSubGroups.name
            binding.switchCompat.isChecked = groupWithSubGroups.isExist

            binding.switchCompat.setOnCheckedChangeListener { _, isChecked ->
                groupWithSubGroups.isExist = isChecked
                groupListener?.onGroupChecked(groupWithSubGroups, isChecked)
            }

            val subGroupsAdapter = SubGroupsAdapter(listener)
            binding.subGroupRecyclerView.layoutManager = LinearLayoutManager(binding.root.context)
            binding.subGroupRecyclerView.adapter = subGroupsAdapter
            subGroupsAdapter.updateSubgroups(groupWithSubGroups.subgroups)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val binding = CardArchivedGroupWithSubgroupsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

private class GroupDiffCallback : DiffUtil.ItemCallback<GroupWithSubGroups>() {
    override fun areItemsTheSame(oldItem: GroupWithSubGroups, newItem: GroupWithSubGroups): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: GroupWithSubGroups, newItem: GroupWithSubGroups): Boolean {
        return oldItem == newItem
    }
}