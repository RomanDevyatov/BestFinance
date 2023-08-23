package com.romandevyatov.bestfinance.ui.adapters.settings.groupswithsubgroups

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.romandevyatov.bestfinance.ui.fragments.more.groupswithsubgroups.expense.ExpenseGroupsAndSubGroupsFragment
import com.romandevyatov.bestfinance.ui.fragments.more.groupswithsubgroups.income.IncomeGroupsAndSubGroupsFragment

class GroupsAndSubGroupsViewPagerAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {

    private val NUM_TABS = 2

    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return IncomeGroupsAndSubGroupsFragment()
        }
        return ExpenseGroupsAndSubGroupsFragment()
    }
}
