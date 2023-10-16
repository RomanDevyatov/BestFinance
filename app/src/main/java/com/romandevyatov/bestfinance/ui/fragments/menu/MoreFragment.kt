package com.romandevyatov.bestfinance.ui.fragments.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.FragmentBottomMenuMoreBinding
import com.romandevyatov.bestfinance.ui.adapters.settings.CategoryAdapter
import com.romandevyatov.bestfinance.ui.adapters.settings.CategoryItem
import com.romandevyatov.bestfinance.ui.adapters.settings.SubCategoryAdapter
import com.romandevyatov.bestfinance.ui.adapters.settings.SubCategoryItem
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
        val onSubCategoryClickListener = object : SubCategoryAdapter.OnSubCategoryClickListener {

            override fun onSubCategoryClick(subCategory: SubCategoryItem) {
                navigateToSubCategory(subCategory)
            }
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = CategoryAdapter(createCategoryData(), onSubCategoryClickListener)
        }
    }

    private fun navigateToSubCategory(subCategory: SubCategoryItem) {
        when (subCategory.name) {
            GROUPS_AND_SUB_GROUPS_CATEGORY -> {
                findNavController().navigate(MoreFragmentDirections.actionMoreFragmentToGroupsAndSubGroupsSettingsFragment())
            }
            WALLETS_CATEGORY -> {
                findNavController().navigate(MoreFragmentDirections.actionMoreFragmentToArchivedWalletsFragment())
            }
            // Add more cases as needed
            // "Export" -> findNavController().navigate(SettingsFragmentDirections.actionCategoryPageFragmentToExportFragment())
            // "Import" -> findNavController().navigate(SettingsFragmentDirections.actionCategoryPageFragmentToImportFragment())
            else -> { }
        }
    }

    private fun createCategoryData(): List<CategoryItem> {
        return listOf(
            CategoryItem(
                getString(R.string.categories),
                R.drawable.ic_archive,
                listOf(
                    SubCategoryItem(GROUPS_AND_SUB_GROUPS_CATEGORY, R.drawable.ic_group_and_subgroups),
                    SubCategoryItem(WALLETS_CATEGORY, R.drawable.ic_wallet)
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
