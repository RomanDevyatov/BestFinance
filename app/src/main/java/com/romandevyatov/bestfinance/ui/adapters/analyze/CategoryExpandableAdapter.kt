package com.romandevyatov.bestfinance.ui.adapters.analyze

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.CardExpandableCategoryBinding
import com.romandevyatov.bestfinance.ui.adapters.analyze.models.CategoryItem

class CategoryExpandableAdapter(
    private var categoryItems: ArrayList<CategoryItem>
) : RecyclerView.Adapter<CategoryExpandableAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = CardExpandableCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return categoryItems.size
    }

    fun getList(): ArrayList<CategoryItem> {
        return categoryItems
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(newList: ArrayList<CategoryItem>) {
        categoryItems = newList
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(private val binding: CardExpandableCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val subParentModel = categoryItems[adapterPosition]
                subParentModel.isExpanded = !subParentModel.isExpanded
                notifyItemChanged(adapterPosition)
            }
        }

        fun bind(categoryItem: CategoryItem) {
            binding.labelGlobalGroup.text = categoryItem.categoryName

            if (categoryItem.isExpanded) {
                binding.expandableLayout.visibility = View.VISIBLE
                binding.arrowImageview.setImageResource(R.drawable.ic_arrow_up)
            } else {
                binding.expandableLayout.visibility = View.GONE
                binding.arrowImageview.setImageResource(R.drawable.ic_arrow_down)
            }

            if (categoryItem.groups != null) {
                binding.groupsRecyclerview.adapter = GroupExpandableAdapter(categoryItem.groups)
            }

            binding.globalSummaTextView.text = categoryItem.categorySum

            binding.groupsRecyclerview.layoutManager = LinearLayoutManager(binding.root.context)
        }
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val subParentModel: CategoryItem = categoryItems[position]
        holder.bind(subParentModel)
    }
}
