package com.romandevyatov.bestfinance.ui.fragments.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.FragmentBottomMenuMoreBinding
import com.romandevyatov.bestfinance.ui.adapters.more.settings.SettingsCategoryAdapter
import com.romandevyatov.bestfinance.ui.adapters.more.settings.SettingsCategoryItem
import com.romandevyatov.bestfinance.ui.adapters.more.settings.SettingsSubCategoryAdapter
import com.romandevyatov.bestfinance.ui.adapters.more.settings.MoreSubCategoryItem
import com.romandevyatov.bestfinance.utils.BackStackLogger.logBackStack
import com.romandevyatov.bestfinance.utils.Constants
import com.romandevyatov.bestfinance.utils.Constants.MORE_FRAGMENT
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.MoreFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MoreFragment : Fragment() {

    private var _binding: FragmentBottomMenuMoreBinding? = null
    private val binding get() = _binding!!

    private lateinit var GROUPS_AND_SUB_GROUPS_CATEGORY: String
    private lateinit var WALLETS_CATEGORY: String
    private lateinit var RATES_CATEGORY: String

    private val moreFragmentViewModel: MoreFragmentViewModel by viewModels()

    companion object {
        lateinit var DEFAULT_CURRENCY: String
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomMenuMoreBinding.inflate(inflater, container, false)

        GROUPS_AND_SUB_GROUPS_CATEGORY = getString(R.string.groups_and_sub_groups)
        WALLETS_CATEGORY = getString(R.string.wallets)
        DEFAULT_CURRENCY = getString(R.string.default_currency)
        RATES_CATEGORY = getString(R.string.rates)

        logBackStack(findNavController())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callback = object : OnBackPressedCallback(
            true
        ) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack(R.id.home_fragment, false)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        setupRecyclerView()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun setupRecyclerView() {
        val onSubCategoryClickListener = object : SettingsSubCategoryAdapter.OnSubCategoryClickListener {

            override fun onSubCategoryClick(subCategory: MoreSubCategoryItem) {
                navigateToSubCategory(subCategory)
            }
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = SettingsCategoryAdapter(
                createCategoryRows(),
                onSubCategoryClickListener,
                moreFragmentViewModel.getDefaultCurrencyCode())
        }
    }

    private fun navigateToSubCategory(subCategory: MoreSubCategoryItem) {
        val navDirections: NavDirections = when (subCategory.name) {
            GROUPS_AND_SUB_GROUPS_CATEGORY ->
                MoreFragmentDirections.actionMoreFragmentToGroupsAndSubGroupsSettingsFragment()
            WALLETS_CATEGORY ->
                MoreFragmentDirections.actionMoreFragmentToArchivedWalletsFragment()
            DEFAULT_CURRENCY ->
                MoreFragmentDirections.actionMoreFragmentToSelectCurrencyFragment().setSource(MORE_FRAGMENT)
            RATES_CATEGORY ->
                MoreFragmentDirections.actionMoreFragmentToRatesFragment()

            // Add more cases as needed
            // "Export" -> SettingsFragmentDirections.actionCategoryPageFragmentToExportFragment()
            // "Import" -> SettingsFragmentDirections.actionCategoryPageFragmentToImportFragment()
            else -> return // Do nothing if it's not a recognized subcategory
        }
        findNavController().navigate(navDirections)
    }

    private fun createCategoryRows(): List<SettingsCategoryItem> {
        return listOf(
            SettingsCategoryItem(
                getString(R.string.categories),
                R.drawable.ic_category,
                listOf(
                    MoreSubCategoryItem(GROUPS_AND_SUB_GROUPS_CATEGORY, R.drawable.ic_group_and_subgroups),
                    MoreSubCategoryItem(WALLETS_CATEGORY, R.drawable.ic_wallet),
                    MoreSubCategoryItem(DEFAULT_CURRENCY, R.drawable.ic_money),
                    MoreSubCategoryItem(RATES_CATEGORY, R.drawable.ic_currency_exchange)
                )
            ),
//            CategoryItem(
//                "Report",
//                R.drawable.ic_report,
//                listOf(
//                    SubCategoryItem("Export", R.drawable.ic_export),
//                    SubCategoryItem("Import", R.drawable.ic_import)
//                )
//            )
        )
    }
}
