package com.romandevyatov.bestfinance.ui.fragments.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.FragmentHistoryBinding
import com.romandevyatov.bestfinance.ui.adapters.history.HistoryViewPagerAdapter
import com.romandevyatov.bestfinance.utils.BackStackLogger
import com.romandevyatov.bestfinance.viewmodels.shared.SharedInitialTabIndexViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : Fragment() {

    private lateinit var historyTypeArray: Array<String>

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val sharedInitialTabIndexViewModel: SharedInitialTabIndexViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        historyTypeArray = resources.getStringArray(R.array.history_type_array)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)

        BackStackLogger.logBackStack(findNavController())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOnBackPressedHandler()

        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout

        val adapter = HistoryViewPagerAdapter(this)
        viewPager.adapter = adapter

        val initialTabIndex = sharedInitialTabIndexViewModel.initialTabIndex ?: 0
        viewPager.setCurrentItem(initialTabIndex, false)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = historyTypeArray[position]
        }.attach()
    }

    private fun setOnBackPressedHandler() {
        val callback = object : OnBackPressedCallback(true) {

            override fun handleOnBackPressed() {
                findNavController().popBackStack(R.id.home_fragment, false)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

}
