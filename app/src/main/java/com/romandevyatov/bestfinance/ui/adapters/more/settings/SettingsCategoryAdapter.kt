package com.romandevyatov.bestfinance.ui.adapters.more.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.ItemCategoryBinding

class SettingsCategoryAdapter(
    private val categoryData: List<SettingsCategoryItem>,
    private val subCategoryClickListener: SettingsSubCategoryAdapter.OnSubCategoryClickListener
) :
    RecyclerView.Adapter<SettingsCategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val categoryName = binding.categoryName
        val categoryIcon = binding.categoryIcon
        val subcategoriesList = binding.subcategoriesList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val currentItem = categoryData[position]

        holder.categoryName.text = currentItem.name
        holder.categoryIcon.setImageResource(currentItem.icon)

        val subcategoryAdapterSettings = SettingsSubCategoryAdapter(currentItem.subcategories, subCategoryClickListener)
        holder.subcategoriesList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = subcategoryAdapterSettings
        }
    }

    override fun getItemCount() = categoryData.size
}
