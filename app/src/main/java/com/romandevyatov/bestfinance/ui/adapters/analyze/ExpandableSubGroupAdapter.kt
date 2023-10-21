package com.romandevyatov.bestfinance.ui.adapters.analyze

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.CardExpandableParentBinding
import com.romandevyatov.bestfinance.ui.adapters.analyze.models.ChildData
import com.romandevyatov.bestfinance.ui.adapters.analyze.models.SubParentData
import com.romandevyatov.bestfinance.utils.Constants

class ExpandableSubGroupAdapter(private val mList: List<SubParentData>) :
    RecyclerView.Adapter<ExpandableSubGroupAdapter.SubParentItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubParentItemViewHolder {
        val binding = CardExpandableParentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SubParentItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class SubParentItemViewHolder(private val binding: CardExpandableParentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val subParentData = mList[adapterPosition]
                subParentData.isExpanded = !subParentData.isExpanded
                notifyItemChanged(adapterPosition)
            }
        }

        fun bind(subParentData: SubParentData) {
            binding.labelGlobalGroup.text = subParentData.parentTitle
            binding.globalSummaTextView.text = calculateSubGroupSumma(subParentData).toString()

            if (subParentData.isExpanded) {
                binding.expandableLayout.visibility = View.VISIBLE
                binding.arrowImageview.setImageResource(R.drawable.ic_arrow_up)
            } else {
                binding.expandableLayout.visibility = View.GONE
                binding.arrowImageview.setImageResource(R.drawable.ic_arrow_down)
            }

            when (subParentData.type) {
                Constants.INCOMINGS_PARENT_TYPE -> {
                    val adapter = NestedChildAdapter(
                        subParentData.childNestedListOfIncomeSubGroup?.map {
                            ChildData(it, null, Constants.INCOMINGS_PARENT_TYPE)
                        }!!.toList()
                    )
                    binding.childRv.adapter = adapter
                }

                Constants.EXPENSES_PARENT_TYPE -> {
                    val adapter = NestedChildAdapter(
                        subParentData.childNestedListOfExpenseSubGroup?.map {
                            ChildData(null, it, Constants.EXPENSES_PARENT_TYPE)
                        }!!.toList()
                    )
                    binding.childRv.adapter = adapter
                }
            }

            binding.childRv.layoutManager = LinearLayoutManager(binding.root.context)
        }

        private fun calculateSubGroupSumma(subParentData: SubParentData): Double {
            return when (subParentData.type) {
                Constants.INCOMINGS_PARENT_TYPE -> {
                    subParentData.childNestedListOfIncomeSubGroup?.sumOf { incomeSubGroupWithIncomeHistories ->
                        incomeSubGroupWithIncomeHistories.incomeHistories.sumOf { it.amount }
                    } ?: 0.0
                }
                Constants.EXPENSES_PARENT_TYPE -> {
                    subParentData.childNestedListOfExpenseSubGroup?.sumOf { expenseSubGroupWithExpenseHistories ->
                        expenseSubGroupWithExpenseHistories.expenseHistory.sumOf { it.amount }
                    } ?: 0.0
                }
                else -> 0.0
            }
        }
    }

    override fun onBindViewHolder(holder: SubParentItemViewHolder, position: Int) {
        val subParentData: SubParentData = mList[position]
        holder.bind(subParentData)
    }
}
