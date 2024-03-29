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

class SpinnerAdapter(
    context: Context,
    private val resourceId: Int,
    private var items: MutableList<String>,
    private val addItem: String? = null,
    var listener: DeleteItemClickListener? = null
) : ArrayAdapter<String>(context, resourceId, items), Filterable {

    interface DeleteItemClickListener {

        fun archive(name: String)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: ItemWithDelBinding
        val spinnerView: View

        if (convertView == null) {
            binding = ItemWithDelBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
            spinnerView = binding.root
            spinnerView.tag = binding
        } else {
            binding = convertView.tag as ItemWithDelBinding
            spinnerView = convertView
        }

        val itemText = items[position]
        binding.itemNameTextView.text = itemText

        val itemDeleteTextView = binding.itemDelTextView

        itemDeleteTextView.isVisible = addItem != null && itemText != addItem

        itemDeleteTextView.setOnClickListener {
            listener?.archive(itemText)
        }

        return spinnerView
    }

    override fun getItem(position: Int): String {
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

    fun updateData(newItems: MutableList<String>) {
        items = newItems
        notifyDataSetChanged()
    }

    private class CustomFilter(
        private val originalList: List<String>,
        private val adapter: SpinnerAdapter
    ) : Filter() {

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filterResults = FilterResults()

            if (constraint == null || constraint.isEmpty()) {
                filterResults.values = originalList
                filterResults.count = originalList.size
            } else {
                val filteredList = originalList.filter { it.contains(constraint, ignoreCase = false) }
                filterResults.values = filteredList
                filterResults.count = filteredList.size
            }

            return filterResults
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            adapter.updateData(results?.values as MutableList<String>)
        }
    }
}
