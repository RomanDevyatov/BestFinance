package com.romandevyatov.bestfinance.ui.adapters.settings.group

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.ItemWithCheckboxBinding
import com.romandevyatov.bestfinance.ui.adapters.settings.group.model.GroupItem

class ArchivedGroupsAdapter(
    private val listener: OnSubGroupCheckedChangeListener? = null
) : RecyclerView.Adapter<ArchivedGroupsAdapter.GroupViewHolder>() {

    interface OnSubGroupCheckedChangeListener {
        fun onSubgroupChecked(groupItem: GroupItem)
    }

    private val differentCallback = object: DiffUtil.ItemCallback<GroupItem>() {
        override fun areItemsTheSame(oldItem: GroupItem, newItem: GroupItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GroupItem, newItem: GroupItem): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, differentCallback)


    fun submitList(newList: MutableList<GroupItem>) {
        differ.submitList(newList)
    }

    inner class GroupViewHolder(val binding: ItemWithCheckboxBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bindItem(groupItem: GroupItem) {
            binding.groupName.text = groupItem.name
            binding.checkBox.isChecked = groupItem.isSelected

            binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
                groupItem.isSelected = isChecked
                listener?.onSubgroupChecked(groupItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemWithCheckboxBinding.inflate(inflater, parent, false)
        return GroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val currentItem = differ.currentList[position]
        holder.bindItem(currentItem)
    }

    override fun getItemCount() = differ.currentList.size

    fun getSelectedGroups(): MutableList<GroupItem> {
        return differ.currentList.filter { it.isSelected }.toMutableList()
    }
}
