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
    private var items: ArrayList<String>,
    private var listener: DeleteItemClickListener? = null
) : ArrayAdapter<String>(context, 0, items) {

    interface DeleteItemClickListener {

        fun archive(name: String)
    }

    private var selectedItemIndex: Int = -1

    override fun isEnabled(position: Int): Boolean {
        return position != 0
    }

    override fun areAllItemsEnabled(): Boolean {
        return true
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return items.size
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

        val itemNameTextView = spinnerView.findViewById(R.id.itemNameTextView) as TextView
        val itemDelTextView = spinnerView.findViewById(R.id.itemDelTextView) as TextView

        val itemText = items[position]
        itemNameTextView.text = itemText

        if (position == 0 || position == count - 1) {
            itemDelTextView.isVisible = false
        }

        if (position == selectedItemIndex) {
            spinnerView.setBackgroundColor(Color.rgb(56,184,226));
        } else {
            spinnerView.setBackgroundColor(Color.TRANSPARENT);
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

        val spnItemName = spinnerView.findViewById(R.id.itemNameTextView) as TextView
        val spnItemDel = spinnerView.findViewById(R.id.itemDelTextView) as TextView

        spnItemName.text = items[position]
        spnItemDel.isVisible = false

        return spinnerView
    }
}
