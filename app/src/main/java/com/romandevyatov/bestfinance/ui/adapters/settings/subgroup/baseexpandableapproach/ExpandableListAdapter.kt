package com.romandevyatov.bestfinance.ui.adapters.settings.subgroup.baseexpandableapproach

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.romandevyatov.bestfinance.R

class ExpandableListAdapter(private val context: Context, private val group1s: List<Group1>) :
    BaseExpandableListAdapter() {

    private val selectedSubgroups: MutableList<SubGroup1> = mutableListOf()

    fun getSelectedSubgroups(): List<SubGroup1> {
        return selectedSubgroups.toList()
    }

    override fun getGroup(groupPosition: Int): Any {
        return group1s[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return group1s[groupPosition].subgroups[childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        if (view == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.card_archived_group_with_subgroups, null)
        }

        val group = group1s[groupPosition]
        val groupNameTextView = view?.findViewById<TextView>(R.id.groupNameTextView)
        groupNameTextView?.text = group.name

        return view!!
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        if (view == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.card_subgroups, null)
        }

        val subgroup = group1s[groupPosition].subgroups[childPosition]
        val subgroupNameTextView = view?.findViewById<TextView>(R.id.subgroupNameTextView)
        subgroupNameTextView?.text = subgroup.name

        val subgroupCheckBox = view?.findViewById<CheckBox>(R.id.subgroupCheckBox)
        subgroupCheckBox?.isChecked = subgroup.isChecked
        subgroupCheckBox?.setOnCheckedChangeListener { _, isChecked ->
            subgroup.isChecked = isChecked
            if (isChecked) {
                selectedSubgroups.add(subgroup)
            } else {
                selectedSubgroups.remove(subgroup)
            }
        }

        return view!!
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    override fun getGroupCount(): Int {
        return group1s.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return group1s[groupPosition].subgroups.size
    }
}



