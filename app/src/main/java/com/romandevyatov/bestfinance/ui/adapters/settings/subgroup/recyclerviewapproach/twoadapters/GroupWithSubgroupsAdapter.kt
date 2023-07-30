package com.romandevyatov.bestfinance.ui.adapters.settings.subgroup.recyclerviewapproach.twoadapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.CardArchivedGroupWithSubgroupsBinding

class GroupWithSubgroupsAdapter(
    private val groups: List<GroupWithSubGroups>,
    private val listener: SubGroupsAdapter.OnSubGroupCheckedChangeListener? = null
) : RecyclerView.Adapter<GroupWithSubgroupsAdapter.GroupViewHolder>() {

    inner class GroupViewHolder(
        private val binding: CardArchivedGroupWithSubgroupsBinding,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindGroup(groupWithSubGroups: GroupWithSubGroups) {
            binding.groupNameTextView.text = groupWithSubGroups.name

            binding.subGroupRecyclerView.layoutManager = LinearLayoutManager(context)
            binding.subGroupRecyclerView.adapter = SubGroupsAdapter(groupWithSubGroups.subgroups, listener)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val binding = CardArchivedGroupWithSubgroupsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GroupViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = groups[position]
        holder.bindGroup(group)
    }

    override fun getItemCount(): Int {
        return groups.size
    }
}

