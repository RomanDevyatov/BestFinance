package com.romandevyatov.bestfinance.utils

import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView

object SpinnerUtil {

    fun getAllItemsFromAutoCompleteTextView(autoCompleteTextView: AutoCompleteTextView): List<String> {
        val adapter = autoCompleteTextView.adapter
        val allItems = mutableListOf<String>()

        if (adapter is ArrayAdapter<*>) {
            for (i in 0 until adapter.count) {
                allItems.add(adapter.getItem(i).toString())
            }
        }

        return allItems
    }

}