package com.romandevyatov.bestfinance.ui.adapters.spinner

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class SpinnerUtils {
    companion object {
        fun getArraySpinner(context: Context): ArrayAdapter<String> {
            val spinnerAdapter: ArrayAdapter<String> =
                object : ArrayAdapter<String>(context, com.google.android.material.R.layout.support_simple_spinner_dropdown_item) {

                    override fun isEnabled(position: Int): Boolean {
                        return position != 0
                    }

                    override fun areAllItemsEnabled(): Boolean {
                        return false
                    }

                    override fun getDropDownView(
                        position: Int,
                        convertView: View?,
                        parent: ViewGroup
                    ): View {
                        val view: TextView = super.getDropDownView(position, convertView, parent) as TextView
                        if (position == 0) {
                            view.setTextColor(Color.GRAY)
                        } else {

                        }

                        return view
                    }
                }

            return spinnerAdapter
        }

//        fun getIncomeGroupArraySpinner(context: Context, spinnerItems: ArrayList<String>, listener: CustomSpinnerAdapter.DeleteItemClickListener<String>): ArrayAdapter<String> {
//            val spinnerAdapter: ArrayAdapter<String> =
//                object : ArrayAdapter<String>(context, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, spinnerItems) {
//
//                    override fun isEnabled(position: Int): Boolean {
//                        return position != 0
//                    }
//
//                    override fun areAllItemsEnabled(): Boolean {
//                        return false
//                    }
//
//                    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
//                        val view = convertView ?: LayoutInflater.from(context).inflate(
//                            android.R.layout.simple_spinner_dropdown_item,
//                            parent,
//                            false
//                        )
//
//                        val itemText = view.findViewById<TextView>(android.R.id.text1)
//                        itemText.text = getItem(position)
//
////                        val view: TextView = super.getDropDownView(position, convertView, parent) as TextView
//                        if (position == 0) {
//                            itemText.setTextColor(Color.GRAY)
//                        } else {
//
//                        }
//
//                        view.setOnLongClickListener {
//                            listener.archiveIncomeGroupItem(itemText.text.toString())
//                            notifyDataSetChanged()
//                            true
//                        }
//
//                        return view
//                    }
//                }
//
//            return spinnerAdapter
//        }
    }
}
