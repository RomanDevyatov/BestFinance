package com.romandevyatov.bestfinance.ui.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.romandevyatov.bestfinance.databinding.FragmentArchivedGroupBinding
import com.romandevyatov.bestfinance.ui.adapters.history.HistoryViewPagerAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArchivedGroupsFragment : Fragment() {

    private val groupTypeArray = arrayOf(
        "Income",
        "Expense"
    )

    private var _binding: FragmentArchivedGroupBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArchivedGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager = binding.grouppViewPager
        val tabLayout = binding.groupTabLayout

        val adapter = ArchivedGroupsViewPagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = groupTypeArray[position]
        }.attach()
    }

}