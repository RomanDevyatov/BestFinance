package com.romandevyatov.bestfinance.ui.adapters.settings.groupswithsubgroups

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.CardSubGroupsBinding
import com.romandevyatov.bestfinance.ui.adapters.settings.groupswithsubgroups.models.SubGroupItem

class SubGroupsAdapter(
    private val listener: OnSubGroupCheckedChangeListener? = null
) : RecyclerView.Adapter<SubGroupsAdapter.SubGroupViewHolder>() {

    interface OnSubGroupCheckedChangeListener {
        fun onSubgroupChecked(subGroupItem: SubGroupItem, isChecked: Boolean)
        fun onSubGroupDelete(subGroupItem: SubGroupItem)
    }

    private val differCallback = object : DiffUtil.ItemCallback<SubGroupItem>() {
        override fun areItemsTheSame(oldItem: SubGroupItem, newItem: SubGroupItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SubGroupItem, newItem: SubGroupItem): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, differCallback)

    fun submitList(newSubgroups: List<SubGroupItem>) {
        differ.submitList(newSubgroups)
    }

    fun removeItem(subGroupsItem: SubGroupItem) {
        val position = differ.currentList.indexOf(subGroupsItem)
        if (position != -1) {
            val updatedList = differ.currentList.toMutableList()
            updatedList.removeAt(position)
            submitList(updatedList)
        }
    }

    inner class SubGroupViewHolder(
        private val binding: CardSubGroupsBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(subGroupItem: SubGroupItem) {
            binding.subgroupNameTextView.text = subGroupItem.name

            binding.deleteButton.setOnClickListener {
                listener?.onSubGroupDelete(subGroupItem)
            }

            binding.switchCompat.isChecked = subGroupItem.isExist

            binding.switchCompat.setOnCheckedChangeListener { _, isChecked ->
                listener?.onSubgroupChecked(subGroupItem, isChecked)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubGroupViewHolder {
        val binding = CardSubGroupsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubGroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubGroupViewHolder, position: Int) {
        val subGroupItem = differ.currentList[position]
        holder.bind(subGroupItem)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}
