package com.romandevyatov.bestfinance.ui.fragments.more.groupswithsubgroups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.FragmentSettingsGroupsAndSubGroupsBinding
import com.romandevyatov.bestfinance.ui.adapters.more.settings.settingsgroupswithsubgroups.SettingsGroupsAndSubGroupsViewPagerAdapter
import com.romandevyatov.bestfinance.utils.BackStackLogger
import com.romandevyatov.bestfinance.viewmodels.shared.SharedInitialTabIndexViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsGroupsAndSubGroupsFragment : Fragment() {

    private lateinit var groupTypeArray: Array<String>

    private var _binding: FragmentSettingsGroupsAndSubGroupsBinding? = null
    private val binding get() = _binding!!

    private val sharedInitialTabIndexViewModel: SharedInitialTabIndexViewModel by activityViewModels()

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

        BackStackLogger.logBackStack(findNavController())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOnBackPressedHandler()
    }

    private fun setOnBackPressedHandler() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack(R.id.more_fragment, false)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun setupViewPagerAndTabLayout() {
        val viewPager = binding.groupViewPager
        val tabLayout = binding.groupTabLayout

        val adapter = SettingsGroupsAndSubGroupsViewPagerAdapter(this)
        viewPager.adapter = adapter

        val initialTabIndex = sharedInitialTabIndexViewModel.initialTabIndex ?: 0
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
