package com.romandevyatov.bestfinance.ui.adapters.settings.deprecated.subgroup.recyclerviewapproach.twoadapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.CardSubGroupsBinding

class SubGroupsAdapter3(
    private val subGroup3s: List<SubGroup3>,
    private val listener: OnSubGroupCheckedChangeListener? = null
) : RecyclerView.Adapter<SubGroupsAdapter3.SubGroupViewHolder>() {

    interface OnSubGroupCheckedChangeListener {
        fun onSubgroupChecked(subgroup: SubGroup3, isChecked: Boolean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubGroupViewHolder {
            val binding = CardSubGroupsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return SubGroupViewHolder(binding)
        }

        override fun onBindViewHolder(holder: SubGroupViewHolder, position: Int) {
            val subgroup = subGroup3s[position]
            holder.bindSubgroup(subgroup)
        }

        inner class SubGroupViewHolder(
            private val binding: CardSubGroupsBinding
            ) : RecyclerView.ViewHolder(binding.root) {

            fun bindSubgroup(subgroup: SubGroup3) {
                binding.subgroupNameTextView.text = subgroup.name

//                binding.subgroupCheckBox.isChecked = subgroup.isChecked
//
//                binding.subgroupCheckBox.setOnCheckedChangeListener { _, isChecked ->
//                    subgroup.isChecked = isChecked
//                    listener?.onSubgroupChecked(subgroup, isChecked)
//                }
            }
        }

    override fun getItemCount(): Int {
        return subGroup3s.size
    }
}
