package com.romandevyatov.bestfinance.ui.adapters.spinnerutils

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import com.romandevyatov.bestfinance.R

class CustomSpinnerAdapter(
    context: Context,
    private val items: ArrayList<String>,
    private val listener: DeleteItemClickListener? = null
) : ArrayAdapter<String>(context, 0, items) {

    interface DeleteItemClickListener {

        fun archive(name: String)
    }

    override fun isEnabled(position: Int): Boolean {
        return position != 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return myView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val spinnerView = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.item_with_del,
            parent,
            false
        )

        val itemNameTextView = spinnerView.findViewById<TextView>(R.id.itemNameTextView)
        val itemDelTextView = spinnerView.findViewById<TextView>(R.id.itemDelTextView)

        val itemText = items[position]
        itemNameTextView.text = itemText ?: "N/A"

        if (position == 0 || position == count - 1) {
            itemDelTextView.isVisible = false
        }

        itemDelTextView.setOnClickListener {
            Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
            listener?.archive(itemText)
        }

        return spinnerView
    }

    private fun myView(position: Int, convertView: View?, parent: ViewGroup): View {
        val spinnerView = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.item_with_del,
            parent,
            false
        )

        val itemNameTextView = spinnerView.findViewById<TextView>(R.id.itemNameTextView)
        val itemDelTextView = spinnerView.findViewById<TextView>(R.id.itemDelTextView)

        val itemText = items[position]
        itemNameTextView.text = itemText ?: "N/A"
        itemDelTextView.isVisible = false

        return spinnerView
    }
}
