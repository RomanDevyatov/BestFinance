package com.romandevyatov.bestfinance.ui.fragments.more.settings

import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.ui.activity.MainActivity
import com.romandevyatov.bestfinance.utils.localization.LocaleUtil
import com.romandevyatov.bestfinance.utils.theme.ThemeHelper
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

        (requireActivity() as MainActivity).applySavedTheme()

        findPreference<SwitchPreferenceCompat>("darkOrLightTheme")?.setOnPreferenceChangeListener { _, newValue ->
            val isDarkModeEnabled = newValue as Boolean

            ThemeHelper.setDarkModeEnabled(requireContext(), isDarkModeEnabled)
            (requireActivity() as MainActivity).applySavedTheme()

            true
        }

        findPreference<ListPreference>("language")?.setOnPreferenceChangeListener { _, newValue ->
            val selectedLanguage = newValue.toString()

            (requireActivity() as MainActivity).updateAppLocale(selectedLanguage)

            Toast.makeText(requireContext(), R.string.add, Toast.LENGTH_SHORT).show()

            true
        }
    }
}
