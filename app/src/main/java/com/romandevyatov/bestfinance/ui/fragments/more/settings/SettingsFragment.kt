package com.romandevyatov.bestfinance.ui.fragments.more.settings

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.romandevyatov.bestfinance.R
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.more_fragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

        // Access preferences and listen for changes
//        val preferenceManager = preferenceManager
//        val sharedPreferences = preferenceManager.sharedPreferences
//        sharedPreferences?.registerOnSharedPreferenceChangeListener(this)

        val darkModePreference = findPreference<SwitchPreferenceCompat>("darkOrLightTheme")
        darkModePreference!!.setDefaultValue(false)
        darkModePreference?.setOnPreferenceChangeListener { _, newValue ->
            val isDarkModeEnabled = newValue as Boolean
            updateTheme(isDarkModeEnabled)
            true
        }
    }

    private fun updateTheme(isDarkModeEnabled: Boolean) {
        val nightMode = if (isDarkModeEnabled) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }

    //    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
//        when (key) {
//            "night_or_light_theme" -> {
//                val isNightModeEnabled = sharedPreferences.getBoolean(key, false)
//                setAppTheme(isNightModeEnabled)
//            }
//            // Add more cases for other preferences if needed
//        }
//    }

//    private fun setAppTheme(isDarkModeEnabled: Boolean) {
//        val themeId = if (isDarkModeEnabled) R.style.Theme_BestFinance else R.style.Theme_BestFinance
//        activity?.setTheme(themeId)
        // You can also apply the theme to the entire application using AppCompatDelegate.setDefaultNightMode()
        // AppCompatDelegate.setDefaultNightMode(if (isDarkModeEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
//    }
}
