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
import com.romandevyatov.bestfinance.ui.fragments.analyze.ParentData
import com.romandevyatov.bestfinance.ui.fragments.analyze.SubParentData
import com.romandevyatov.bestfinance.utils.Constants

class ExpandableGroupAdapter(val parents: ArrayList<ParentData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.expandable_parent_card, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return parents.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val linearLayout: LinearLayout
        val expandableLayout: RelativeLayout
        val mTextView: TextView
        val mArrowImage: ImageView
        val nestedRecyclerView: RecyclerView

        init {
            linearLayout = itemView.findViewById(R.id.linear_layout)
            expandableLayout = itemView.findViewById(R.id.expandable_layout)
            mTextView = itemView.findViewById(R.id.itemTv)
            mArrowImage = itemView.findViewById(R.id.arro_imageview)
            nestedRecyclerView = itemView.findViewById(R.id.child_rv)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val subParentModel: ParentData = parents[position]

        holder as ItemViewHolder

        holder.mTextView.text = subParentModel.analyzeParentTitle

        if (subParentModel.isExpanded) {
            holder.expandableLayout.visibility = View.VISIBLE
            holder.mArrowImage.setImageResource(R.drawable.ic_arrow_up)
        } else {
            holder.expandableLayout.visibility = View.GONE
            holder.mArrowImage.setImageResource(R.drawable.ic_arrow_down)
        }

        val adapter: ExpandableSubGroupAdapter
        when (subParentModel.type) {
            Constants.INCOMINGS_PARENT_TYPE -> {
                adapter = ExpandableSubGroupAdapter(subParentModel.subParentNestedList?.map {
                    SubParentData(
                        parentTitle = it.incomeGroup.name,
                        childNestedList = it.incomeSubGroupWithIncomeHistories,
                        type = Constants.INCOMINGS_PARENT_TYPE)
                }!!.toList())
                holder.nestedRecyclerView.adapter = adapter
            }

            Constants.EXPENSES_PARENT_TYPE -> {
                adapter = ExpandableSubGroupAdapter(subParentModel.subParentNestedListExpenses?.map {
                    SubParentData(parentTitle = it.expenseGroup.name,
                    childNestedListExpenses = it.expenseSubGroupWithExpenseHistories,
                    type = Constants.EXPENSES_PARENT_TYPE)
                }!!.toList())
                holder.nestedRecyclerView.adapter = adapter
            }
        }

        val tr = subParentModel.subParentNestedList?.map {
            SubParentData(
                parentTitle = it.incomeGroup.name,
                childNestedList = it.incomeSubGroupWithIncomeHistories,
                type = Constants.INCOMINGS_PARENT_TYPE
            )
        }?.toList()

//        val adapter = ExpandableSubParentAdapter(tr!!)
        holder.nestedRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
//        holder.nestedRecyclerView.setHasFixedSize(true)

        holder.linearLayout.setOnClickListener {
            subParentModel.isExpanded = !subParentModel.isExpanded
            notifyItemChanged(holder.adapterPosition)
        }
    }

}
