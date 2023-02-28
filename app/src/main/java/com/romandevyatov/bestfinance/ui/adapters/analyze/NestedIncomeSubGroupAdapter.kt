package com.romandevyatov.bestfinance.ui.adapters.analyze

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.db.entities.mediator.ChildData
import com.romandevyatov.bestfinance.utils.Constants

class NestedIncomeSubGroupAdapter(private val mList: List<ChildData>) :
    RecyclerView.Adapter<NestedIncomeSubGroupAdapter.NestedViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NestedViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.expandable_child_card, parent, false)
        return NestedViewHolder(view)
    }

    override fun onBindViewHolder(holder: NestedViewHolder, position: Int) {
        when (mList[position].type) {
            Constants.INCOMINGS_PARENT_TYPE -> {
                holder.mTv.text = mList[position].incomeSubGroupWithIncomeHistories?.incomeSubGroup?.name
            }

            Constants.EXPENSES_PARENT_TYPE -> {
                holder.mTv.text = mList[position].expenseSubGroupsIncludingExpenseHistories?.expenseSubGroup?.name
            }
        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class NestedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mTv: TextView

        init {
            mTv = itemView.findViewById(R.id.expandable_child_textview)
        }
    }
}
