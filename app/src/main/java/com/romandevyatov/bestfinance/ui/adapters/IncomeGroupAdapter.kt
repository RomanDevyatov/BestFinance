package com.romandevyatov.bestfinance.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.IncomeGroupCardBinding
import com.romandevyatov.bestfinance.db.entities.IncomeGroup

class IncomeGroupAdapter(
    private val onClickListener: IncomeGroupItemClickListener
) : RecyclerView.Adapter<IncomeGroupItemViewHolder>() {

//    inner class MyViewHolder(val incomeGroupCardBinding: IncomeGroupCardBinding): RecyclerView.ViewHolder(incomeGroupCardBinding.root) {
//        init {
//            incomeGroupCardBinding.root.setOnClickListener {
//                val position = adapterPosition
////                onClickListener.deleteOnClick(position)
//                true
//            }
//        }
//    }

    private val differentCallback = object: DiffUtil.ItemCallback<IncomeGroup>() {
        override fun areItemsTheSame(oldItem: IncomeGroup, newItem: IncomeGroup): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: IncomeGroup, newItem: IncomeGroup): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differentCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomeGroupItemViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = IncomeGroupCardBinding.inflate(from, parent, false)
        return IncomeGroupItemViewHolder(parent.context, binding, onClickListener)
    }

    override fun onBindViewHolder(holder: IncomeGroupItemViewHolder, position: Int) {
        holder.bindIncomeGroupItem(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}