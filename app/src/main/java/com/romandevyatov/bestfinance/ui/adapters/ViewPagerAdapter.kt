package com.romandevyatov.bestfinance.ui.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.romandevyatov.bestfinance.ui.fragments.ExpenseHistoryFragment
import com.romandevyatov.bestfinance.ui.fragments.IncomeHistoryFragment
import com.romandevyatov.bestfinance.ui.fragments.TransferFragment


private const val NUM_TABS = 3


class ViewPagerAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return IncomeHistoryFragment()
            1 -> return ExpenseHistoryFragment()
        }
        return TransferFragment()
    }
}