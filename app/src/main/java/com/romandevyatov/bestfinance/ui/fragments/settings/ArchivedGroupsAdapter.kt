package com.romandevyatov.bestfinance.ui.fragments.settings

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.data.entities.IncomeGroup
import com.romandevyatov.bestfinance.databinding.ItemWithCheckboxBinding

class ArchivedGroupsAdapter :
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

    inner class GroupViewHolder(val binding: ItemWithCheckboxBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bindItem(groupItem: GroupItem) {
            binding.groupName.text = groupItem.name
            binding.checkBox.isChecked = groupItem.isSelected
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
        return differ.currentList.filter { it.isSelected }
    }

}