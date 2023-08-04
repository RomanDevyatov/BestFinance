package com.romandevyatov.bestfinance.ui.adapters.settings.groupswithsubgroups

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.romandevyatov.bestfinance.ui.fragments.settings.groupswithsubgroups.expense.GeneralExpenseGroupsAndSubGroupsFragment
import com.romandevyatov.bestfinance.ui.fragments.settings.groupswithsubgroups.income.GeneralIncomeGroupsAndSubGroupsFragment

class GeneralGroupsAndSubGroupsViewPagerAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {

    private val NUM_TABS = 2

    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return GeneralIncomeGroupsAndSubGroupsFragment()
        }
        return GeneralExpenseGroupsAndSubGroupsFragment()
    }
}
