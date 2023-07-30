package com.romandevyatov.bestfinance.ui.adapters.settings.subgroup

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.romandevyatov.bestfinance.ui.fragments.settings.subgroups.expense.ArchivedExpenseSubGroupsFragment
import com.romandevyatov.bestfinance.ui.fragments.settings.subgroups.income.ArchivedIncomeSubGroupsFragment

class ArchivedSubGroupsViewPagerAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {

    private val NUM_TABS = 2

    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return ArchivedIncomeSubGroupsFragment()
        }
        return ArchivedExpenseSubGroupsFragment()
    }
}
