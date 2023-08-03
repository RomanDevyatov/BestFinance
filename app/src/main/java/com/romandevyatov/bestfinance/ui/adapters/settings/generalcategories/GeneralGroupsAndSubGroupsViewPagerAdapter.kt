package com.romandevyatov.bestfinance.ui.adapters.settings.generalcategories

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.romandevyatov.bestfinance.ui.fragments.settings.general.expense.GeneralExpenseGroupsAndSubGroupsFragment
import com.romandevyatov.bestfinance.ui.fragments.settings.general.income.GeneralIncomeGroupsAndSubGroupsFragment

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
