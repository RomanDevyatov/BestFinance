package com.romandevyatov.bestfinance.ui.fragments.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.data.entities.IncomeGroup
import com.romandevyatov.bestfinance.databinding.ItemWithCheckboxBinding

class ArchivedGroupsAdapter(private val groupData: List<GroupItem>) :
    RecyclerView.Adapter<ArchivedGroupsAdapter.GroupViewHolder>() {

    private val differentCallback = object: DiffUtil.ItemCallback<GroupItem>() {
        override fun areItemsTheSame(oldItem: GroupItem, newItem: GroupItem): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: GroupItem, newItem: GroupItem): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, differentCallback)

    fun submitList(newList: List<GroupItem>) {
        differ.submitList(newList)
    }

    class GroupViewHolder(private val binding: ItemWithCheckboxBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindItem(
            groupItem: GroupItem
        ) {
            binding.groupName.text = groupItem.name
            binding.checkBox.isChecked = groupItem.isSelected

            binding.root.setOnClickListener {
                groupItem.isSelected = !groupItem.isSelected
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

    fun getSelectedGroups(): List<GroupItem> {
        return groupData.filter { it.isSelected }
    }
}