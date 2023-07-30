package com.romandevyatov.bestfinance.ui.adapters.settings.group

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.romandevyatov.bestfinance.ui.fragments.settings.groups.expense.ArchivedExpenseGroupsFragment
import com.romandevyatov.bestfinance.ui.fragments.settings.groups.income.ArchivedIncomeGroupsFragment

class ArchivedGroupsViewPagerAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {

    private val NUM_TABS = 2

    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return ArchivedIncomeGroupsFragment()
        }
        return ArchivedExpenseGroupsFragment()
    }
}
