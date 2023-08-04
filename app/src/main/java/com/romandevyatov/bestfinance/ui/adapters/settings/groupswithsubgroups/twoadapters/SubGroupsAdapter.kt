package com.romandevyatov.bestfinance.ui.adapters.settings.groupswithsubgroups.twoadapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.CardSubGroupsBinding
import com.romandevyatov.bestfinance.ui.adapters.settings.groupswithsubgroups.twoadapters.models.SubGroup

class SubGroupsAdapter(
    private val listener: OnSubGroupCheckedChangeListener? = null
) : RecyclerView.Adapter<SubGroupsAdapter.SubGroupViewHolder>() {

    interface OnSubGroupCheckedChangeListener {
        fun onSubgroupChecked(subgroup: SubGroup, isChecked: Boolean)

        fun onSubGroupDelete(subGroup: SubGroup)
    }

    private val differentCallback = object: DiffUtil.ItemCallback<SubGroup>() {
        override fun areItemsTheSame(oldItem: SubGroup, newItem: SubGroup): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SubGroup, newItem: SubGroup): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, differentCallback)

    fun updateSubgroups(newSubgroups: List<SubGroup>) {
        differ.submitList(newSubgroups)
    }

    inner class SubGroupViewHolder(
        private val binding: CardSubGroupsBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindSubgroup(subGroup: SubGroup) {
            binding.subgroupNameTextView.text = subGroup.name

            binding.deleteButton.setOnClickListener {
                listener?.onSubGroupDelete(subGroup)
            }

            binding.switchCompat.isChecked = subGroup.isExist

            binding.switchCompat.setOnCheckedChangeListener { _, isChecked ->
                subGroup.isExist = isChecked
                listener?.onSubgroupChecked(subGroup, isChecked)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubGroupViewHolder {
        val binding = CardSubGroupsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubGroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubGroupViewHolder, position: Int) {
        val subgroup = differ.currentList[position]
        holder.bindSubgroup(subgroup)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


}
