package com.romandevyatov.bestfinance.ui.adapters.settings.generalcategories.recyclerviewapproach.twoadapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.CardSubgroupsBinding

class SubGroupsAdapter2(
    private val subGroups: List<SubGroup2>,
    private val listener: OnSubGroupCheckedChangeListener? = null
) : RecyclerView.Adapter<SubGroupsAdapter2.SubGroupViewHolder>() {

    interface OnSubGroupCheckedChangeListener {
        fun onSubgroupChecked(subgroup: SubGroup2, isChecked: Boolean)
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

            fun bindSubgroup(subgroup: SubGroup2) {
                binding.subgroupNameTextView.text = subgroup.name

                binding.subgroupCheckBox.isChecked = subgroup.isChecked

                binding.subgroupCheckBox.setOnCheckedChangeListener { _, isChecked ->
                    subgroup.isChecked = isChecked
                    listener?.onSubgroupChecked(subgroup, isChecked)
                }

                binding.switchCompat.isChecked = subgroup.isExist

                binding.switchCompat.setOnCheckedChangeListener { _, isChecked ->
                    subgroup.isExist = isChecked
                    listener?.onSubgroupChecked(subgroup, isChecked)
                }

            }
        }

    override fun getItemCount(): Int {
        return subGroups.size
    }
}
