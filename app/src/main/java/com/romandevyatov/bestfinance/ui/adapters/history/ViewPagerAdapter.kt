package com.romandevyatov.bestfinance.ui.adapters.history

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.romandevyatov.bestfinance.ui.fragments.history.ExpenseHistoryFragment
import com.romandevyatov.bestfinance.ui.fragments.history.IncomeHistoryFragment
import com.romandevyatov.bestfinance.ui.fragments.history.TransferHistoryFragment

class ViewPagerAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {

    private val NUM_TABS = 3

    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return IncomeHistoryFragment()
            1 -> return ExpenseHistoryFragment()
        }
        return TransferHistoryFragment()
    }
}
