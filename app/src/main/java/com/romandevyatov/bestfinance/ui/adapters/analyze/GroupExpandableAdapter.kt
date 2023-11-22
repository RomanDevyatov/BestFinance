package com.romandevyatov.bestfinance.ui.adapters.analyze

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.CardExpandableGroupBinding
import com.romandevyatov.bestfinance.ui.adapters.analyze.models.GroupItem

class GroupExpandableAdapter(private val groupItems: List<GroupItem>) :
    RecyclerView.Adapter<GroupExpandableAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = CardExpandableGroupBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return groupItems.size
    }

    inner class ItemViewHolder(private val binding: CardExpandableGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val subParentData = groupItems[adapterPosition]
                subParentData.isExpanded = !subParentData.isExpanded
                notifyItemChanged(adapterPosition)
            }
        }

        fun bind(groupItem: GroupItem) {
            binding.groupName.text = groupItem.groupName
            binding.globalSummaTextView.text = groupItem.groupSum

            if (groupItem.isExpanded) {
                binding.expandableLayout.visibility = View.VISIBLE
                binding.arrowImageview.setImageResource(R.drawable.ic_arrow_up)
            } else {
                binding.expandableLayout.visibility = View.GONE
                binding.arrowImageview.setImageResource(R.drawable.ic_arrow_down)
            }

            binding.subGroupsRecyclerview.layoutManager = LinearLayoutManager(binding.root.context)

            val adapter = groupItem.subGroupNameAndSumItem?.let { SubGroupAdapter(it) }
            binding.subGroupsRecyclerview.adapter = adapter
        }
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val subParentData: GroupItem = groupItems[position]
        holder.bind(subParentData)
    }
}
