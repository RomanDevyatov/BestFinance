package com.romandevyatov.bestfinance.ui.adapters.settings.generalcategories.recyclerviewapproach.twoadapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.CardArchivedGroupWithSubgroupsBinding

class GroupWithSubgroupsAdapter2(
    private val groups: List<GroupWithSubGroups2>,
    private val listener: SubGroupsAdapter2.OnSubGroupCheckedChangeListener? = null
) : RecyclerView.Adapter<GroupWithSubgroupsAdapter2.GroupViewHolder>() {

    inner class GroupViewHolder(
        private val binding: CardArchivedGroupWithSubgroupsBinding,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindGroup(groupWithSubGroups: GroupWithSubGroups2) {
            binding.groupNameTextView.text = groupWithSubGroups.name
            binding.switchCompat2.isChecked = groupWithSubGroups.isExist

            binding.switchCompat2.setOnCheckedChangeListener { _, isChecked ->
                groupWithSubGroups.isExist = isChecked
                // add here listener
            }

            binding.subGroupRecyclerView.layoutManager = LinearLayoutManager(context)
            binding.subGroupRecyclerView.adapter = SubGroupsAdapter2(groupWithSubGroups.subgroups, listener)
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

