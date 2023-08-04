package com.romandevyatov.bestfinance.ui.adapters.settings.groupswithsubgroups.twoadapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.CardGroupWithSubgroupsBinding
import com.romandevyatov.bestfinance.ui.adapters.settings.groupswithsubgroups.twoadapters.models.GroupWithSubGroups

class GroupWithSubgroupsAdapter(
    private val groupListener: OnGroupCheckedChangeListener? = null,
    private val listener: SubGroupsAdapter.OnSubGroupCheckedChangeListener? = null
) : RecyclerView.Adapter<GroupWithSubgroupsAdapter.GroupViewHolder>() {

    interface OnGroupCheckedChangeListener {
        fun onGroupChecked(group: GroupWithSubGroups, isChecked: Boolean)
    }

    private val differentCallback = object: DiffUtil.ItemCallback<GroupWithSubGroups>() {
        override fun areItemsTheSame(oldItem: GroupWithSubGroups, newItem: GroupWithSubGroups): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GroupWithSubGroups, newItem: GroupWithSubGroups): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, differentCallback)

    fun updateGroups(newGroups: List<GroupWithSubGroups>) {
        differ.submitList(newGroups)
    }

    inner class GroupViewHolder(
        private val binding: CardGroupWithSubgroupsBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindGroup(groupWithSubGroups: GroupWithSubGroups) {
            binding.groupNameTextView.text = groupWithSubGroups.name
            binding.switchCompat.isChecked = groupWithSubGroups.isArchived

            binding.switchCompat.setOnCheckedChangeListener { _, isChecked ->
                groupWithSubGroups.isArchived = isChecked
                groupListener?.onGroupChecked(groupWithSubGroups, isChecked)
            }

            val subGroupsAdapter = SubGroupsAdapter(listener)
            binding.subGroupRecyclerView.layoutManager = LinearLayoutManager(binding.root.context)
            binding.subGroupRecyclerView.adapter = subGroupsAdapter
            subGroupsAdapter.updateSubgroups(groupWithSubGroups.subgroups)
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
