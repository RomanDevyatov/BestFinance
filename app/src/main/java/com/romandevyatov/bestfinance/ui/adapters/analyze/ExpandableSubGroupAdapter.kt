package com.romandevyatov.bestfinance.ui.adapters.analyze

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.ui.adapters.analyze.models.ChildData
import com.romandevyatov.bestfinance.ui.adapters.analyze.models.SubParentData
import com.romandevyatov.bestfinance.utils.Constants

class ExpandableSubGroupAdapter (private val mList: List<SubParentData>) :
    RecyclerView.Adapter<ExpandableSubGroupAdapter.SubParentItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubParentItemViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.card_expandable_parent, parent, false)
        return SubParentItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class SubParentItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val linearLayout: LinearLayout
        val expandableLayout: RelativeLayout
        val mTextView: TextView
        val summaTextView: TextView
        val mArrowImage: ImageView
        val nestedRecyclerView: RecyclerView

        init {
            linearLayout = itemView.findViewById(R.id.linear_layout)
            expandableLayout = itemView.findViewById(R.id.expandable_layout)
            mTextView = itemView.findViewById(R.id.label_global_group)
            summaTextView = itemView.findViewById(R.id.global_summa_text_view)
            mArrowImage = itemView.findViewById(R.id.arrow_imageview)
            nestedRecyclerView = itemView.findViewById(R.id.child_rv)
        }
    }

    override fun onBindViewHolder(holder: SubParentItemViewHolder, position: Int) {
        val subParentData: SubParentData = mList[position]

        holder as SubParentItemViewHolder

        holder.mTextView.text = subParentData.parentTitle

        if (subParentData.isExpanded) {
            holder.expandableLayout.visibility = View.VISIBLE
            holder.mArrowImage.setImageResource(R.drawable.ic_arrow_up)
        } else {
            holder.expandableLayout.visibility = View.GONE
            holder.mArrowImage.setImageResource(R.drawable.ic_arrow_down)
        }

        val adapter: NestedChildAdapter
        when (subParentData.type) {
            Constants.INCOMINGS_PARENT_TYPE -> {
                adapter = NestedChildAdapter(subParentData.childNestedListOfIncomeSubGroup?.map {
                    ChildData(it, null, Constants.INCOMINGS_PARENT_TYPE)
                }!!.toList())
                holder.nestedRecyclerView.adapter = adapter

                val subGroupSumma = subParentData.childNestedListOfIncomeSubGroup?.sumOf { incomeSubGroupWithIncomeHistories ->
                    incomeSubGroupWithIncomeHistories.incomeHistories.sumOf { it.amount }
                }
                holder.summaTextView.text = subGroupSumma.toString()
            }

            Constants.EXPENSES_PARENT_TYPE -> {
                adapter = NestedChildAdapter(subParentData.childNestedListOfExpenseSubGroup?.map {
                    ChildData(null, it, Constants.EXPENSES_PARENT_TYPE)
                }!!.toList())
                holder.nestedRecyclerView.adapter = adapter

                val subGroupSumma = subParentData.childNestedListOfExpenseSubGroup?.sumOf { expenseSubGroupWithExpenseHistories ->
                    expenseSubGroupWithExpenseHistories.expenseHistory.sumOf { it.amount }
                }
                holder.summaTextView.text = subGroupSumma.toString()
            }
        }

        holder.nestedRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)


        holder.linearLayout.setOnClickListener {
            subParentData.isExpanded = !subParentData.isExpanded
            notifyItemChanged(holder.adapterPosition)
        }
    }
}
