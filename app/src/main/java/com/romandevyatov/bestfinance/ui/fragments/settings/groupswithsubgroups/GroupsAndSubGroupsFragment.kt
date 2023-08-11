package com.romandevyatov.bestfinance.ui.fragments.settings.groupswithsubgroups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.SettingsFragmentGroupsAndSubGroupsBinding
import com.romandevyatov.bestfinance.ui.adapters.settings.groupswithsubgroups.GroupsAndSubGroupsViewPagerAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GroupsAndSubGroupsFragment : Fragment() {

    private val groupTypeArray = arrayOf(
        "Income",
        "Expense"
    )

    private var _binding: SettingsFragmentGroupsAndSubGroupsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SettingsFragmentGroupsAndSubGroupsBinding.inflate(inflater, container, false)

        setOnBackPressedHandler()
        setupViewPagerAndTabLayout()

        return binding.root
    }

    private fun setOnBackPressedHandler() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_groups_and_sub_groups_settings_fragment_to_settings_fragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun setupViewPagerAndTabLayout() {
        val viewPager = binding.groupViewPager
        val tabLayout = binding.groupTabLayout

        val adapter = GroupsAndSubGroupsViewPagerAdapter(this)
        viewPager.adapter = adapter

        val initialTabIndex = arguments?.getInt("initialTabIndex") ?: 0
        viewPager.setCurrentItem(initialTabIndex, false)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = groupTypeArray[position]
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
