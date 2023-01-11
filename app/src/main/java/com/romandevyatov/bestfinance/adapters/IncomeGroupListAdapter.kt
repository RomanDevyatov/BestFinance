package com.romandevyatov.bestfinance.adapters

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.romandevyatov.bestfinance.R

class IncomeGroupListAdapter(
    private val context: Activity,
    private val id: Array<String>,
    private val name: Array<String>) : ArrayAdapter<String>(context, R.layout.income_group_list, name) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.income_group_list, null, true)

        val idText = rowView.findViewById(R.id.text_view_id) as TextView
        val nameText = rowView.findViewById(R.id.text_view_name) as TextView

        idText.text = "Id: ${id[position]}"
        nameText.text = "Name: ${name[position]}"

        return rowView
    }
}
