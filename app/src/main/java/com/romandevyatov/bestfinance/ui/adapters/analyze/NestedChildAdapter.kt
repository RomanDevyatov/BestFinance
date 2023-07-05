package com.romandevyatov.bestfinance.ui.adapters.analyze

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.ui.adapters.analyze.models.ChildData
import com.romandevyatov.bestfinance.utils.Constants

class NestedChildAdapter(private val mList: List<ChildData>) :
    RecyclerView.Adapter<NestedChildAdapter.NestedViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NestedViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.card_expandable_child, parent, false)
        return NestedViewHolder(view)
    }

    override fun onBindViewHolder(holder: NestedViewHolder, position: Int) {
        when (mList[position].type) {
            Constants.INCOMINGS_PARENT_TYPE -> {
                holder.mTv.text = mList[position].incomeSubGroupWithIncomeHistories?.incomeSubGroup?.name

                val sumOfSubGroup = mList[position].incomeSubGroupWithIncomeHistories?.incomeHistories?.sumOf { it.amount }
                holder.categorySummaOfExpandableChild.text = sumOfSubGroup.toString()
            }

            Constants.EXPENSES_PARENT_TYPE -> {
                holder.mTv.text = mList[position].expenseSubGroupIncludingExpenseHistories?.expenseSubGroup?.name

                val sumOfSubGroup = mList[position].expenseSubGroupIncludingExpenseHistories?.expenseHistory?.sumOf { it.amount }
                holder.categorySummaOfExpandableChild.text = sumOfSubGroup.toString()
            }
        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class NestedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mTv: TextView
        val categorySummaOfExpandableChild: TextView

        init {
            mTv = itemView.findViewById(R.id.expandable_child_textview)
            categorySummaOfExpandableChild = itemView.findViewById(R.id.category_summa_of_expandable_child)
        }
    }
}
