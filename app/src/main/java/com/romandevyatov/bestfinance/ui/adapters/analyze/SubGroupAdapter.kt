package com.romandevyatov.bestfinance.ui.adapters.analyze

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.CardExpandableSubGroupBinding
import com.romandevyatov.bestfinance.ui.adapters.analyze.models.SubGroupNameAndSumItem

class SubGroupAdapter(private val subGroupNameAndSumItems: List<SubGroupNameAndSumItem>) :
    RecyclerView.Adapter<SubGroupAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = CardExpandableSubGroupBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = subGroupNameAndSumItems[position]

        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return subGroupNameAndSumItems.size
    }

    inner class ItemViewHolder(private val binding: CardExpandableSubGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SubGroupNameAndSumItem) {
            binding.expandableChildTextview.text = item.subGroupName

            binding.categorySummaOfExpandableChild.text = item.sumOfSubGroup.toString()
        }
    }
}
