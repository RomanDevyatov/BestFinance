package com.romandevyatov.bestfinance.ui.adapters.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.R

class CategoryAdapter(
    private val categoryData: List<CategoryItem>,
    private val subCategoryClickListener: SubCategoryAdapter.OnSubCategoryClickListener) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryName: TextView = itemView.findViewById(R.id.categoryName)
        val categoryIcon: ImageView = itemView.findViewById(R.id.categoryIcon)
        val subcategoriesList: RecyclerView = itemView.findViewById(R.id.subcategoriesList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(itemView)
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
