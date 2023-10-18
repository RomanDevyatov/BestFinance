package com.romandevyatov.bestfinance.ui.adapters.settings.groupswithsubgroups

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.databinding.CardSubGroupsBinding
import com.romandevyatov.bestfinance.ui.adapters.settings.groupswithsubgroups.models.SubGroupItem

class SubGroupsAdapter(
    private val mList: MutableList<SubGroupItem> = mutableListOf(),
    private val onSubGroupListener: OnSubGroupListener? = null
    ) : RecyclerView.Adapter<SubGroupsAdapter.SubGroupViewHolder>() {

    interface OnSubGroupListener {
        fun onSubgroupChecked(subGroupItem: SubGroupItem, isChecked: Boolean)
        fun onSubGroupDelete(subGroupItem: SubGroupItem)
        fun navigateToUpdateSubGroup(id: Long)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubGroupViewHolder {
        val binding = CardSubGroupsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubGroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubGroupViewHolder, position: Int) {
        val subGroupItem = mList[position]
        holder.bind(subGroupItem)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class SubGroupViewHolder(
        val binding: CardSubGroupsBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(subGroupItem: SubGroupItem) {
            binding.subgroupNameTextView.text = subGroupItem.name

            binding.switchCompat.setOnCheckedChangeListener(null)
            binding.switchCompat.isChecked = subGroupItem.isExist

            // Set the listener after setting the state to avoid triggering it unnecessarily
            binding.switchCompat.setOnCheckedChangeListener { _, isChecked ->
                subGroupItem.isExist = isChecked
                onSubGroupListener?.onSubgroupChecked(subGroupItem, isChecked)
            }

            binding.deleteButton.setOnClickListener {
                onSubGroupListener?.onSubGroupDelete(subGroupItem)
            }

            binding.root.setOnClickListener {
                onSubGroupListener?.navigateToUpdateSubGroup(subGroupItem.id)
            }
        }
    }
}
