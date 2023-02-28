package com.romandevyatov.bestfinance.ui.adapters.analyze

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.db.entities.mediator.ParentData
import com.romandevyatov.bestfinance.db.entities.relations.IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories
import com.romandevyatov.bestfinance.db.entities.relations.IncomeSubGroupWithIncomeHistories

class ExpandableRecyclerAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

//    private var list: MutableList<IncomeSubGroupWithIncomeHistories> = ArrayList()

    private val differentCallback = object: DiffUtil.ItemCallback<ParentData>() {

        override fun areItemsTheSame(oldItem: ParentData, newItem: ParentData): Boolean {
            return oldItem.parentTitle == newItem.parentTitle
        }

        override fun areContentsTheSame(oldItem: ParentData, newItem: ParentData): Boolean {
            return oldItem == newItem
        }
    }

    val parentDataListAsyncDiffer = AsyncListDiffer(this, differentCallback)

    private val viewPool = RecyclerView.RecycledViewPool()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.each_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return parentDataListAsyncDiffer.currentList.size
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
        val model: ParentData = parentDataListAsyncDiffer.currentList[position]

        holder as ItemViewHolder

        holder.mTextView.text = model.parentTitle

        if (model.isExpanded) {
            holder.expandableLayout.visibility = View.VISIBLE
            holder.mArrowImage.setImageResource(R.drawable.ic_arrow_up)
        } else {
            holder.expandableLayout.visibility = View.GONE
            holder.mArrowImage.setImageResource(R.drawable.ic_arrow_down)
        }

        val adapter = NestedAdapter(model.nestedList)
        holder.nestedRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.nestedRecyclerView.setHasFixedSize(true)
        holder.nestedRecyclerView.adapter = adapter

        holder.linearLayout.setOnClickListener {
            model.isExpanded = !model.isExpanded
            notifyItemChanged(holder.adapterPosition)
        }
    }

    fun submitList(incomeSubGroups: List<ParentData>) {
        parentDataListAsyncDiffer.submitList(incomeSubGroups)
    }

//    private val differentCallback = object: DiffUtil.ItemCallback<ParentData>() {
//
//        override fun areItemsTheSame(oldItem: ParentData, newItem: ParentData): Boolean {
//            return oldItem.parentTitle == newItem.parentTitle && oldItem.type == newItem.type && oldItem.nestedList == newItem.nestedList
//        }
//
//        override fun areContentsTheSame(oldItem: ParentData, newItem: ParentData): Boolean {
//            return oldItem == newItem
//        }
//
//    }
//
//    val parentDataAssyncListDiffer = AsyncListDiffer(this, differentCallback)

//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        return if (viewType == Constants.PARENT) {
//            val rowView: View = LayoutInflater.from(parent.context).inflate(R.layout.expandable_parent, parent,false)
//            GroupViewHolder(rowView)
//        } else {
//            val rowView: View = LayoutInflater.from(parent.context).inflate(R.layout.expandable_child, parent,false)
//            ChildViewHolder(rowView)
//        }
//    }
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        val currentList = parentDataAssyncListDiffer.currentList[position]
//        if (currentList.type == Constants.PARENT) {
//            holder as GroupViewHolder
//            holder.apply {
//                parentTV?.text = currentList.parentTitle
//                downIV?.setOnClickListener {
//                    expandOrCollapseParentItem(currentList, position)
//                }
//            }
//        } else {
//            holder as ChildViewHolder
//
//            holder.apply {
//                val singleService = currentList.nestedList.first()
//                childTV?.text = singleService.childTitle // incomeSubGroup.name
//            }
//        }
//
//    }
//
//    override fun getItemCount(): Int {
//        return parentDataAssyncListDiffer.currentList.size
//    }
//
//    override fun getItemViewType(position: Int): Int = parentDataAssyncListDiffer.currentList[position].type
//
//    override fun getItemId(position: Int): Long {
//        return position.toLong()
//    }
//
//    fun submitList(list: List<ParentData>) {
//        parentDataAssyncListDiffer.submitList(list)
//    }
//
//    private fun expandOrCollapseParentItem(currentList: ParentData, position: Int) {
//        if (currentList.isExpanded) {
//            collapseParentRow(position)
//        } else {
//            expandParentRow(position)
//        }
//    }
//
//    @SuppressLint("NotifyDataSetChanged")
//    private fun expandParentRow(position: Int) {
//        val currentBoardingRow = parentDataAssyncListDiffer.currentList[position]
//        currentBoardingRow.isExpanded = true
//
//        val childElements = currentBoardingRow.nestedList
//        var nextPosition = position
//
//        if (currentBoardingRow.type == Constants.PARENT) {
//
//            childElements.forEach { element ->
//                val parentModel = ParentData()
//                parentModel.type = Constants.CHILD
//                val subList : ArrayList<ChildData> = ArrayList()
//                subList.add(element)
//                parentModel.nestedList = subList
//                val tempList = parentDataAssyncListDiffer.currentList.toMutableList()
//                tempList[position].isExpanded = true
//                tempList.add(++nextPosition, parentModel)
////                submitList(tempList)
//                mClick.doExpandUpdate(tempList)
//            }
//            notifyDataSetChanged()
//        }
//    }
//
//    @SuppressLint("NotifyDataSetChanged")
//    private fun collapseParentRow(position: Int) {
//        val currentBoardingRow = parentDataAssyncListDiffer.currentList[position]
//        val services = parentDataAssyncListDiffer.currentList[position].nestedList
//        parentDataAssyncListDiffer.currentList[position].isExpanded = false
//        if(currentBoardingRow.type == Constants.PARENT) {
//            services.forEach { _ ->
//                parentDataAssyncListDiffer.currentList.removeAt(position + 1)
//            }
//            notifyDataSetChanged()
//        }
//    }
//
//    class GroupViewHolder(row: View) : RecyclerView.ViewHolder(row) {
//        val parentTV = row.findViewById(R.id.parent_Title) as TextView?
//        val downIV = row.findViewById(R.id.down_iv) as ImageView?
//    }
//
//    class ChildViewHolder(row: View) : RecyclerView.ViewHolder(row) {
//        val childTV = row.findViewById(R.id.child_Title) as TextView?
//
//    }

//    @SuppressLint("NotifyDataSetChanged")
//    private fun collapseRow(position: Int){
//        val row = parentDataAssyncListDiffer.currentList[position]
//        var nextPosition = position + 1
//        when (row.type) {
//            Constants.PARENT -> {
//                outerloop@ while (true) {
//                    //  println("Next Position during Collapse $nextPosition size is ${shelfModelList.size} and parent is ${shelfModelList[nextPosition].type}")
//
//                    if (nextPosition == parentDataAssyncListDiffer.currentList.size || parentDataAssyncListDiffer.currentList[nextPosition].type == Constants.PARENT) {
//
//                        break@outerloop
//                    }
//
//                    parentDataAssyncListDiffer.currentList.removeAt(nextPosition)
//                }
//
//                notifyDataSetChanged()
//            }
//
//        }
//    }

//    private fun expandRow(position: Int){
//        val row = parentDataAssyncListDiffer.currentList[position]
//        var nextPosition = position
//        when (row.type) {
//            Constants.PARENT -> {
//                for (child in row.subList){
//                    parentDataAssyncListDiffer.currentList.add(++nextPosition, ExpandableCountryModel(ExpandableCountryModel.CHILD, child))
//                }
//                notifyDataSetChanged()
//            }
//            Constants.CHILD -> {
//                notifyDataSetChanged()
//            }
//        }
//    }


}



//        val row = countryStateModelList[position]
//        when(row.type){
//            ExpandableCountryModel.PARENT -> {
//                (holder as CountryStateParentViewHolder).countryName.text = row.countryParent.country
//                holder.closeImage.setOnClickListener {
//                    if (row.isExpanded) {
//                        row.isExpanded = false
//                        collapseRow(position)
//                        holder.layout.setBackgroundColor(Color.WHITE)
//
//
//                    }else{
//                        holder.layout.setBackgroundColor(Color.GRAY)
//                        row.isExpanded = true
//                        holder.upArrowImg.visibility = View.VISIBLE
//                        holder.closeImage.visibility = View.GONE
//                        expandRow(position)
//                    }
//                }
//                holder.upArrowImg.setOnClickListener{
//                    if(row.isExpanded){
//                        row.isExpanded = false
//                        collapseRow(position)
//                        holder.layout.setBackgroundColor(Color.WHITE)
//                        holder.upArrowImg.visibility = View.GONE
//                        holder.closeImage.visibility = View.VISIBLE
//
//                    }
//                }
//            }
//
//
//            ExpandableCountryModel.CHILD -> {
//                (holder as CountryStateChildViewHolder).stateName.text = row.countryChild.name
//                holder.capitalImage.text = row.countryChild.capital
//            }
//        }