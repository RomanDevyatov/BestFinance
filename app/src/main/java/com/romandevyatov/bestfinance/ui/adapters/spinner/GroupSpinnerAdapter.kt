package com.romandevyatov.bestfinance.ui.adapters.spinner

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.core.view.isVisible
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.ItemWithDelBinding

class GroupSpinnerAdapter(
    context: Context,
    private val resourceId: Int,
    private var items: MutableList<SpinnerItem>,
    private val addItemText: String,
    var listener: DeleteItemClickListener? = null
) : ArrayAdapter<SpinnerItem>(context, resourceId, items), Filterable {

    interface DeleteItemClickListener {

        fun archive(name: String)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val spinnerView = convertView ?: LayoutInflater.from(context).inflate(
            resourceId,
            parent,
            false
        )

        val binding = ItemWithDelBinding.bind(spinnerView)

//        val itemNameTextView = spinnerView.findViewById<TextView>(R.id.itemNameTextView)
//        val itemDeleteTextView = spinnerView.findViewById<TextView>(R.id.itemDelTextView)

        val itemText = items[position].name
        binding.itemNameTextView.text = itemText

        val hasListener = listener != null
        val isAddItem = itemText == addItemText
        binding.itemDelTextView.isVisible = hasListener && !isAddItem

        binding.itemDelTextView.setOnClickListener {
            listener?.archive(itemText)
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
            adapter.updateData(results?.values as MutableList<SpinnerItem>)
        }
    }

}
