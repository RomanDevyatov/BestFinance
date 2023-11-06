package com.romandevyatov.bestfinance.ui.adapters.more.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.CardExpandableGroupBinding
import com.romandevyatov.bestfinance.databinding.ItemSubcategoryBinding
import com.romandevyatov.bestfinance.ui.fragments.menu.MoreFragment

class SettingsSubCategoryAdapter(
    private val subcategoryData: List<MoreSubCategoryItem>,
    private val clickListener: OnSubCategoryClickListener,
    private val currencyCode: String? = null
) : RecyclerView.Adapter<SettingsSubCategoryAdapter.SubCategoryViewHolder>() {

    interface OnSubCategoryClickListener {
        fun onSubCategoryClick(subCategory: MoreSubCategoryItem)
    }

    inner class SubCategoryViewHolder(private val binding: ItemSubcategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MoreSubCategoryItem) {
            binding.subcategoryName.text = item.name
            binding.subcategoryIcon.setImageResource(item.icon)
            if (item.name == MoreFragment.CURRENCY) {
                binding.currencyCodeTextView.visibility = View.VISIBLE

                binding.currencyCodeTextView.text = currencyCode.toString()
            } else {
                binding.currencyCodeTextView.visibility = View.GONE
            }

            binding.tickIcon.visibility = View.VISIBLE

            binding.root.setOnClickListener {
                clickListener.onSubCategoryClick(item)
            }
        }
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

        holder.bind(currentItem)
    }

    override fun getItemCount() = subcategoryData.size
}
