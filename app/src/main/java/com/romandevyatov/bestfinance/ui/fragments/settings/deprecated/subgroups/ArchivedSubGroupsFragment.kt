package com.romandevyatov.bestfinance.ui.fragments.settings.deprecated.subgroups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.romandevyatov.bestfinance.databinding.FragmentArchivedSubGroupBinding
import com.romandevyatov.bestfinance.ui.adapters.settings.deprecated.subgroup.ArchivedSubGroupsViewPagerAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArchivedSubGroupsFragment: Fragment() {

    private val subGroupTypeArray = arrayOf(
        "Income",
        "Expense"
    )

    private var _binding: FragmentArchivedSubGroupBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArchivedSubGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager = binding.subGroupViewPager
        val tabLayout = binding.subGroupTabLayout

        val adapter = ArchivedSubGroupsViewPagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = subGroupTypeArray[position]
        }.attach()
    }

}