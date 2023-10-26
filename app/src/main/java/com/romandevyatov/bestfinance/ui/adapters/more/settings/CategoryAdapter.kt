package com.romandevyatov.bestfinance.ui.adapters.more.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.ItemCategoryBinding

class CategoryAdapter(
    private val categoryData: List<CategoryItem>,
    private val subCategoryClickListener: SubCategoryAdapter.OnSubCategoryClickListener
) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(private val binding: ItemCategoryBinding) :
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

        val subcategoryAdapter = SubCategoryAdapter(currentItem.subcategories, subCategoryClickListener)
        holder.subcategoriesList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = subcategoryAdapter
        }
    }

    override fun getItemCount() = categoryData.size
}
