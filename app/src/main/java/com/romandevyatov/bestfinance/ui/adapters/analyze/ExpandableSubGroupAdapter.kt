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
import com.romandevyatov.bestfinance.ui.fragments.analyze.ChildData
import com.romandevyatov.bestfinance.ui.fragments.analyze.SubParentData
import com.romandevyatov.bestfinance.utils.Constants

class ExpandableSubGroupAdapter (private val mList: List<SubParentData>) :
    RecyclerView.Adapter<ExpandableSubGroupAdapter.ItemViewHolder2>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder2 {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.expandable_parent_card, parent, false)
        return ItemViewHolder2(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ItemViewHolder2(itemView: View) : RecyclerView.ViewHolder(itemView) {
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

    override fun onBindViewHolder(holder: ItemViewHolder2, position: Int) {
        val model: SubParentData = mList[position]

        holder as ItemViewHolder2

        holder.mTextView.text = model.parentTitle

        if (model.isExpanded) {
            holder.expandableLayout.visibility = View.VISIBLE
            holder.mArrowImage.setImageResource(R.drawable.ic_arrow_up)
        } else {
            holder.expandableLayout.visibility = View.GONE
            holder.mArrowImage.setImageResource(R.drawable.ic_arrow_down)
        }

        val adapter: NestedChildAdapter
        when (model.type) {
            Constants.INCOMINGS_PARENT_TYPE -> {
                adapter = NestedChildAdapter(model.childNestedList?.map {
                    ChildData(it, null, Constants.INCOMINGS_PARENT_TYPE)
                }!!.toList())
                holder.nestedRecyclerView.adapter = adapter
            }

            Constants.EXPENSES_PARENT_TYPE -> {
                adapter = NestedChildAdapter(model.childNestedListExpenses?.map {
                    ChildData(null, it, Constants.EXPENSES_PARENT_TYPE)
                }!!.toList())
                holder.nestedRecyclerView.adapter = adapter
            }
        }

        holder.nestedRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)


        holder.linearLayout.setOnClickListener {
            model.isExpanded = !model.isExpanded
            notifyItemChanged(holder.adapterPosition)
        }
    }


}
