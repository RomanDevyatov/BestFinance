package com.romandevyatov.bestfinance.ui.adapters.settings.deprecated.subgroup.recyclerviewapproach.oneadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.CardArchivedGroupWithSubgroupsBinding
import com.romandevyatov.bestfinance.databinding.CardSubGroupsBinding
import com.romandevyatov.bestfinance.ui.adapters.settings.deprecated.subgroup.recyclerviewapproach.twoadapters.SubGroup3
import com.romandevyatov.bestfinance.ui.adapters.settings.deprecated.subgroup.recyclerviewapproach.twoadapters.SubGroupsAdapter3

class ExpandableRecyclerView1Adapter(
    private val items: List<ExpandableItem>,
    onSubGroupCheckedImpl: SubGroupsAdapter3.OnSubGroupCheckedChangeListener? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnSubGroupCheckedChangeListener {
        fun onSubgroupChecked(subgroup: SubGroup3, isChecked: Boolean)
    }

    private var subgroupCheckedChangeListener: OnSubGroupCheckedChangeListener? = null

    fun setSubGroupCheckedChangeListener(listener: OnSubGroupCheckedChangeListener) {
        this.subgroupCheckedChangeListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_GROUP -> {
                val binding = CardArchivedGroupWithSubgroupsBinding.inflate(inflater, parent, false)
                GroupViewHolder(binding)
            }
            VIEW_TYPE_SUBGROUP -> {
                val binding = CardSubGroupsBinding.inflate(inflater, parent, false)
                SubGroupViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Unknown viewType: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is GroupViewHolder -> holder.bindGroup(item as Group3)
            is SubGroupViewHolder -> holder.bindSubgroup(item as SubGroup3)
            else -> throw IllegalArgumentException("Unknown ViewHolder: ${holder.javaClass.simpleName}")
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is Group3 -> VIEW_TYPE_GROUP
            else -> VIEW_TYPE_SUBGROUP
        }
    }

    inner class GroupViewHolder(private val binding: CardArchivedGroupWithSubgroupsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindGroup(group3: Group3) {
            binding.groupNameTextView.text = group3.name
        }
    }

    inner class SubGroupViewHolder(private val binding: CardSubGroupsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindSubgroup(subgroup: SubGroup3) {
            binding.subgroupNameTextView.text = subgroup.name

//            binding.subgroupCheckBox.isChecked = subgroup.isChecked
//
//            binding.subgroupCheckBox.setOnCheckedChangeListener { _, isChecked ->
//                subgroup.isChecked = isChecked
////                subgroupCheckedChangeListener?.onSubgroupChecked(subgroup, isChecked)
//            }
        }
    }

    companion object {
        private const val VIEW_TYPE_GROUP = 0
        private const val VIEW_TYPE_SUBGROUP = 1
    }
}

