package com.romandevyatov.bestfinance.ui.adapters.spinnerutils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filterable
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import com.romandevyatov.bestfinance.R

class SpinnerAdapter(
    context: Context,
    private val resourceId: Int,
    private var items: MutableList<String>,
    private val addItem: String,
    private val listener: DeleteItemClickListener? = null
) : ArrayAdapter<String>(context, resourceId, items), Filterable {


    interface DeleteItemClickListener {

        fun archive(name: String)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val spinnerView = convertView ?: LayoutInflater.from(context).inflate(
            resourceId,
            parent,
            false
        )

        val itemNameTextView = spinnerView.findViewById<TextView>(R.id.itemNameTextView)
        val itemDelTextView = spinnerView.findViewById<TextView>(R.id.itemDelTextView)

        val itemText = items[position]
        itemNameTextView.text = itemText

        itemDelTextView.isVisible = itemText != addItem
        itemDelTextView.setOnClickListener {
            Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
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

    fun updateData(newItems: MutableList<String>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

}






