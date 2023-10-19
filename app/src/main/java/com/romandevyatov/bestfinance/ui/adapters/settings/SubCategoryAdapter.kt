package com.romandevyatov.bestfinance.ui.adapters.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.ItemSubcategoryBinding

class SubCategoryAdapter(
    private val subcategoryData: List<SubCategoryItem>,
    private val clickListener: OnSubCategoryClickListener
) :
    RecyclerView.Adapter<SubCategoryAdapter.SubCategoryViewHolder>() {

    interface OnSubCategoryClickListener {
        fun onSubCategoryClick(subCategory: SubCategoryItem)
    }

    class SubCategoryViewHolder(binding: ItemSubcategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val subcategoryName = binding.subcategoryName
        val subcategoryIcon = binding.subcategoryIcon
        val tickIcon = binding.tickIcon
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubCategoryViewHolder {
        val binding = ItemSubcategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SubCategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubCategoryViewHolder, position: Int) {
        val currentItem = subcategoryData[position]

        holder.subcategoryName.text = currentItem.name
        holder.subcategoryIcon.setImageResource(currentItem.icon)

        holder.tickIcon.visibility = View.VISIBLE

        holder.itemView.setOnClickListener {
            clickListener.onSubCategoryClick(currentItem)
        }
    }

    override fun getItemCount() = subcategoryData.size
}
