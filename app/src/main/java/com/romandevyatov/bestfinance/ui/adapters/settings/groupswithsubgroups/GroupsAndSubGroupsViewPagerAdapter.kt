package com.romandevyatov.bestfinance.ui.adapters.settings.groupswithsubgroups

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.romandevyatov.bestfinance.ui.fragments.more.settings.groupswithsubgroups.expense.SettingsExpenseGroupsAndSubGroupsFragment
import com.romandevyatov.bestfinance.ui.fragments.more.settings.groupswithsubgroups.income.SettingsIncomeGroupsAndSubGroupsFragment

class GroupsAndSubGroupsViewPagerAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {

    private val NUM_TABS = 2

    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return SettingsIncomeGroupsAndSubGroupsFragment()
        }
        return SettingsExpenseGroupsAndSubGroupsFragment()
    }
}
