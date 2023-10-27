package com.romandevyatov.bestfinance.ui.adapters.spinner

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import androidx.core.view.isVisible
import com.romandevyatov.bestfinance.databinding.ItemWithDelBinding
import com.romandevyatov.bestfinance.ui.adapters.spinner.models.SpinnerItem

class GroupSpinnerAdapter(
    context: Context,
    private val resourceId: Int,
    private var items: MutableList<SpinnerItem>,
    private val addItemText: String? = null,
    var deleteItemClickListener: DeleteItemClickListener? = null
) : ArrayAdapter<SpinnerItem>(context, resourceId, items), Filterable {

    interface DeleteItemClickListener {

        fun archive(spinnerItem: SpinnerItem)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val spinnerView = convertView ?: LayoutInflater.from(context).inflate(
            resourceId,
            parent,
            false
        )

        val binding = ItemWithDelBinding.bind(spinnerView)

        val spinnerItem = items[position]
        val itemText = spinnerItem.name
        binding.itemNameTextView.text = itemText

        val hasListener = deleteItemClickListener != null
        val isAddItem = addItemText != null && itemText == addItemText
        binding.itemDelTextView.isVisible = hasListener && !isAddItem

        binding.itemDelTextView.setOnClickListener {
            deleteItemClickListener?.archive(spinnerItem)
        }

        return spinnerView
    }

    override fun getItem(position: Int): SpinnerItem {
        return items[position]
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private var customFilter: CustomFilter? = null

    override fun getFilter(): Filter {
        if (customFilter == null) {
            customFilter = CustomFilter(items, this)
        }
        return customFilter!!
    }

    fun updateData(newItems: MutableList<SpinnerItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    private class CustomFilter(
        private val originalList: List<SpinnerItem>,
        private val adapter: GroupSpinnerAdapter
    ) : Filter() {

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filterResults = FilterResults()

            if (constraint == null || constraint.isEmpty()) {
                filterResults.values = originalList
                filterResults.count = originalList.size
            } else {
                val filteredList = originalList.filter { it.name.contains(constraint, ignoreCase = false) }
                filterResults.values = filteredList
                filterResults.count = filteredList.size
            }

            return filterResults
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            if (results != null && results.values is MutableList<*>) {
                val filteredData = results.values as? MutableList<SpinnerItem>
                if (filteredData != null) {
                    adapter.updateData(filteredData)
                }
            }
        }
    }
}
