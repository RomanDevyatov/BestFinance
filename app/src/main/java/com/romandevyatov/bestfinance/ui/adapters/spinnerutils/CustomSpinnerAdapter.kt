package com.romandevyatov.bestfinance.ui.adapters.spinnerutils

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import com.romandevyatov.bestfinance.R

class CustomSpinnerAdapter(
    context: Context,
    private val items: List<String>
) : ArrayAdapter<String>(context, 0, items) {

//    override fun isEnabled(position: Int): Boolean {
//        return position != 0
//    }

//    override fun areAllItemsEnabled(): Boolean {
//        return false
//    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return myView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }

    private fun myView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.spinner_item_with_button,
            parent, false
        )

        val itemText = view.findViewById<TextView>(R.id.item_text9)
        val deleteButton = view.findViewById<Button>(R.id.delete_button9)

        itemText.text = items[position]

        if (position == 0) {
            itemText.setTextColor(Color.RED)
        } else {

        }

        deleteButton.setOnClickListener {
            remove(items[position])
            notifyDataSetChanged()
        }

        return view
    }
}
