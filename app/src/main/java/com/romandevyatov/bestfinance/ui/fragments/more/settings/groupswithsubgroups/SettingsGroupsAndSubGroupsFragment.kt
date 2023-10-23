package com.romandevyatov.bestfinance.ui.fragments.more.settings.groupswithsubgroups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.FragmentSettingsGroupsAndSubGroupsBinding
import com.romandevyatov.bestfinance.ui.adapters.settings.groupswithsubgroups.GroupsAndSubGroupsViewPagerAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsGroupsAndSubGroupsFragment : Fragment() {

    private lateinit var groupTypeArray: Array<String>

    private var _binding: FragmentSettingsGroupsAndSubGroupsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        groupTypeArray = resources.getStringArray(R.array.group_type_array)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsGroupsAndSubGroupsBinding.inflate(inflater, container, false)

        setupViewPagerAndTabLayout()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOnBackPressedHandler()
    }

    private fun setOnBackPressedHandler() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.more_fragment)
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