package com.romandevyatov.bestfinance.ui.fragments.menu

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.CheckBoxPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.FragmentMenuSettingsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

    private var _binding: FragmentMenuSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val enableFeaturePreference = findPreference<CheckBoxPreference>("enable_feature")
        enableFeaturePreference?.setOnPreferenceChangeListener { _, newValue ->
            // Handle the preference change here (e.g., update app behavior based on the new value)
//            settingsViewModel.setFeatureEnabled(newValue as Boolean)
            true // Return true to allow the preference change to be saved
        }

        // Find the preferences by their keys
//        enableFeaturePreference = findPreference("enable_feature")!!

        // Set up listeners for preference changes
//        enableFeaturePreference.setOnPreferenceChangeListener { _, newValue ->
//            // Handle the preference change here (e.g., update app behavior based on the new value)
//            true // Return true to allow the preference change to be saved
//        }
    }



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

}
