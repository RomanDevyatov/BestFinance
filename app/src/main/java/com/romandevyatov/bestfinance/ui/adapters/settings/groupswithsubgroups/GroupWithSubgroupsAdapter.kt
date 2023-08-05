package com.romandevyatov.bestfinance.ui.adapters.settings.groupswithsubgroups

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.CardGroupWithSubgroupsBinding
import com.romandevyatov.bestfinance.ui.adapters.settings.groupswithsubgroups.models.GroupWithSubGroupsItem

class GroupWithSubgroupsAdapter(
    private val groupListener: OnGroupCheckedChangeListener? = null,
    private val listener: SubGroupsAdapter.OnSubGroupListener? = null
) : RecyclerView.Adapter<GroupWithSubgroupsAdapter.GroupViewHolder>() {

    interface OnGroupCheckedChangeListener {
        fun onGroupChecked(groupWithSubGroupsItem: GroupWithSubGroupsItem, isChecked: Boolean)
        fun onGroupDelete(groupWithSubGroupsItem: GroupWithSubGroupsItem)
        fun navigate(name: String)
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

    inner class GroupViewHolder(
        val binding: CardGroupWithSubgroupsBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindGroup(groupWithSubGroupsItem: GroupWithSubGroupsItem) {
            binding.groupNameTextView.text = groupWithSubGroupsItem.name

            binding.switchCompat.setOnCheckedChangeListener(null)
            binding.switchCompat.isChecked = groupWithSubGroupsItem.isExist
            binding.switchCompat.setOnCheckedChangeListener { _, isChecked ->
                groupWithSubGroupsItem.isExist = isChecked
                groupListener?.onGroupChecked(groupWithSubGroupsItem, isChecked)

                if (isChecked) {
                    binding.subGroupRecyclerView.visibility = View.VISIBLE
                } else {
                    binding.subGroupRecyclerView.visibility = View.GONE
                }
            }

            if (groupWithSubGroupsItem.isExist) {
                binding.subGroupRecyclerView.visibility = View.VISIBLE
            } else {
                binding.subGroupRecyclerView.visibility = View.GONE
            }

            binding.deleteButton.setOnClickListener {
                groupListener?.onGroupDelete(groupWithSubGroupsItem)
            }

            val subGroupsAdapter = SubGroupsAdapter(groupWithSubGroupsItem.subgroups, listener)
            binding.subGroupRecyclerView.layoutManager = LinearLayoutManager(binding.root.context)
            binding.subGroupRecyclerView.adapter = subGroupsAdapter

            binding.root.setOnClickListener {
                groupListener?.navigate(groupWithSubGroupsItem.name)
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
