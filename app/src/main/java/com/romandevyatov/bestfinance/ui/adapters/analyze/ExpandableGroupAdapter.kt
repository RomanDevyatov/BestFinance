package com.romandevyatov.bestfinance.ui.adapters.analyze

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.CardExpandableParentBinding
import com.romandevyatov.bestfinance.ui.adapters.analyze.models.ParentData
import com.romandevyatov.bestfinance.ui.adapters.analyze.models.SubParentData
import com.romandevyatov.bestfinance.utils.Constants

class ExpandableGroupAdapter(
    private var parents: ArrayList<ParentData>,
    private val changingBalanceTitle: String
) : RecyclerView.Adapter<ExpandableGroupAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = CardExpandableParentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return parents.size
    }

    fun getList(): ArrayList<ParentData> {
        return parents
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(newList: ArrayList<ParentData>) {
        parents = newList
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(private val binding: CardExpandableParentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val subParentModel = parents[adapterPosition]
                subParentModel.isExpanded = !subParentModel.isExpanded
                notifyItemChanged(adapterPosition)
            }
        }

        fun bind(subParentModel: ParentData) {
            binding.labelGlobalGroup.text = subParentModel.analyzeParentTitle

            if (subParentModel.isExpanded) {
                binding.expandableLayout.visibility = View.VISIBLE
                binding.arrowImageview.setImageResource(R.drawable.ic_arrow_up)
            } else {
                binding.expandableLayout.visibility = View.GONE
                binding.arrowImageview.setImageResource(R.drawable.ic_arrow_down)
            }

            val adapter: ExpandableSubGroupAdapter
            when (subParentModel.type) {
                Constants.INCOMINGS_PARENT_TYPE -> {
                    adapter = ExpandableSubGroupAdapter(
                        subParentModel.subParentNestedListIncomings?.map {
                            if (it.incomeGroup != null) {
                                SubParentData(
                                    parentTitle = it.incomeGroup.name,
                                    childNestedListOfIncomeSubGroup = it.incomeSubGroupWithIncomeHistories,
                                    type = Constants.INCOMINGS_PARENT_TYPE
                                )
                            } else {
                                SubParentData(
                                    parentTitle = changingBalanceTitle,
                                    childNestedListOfIncomeSubGroup = it.incomeSubGroupWithIncomeHistories,
                                    type = Constants.INCOMINGS_PARENT_TYPE
                                )
                            }
                        }!!.toList()
                    )
                    binding.childRv.adapter = adapter

                    var summa = 0.0
                    for (incomeGroupWithIncomeSubGroupsIncludingIncomeHistories in subParentModel.subParentNestedListIncomings!!) {
                        val lis = incomeGroupWithIncomeSubGroupsIncludingIncomeHistories.incomeSubGroupWithIncomeHistories.map { incomeSubGroupWithIncomeHistories ->
                            incomeSubGroupWithIncomeHistories.incomeHistories.sumOf { it.amount }
                        }.toList()
                        summa += lis.sum()
                    }
                    binding.globalSummaTextView.text = summa.toString()
                }

                Constants.EXPENSES_PARENT_TYPE -> {
                    adapter = ExpandableSubGroupAdapter(
                        subParentModel.subParentNestedListExpenses?.map {
                            if (it.expenseGroup != null) {
                                SubParentData(
                                    parentTitle = it.expenseGroup.name,
                                    childNestedListOfExpenseSubGroup =
                                        it.expenseSubGroupWithExpenseHistories,
                                    type = Constants.EXPENSES_PARENT_TYPE
                                )
                            } else {
                                SubParentData(
                                    parentTitle = changingBalanceTitle,
                                    childNestedListOfExpenseSubGroup = it.expenseSubGroupWithExpenseHistories,
                                    type = Constants.INCOMINGS_PARENT_TYPE
                                )
                            }
                        }!!.toList()
                    )
                    binding.childRv.adapter = adapter

                    var summa = 0.0
                    for (expenseGroupWithExpenseSubGroupsIncludingExpenseHistories in subParentModel.subParentNestedListExpenses!!) {
                        val lis = expenseGroupWithExpenseSubGroupsIncludingExpenseHistories.expenseSubGroupWithExpenseHistories.map { expenseSubGroupWithExpenseHistories ->
                            expenseSubGroupWithExpenseHistories.expenseHistory.sumOf { it.amount }
                        }.toList()
                        summa += lis.sum()
                    }
                    binding.globalSummaTextView.text = summa.toString()
                }
            }

            binding.childRv.layoutManager = LinearLayoutManager(binding.root.context)
        }
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val subParentModel: ParentData = parents[position]
        holder.bind(subParentModel)
    }
}
