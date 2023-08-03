package com.romandevyatov.bestfinance.ui.adapters.settings.deprecated.subgroup.recyclerviewapproach.twoadapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.CardArchivedGroupWithSubgroupsBinding

class GroupWithSubgroupsAdapter3(
    private val groups: List<GroupWithSubGroups3>,
    private val listener: SubGroupsAdapter3.OnSubGroupCheckedChangeListener? = null
) : RecyclerView.Adapter<GroupWithSubgroupsAdapter3.GroupViewHolder>() {

    inner class GroupViewHolder(
        private val binding: CardArchivedGroupWithSubgroupsBinding,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindGroup(groupWithSubGroups3: GroupWithSubGroups3) {
            binding.groupNameTextView.text = groupWithSubGroups3.name

            binding.subGroupRecyclerView.layoutManager = LinearLayoutManager(context)
            binding.subGroupRecyclerView.adapter = SubGroupsAdapter3(groupWithSubGroups3.subgroups, listener)
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

