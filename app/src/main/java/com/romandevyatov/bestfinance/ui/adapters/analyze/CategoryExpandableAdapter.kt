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
import com.romandevyatov.bestfinance.ui.adapters.analyze.models.GroupItem

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

            val groupItemList = categoryItem.groups?.map {
                GroupItem(
                    groupName = it.groupName,
                    subGroupNameAndSumItem = it.subGroupNameAndSumItem
                )
            }!!.toMutableList()

            binding.groupsRecyclerview.adapter = GroupExpandableAdapter(groupItemList)

            var summa = 0.0
            for (groupData in groupItemList) {
                val groupSumma = groupData.subGroupNameAndSumItem?.sumOf {
                    it.sumOfSubGroup
                }

                summa += groupSumma ?: 0.0
            }
            binding.globalSummaTextView.text = summa.toString()

            binding.groupsRecyclerview.layoutManager = LinearLayoutManager(binding.root.context)
        }
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val subParentModel: CategoryItem = categoryItems[position]
        holder.bind(subParentModel)
    }
}
