package com.romandevyatov.bestfinance.ui.fragments.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.FragmentMenuSettingsBinding
import com.romandevyatov.bestfinance.ui.adapters.settings.CategoryAdapter
import com.romandevyatov.bestfinance.ui.adapters.settings.CategoryItem
import com.romandevyatov.bestfinance.ui.adapters.settings.SubCategoryAdapter
import com.romandevyatov.bestfinance.ui.adapters.settings.SubCategoryItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentMenuSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val onSubCategoryClickListener = object : SubCategoryAdapter.OnSubCategoryClickListener {

            override fun onSubCategoryClick(subCategory: SubCategoryItem) {
                Toast.makeText(requireContext(), "Clicked: ${subCategory.name}", Toast.LENGTH_SHORT).show()

                navigateToSubCategory(subCategory)
            }
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = CategoryAdapter(createCategoryData(), onSubCategoryClickListener)
        }
    }

    private fun navigateToSubCategory(subCategory: SubCategoryItem) {
        val action = when (subCategory.name) {
//            "Group"     -> SettingsFragmentDirections.actionNavigationSettingsToArchivedGroups()
//            "Subgroup"  -> SettingsFragmentDirections.actionSettingsFragmentToArchivedSubGroupsFragment()
            "Groups and Sub Groups"   -> SettingsFragmentDirections.actionSettingsFragmentToGeneralGroupsAndSubGroupsFragment()
            "Wallet"    -> SettingsFragmentDirections.actionSettingsFragmentToArchivedWalletsFragment()
//                    "Export"    -> SettingsFragmentDirections.actionCategoryPageFragmentToExportFragment()
//                    "Import"    -> SettingsFragmentDirections.actionCategoryPageFragmentToImportFragment()
            else -> return
        }
        findNavController().navigate(action)
    }

    private fun createCategoryData(): List<CategoryItem> {
        return listOf(
            CategoryItem(
                "Archived",
                R.drawable.ic_archive,
                listOf(
                    SubCategoryItem("Group", R.drawable.ic_group),
                    SubCategoryItem("Subgroup", R.drawable.ic_subgroup),
                    SubCategoryItem("Wallet", R.drawable.ic_wallet),
                    SubCategoryItem("General", R.drawable.ic_wallet)
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

//: PreferenceFragmentCompat() {
//
//    private var _binding: FragmentMenuSettingsBinding? = null
//    private val binding get() = _binding!!
//
//    private lateinit var sharedPreferences: SharedPreferences
//
//    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
//        setPreferencesFromResource(R.xml.preferences, rootKey)
//
//        val enableFeaturePreference = findPreference<CheckBoxPreference>("enable_feature")
//        enableFeaturePreference?.setOnPreferenceChangeListener { _, newValue ->
//            // Handle the preference change here (e.g., update app behavior based on the new value)
////            settingsViewModel.setFeatureEnabled(newValue as Boolean)
//            true // Return true to allow the preference change to be saved
//        }

        // Find the preferences by their keys
//        enableFeaturePreference = findPreference("enable_feature")!!

        // Set up listeners for preference changes
//        enableFeaturePreference.setOnPreferenceChangeListener { _, newValue ->
//            // Handle the preference change here (e.g., update app behavior based on the new value)
//            true // Return true to allow the preference change to be saved
//        }
//    }



//    override fun onResume() {
//        super.onResume()
//        // Update the preference summaries (if needed) when the fragment is resumed
//        updatePreferenceSummaries()
//    }
//
//    private fun updatePreferenceSummaries() {
//        // Set summary for preferences (if needed)
//        val enableFeature = sharedPreferences.getBoolean("enable_feature", false)
////        enableFeaturePreference.summary = "Feature is ${if (enableFeature) "enabled" else "disabled"}"
//    }

//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentMenuSettingsBinding.inflate(inflater, container, false)
////        val callback = object : OnBackPressedCallback(true) {
////            override fun handleOnBackPressed() { }
////        }
////        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        binding.textView2.text = "settings"
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}
