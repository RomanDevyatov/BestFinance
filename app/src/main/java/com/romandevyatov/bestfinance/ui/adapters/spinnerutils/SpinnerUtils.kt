package com.romandevyatov.bestfinance.ui.adapters.spinnerutils

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
    }
}
