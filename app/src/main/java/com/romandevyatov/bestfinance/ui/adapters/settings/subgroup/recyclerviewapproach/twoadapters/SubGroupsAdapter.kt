package com.romandevyatov.bestfinance.ui.adapters.settings.subgroup.recyclerviewapproach.twoadapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.CardSubgroupsBinding

class SubGroupsAdapter(
    private val subGroups: List<SubGroup>,
    private val listener: OnSubGroupCheckedChangeListener? = null
) : RecyclerView.Adapter<SubGroupsAdapter.SubGroupViewHolder>() {

    interface OnSubGroupCheckedChangeListener {
        fun onSubgroupChecked(subgroup: SubGroup, isChecked: Boolean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubGroupViewHolder {
            val binding = CardSubgroupsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return SubGroupViewHolder(binding)
        }

        override fun onBindViewHolder(holder: SubGroupViewHolder, position: Int) {
            val subgroup = subGroups[position]
            holder.bindSubgroup(subgroup)
        }

        inner class SubGroupViewHolder(
            private val binding: CardSubgroupsBinding
            ) : RecyclerView.ViewHolder(binding.root) {

            fun bindSubgroup(subgroup: SubGroup) {
                binding.subgroupNameTextView.text = subgroup.name

                binding.subgroupCheckBox.isChecked = subgroup.isChecked

                binding.subgroupCheckBox.setOnCheckedChangeListener { _, isChecked ->
                    subgroup.isChecked = isChecked
                    listener?.onSubgroupChecked(subgroup, isChecked)
                }
            }
        }

    override fun getItemCount(): Int {
        return subGroups.size
    }
}
