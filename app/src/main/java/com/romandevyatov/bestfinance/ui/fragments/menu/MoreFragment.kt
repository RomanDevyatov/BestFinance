package com.romandevyatov.bestfinance.ui.fragments.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.FragmentBottomMenuMoreBinding
import com.romandevyatov.bestfinance.ui.adapters.more.settings.SettingsCategoryAdapter
import com.romandevyatov.bestfinance.ui.adapters.more.settings.SettingsCategoryItem
import com.romandevyatov.bestfinance.ui.adapters.more.settings.SettingsSubCategoryAdapter
import com.romandevyatov.bestfinance.ui.adapters.more.settings.SettingsSubCategoryItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MoreFragment : Fragment() {

    private var _binding: FragmentBottomMenuMoreBinding? = null
    private val binding get() = _binding!!

    private lateinit var GROUPS_AND_SUB_GROUPS_CATEGORY: String
    private lateinit var WALLETS_CATEGORY: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomMenuMoreBinding.inflate(inflater, container, false)

        GROUPS_AND_SUB_GROUPS_CATEGORY = getString(R.string.groups_and_sub_groups)
        WALLETS_CATEGORY = getString(R.string.wallets)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callback = object : OnBackPressedCallback(
            true
        ) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.home_fragment)
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

            override fun onSubCategoryClick(subCategory: SettingsSubCategoryItem) {
                navigateToSubCategory(subCategory)
            }
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = SettingsCategoryAdapter(createCategoryData(), onSubCategoryClickListener)
        }
    }

    private fun navigateToSubCategory(subCategory: SettingsSubCategoryItem) {
        val navDirections: NavDirections = when (subCategory.name) {
            GROUPS_AND_SUB_GROUPS_CATEGORY ->
                MoreFragmentDirections.actionMoreFragmentToGroupsAndSubGroupsSettingsFragment()
            WALLETS_CATEGORY ->
                MoreFragmentDirections.actionMoreFragmentToArchivedWalletsFragment()
            // Add more cases as needed
            // "Export" -> SettingsFragmentDirections.actionCategoryPageFragmentToExportFragment()
            // "Import" -> SettingsFragmentDirections.actionCategoryPageFragmentToImportFragment()
            else -> return // Do nothing if it's not a recognized subcategory
        }
        findNavController().navigate(navDirections)
    }

    private fun createCategoryData(): List<SettingsCategoryItem> {
        return listOf(
            SettingsCategoryItem(
                getString(R.string.categories),
                R.drawable.ic_archive,
                listOf(
                    SettingsSubCategoryItem(GROUPS_AND_SUB_GROUPS_CATEGORY, R.drawable.ic_group_and_subgroups),
                    SettingsSubCategoryItem(WALLETS_CATEGORY, R.drawable.ic_wallet)
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
