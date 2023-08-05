package com.romandevyatov.bestfinance.ui.adapters.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.R

class SubCategoryAdapter(
    private val subcategoryData: List<SubCategoryItem>,
    private val clickListener: OnSubCategoryClickListener
) :
    RecyclerView.Adapter<SubCategoryAdapter.SubCategoryViewHolder>() {

    interface OnSubCategoryClickListener {
        fun onSubCategoryClick(subCategory: SubCategoryItem)
    }

    class SubCategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val subcategoryName: TextView = itemView.findViewById(R.id.subcategoryName)
        val subcategoryIcon: ImageView = itemView.findViewById(R.id.subcategoryIcon)
        val tickIcon: ImageView = itemView.findViewById(R.id.tickIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubCategoryViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_subcategory, parent, false)
        return SubCategoryViewHolder(itemView)
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
