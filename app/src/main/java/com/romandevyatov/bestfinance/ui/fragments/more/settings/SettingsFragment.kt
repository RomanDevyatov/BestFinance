package com.romandevyatov.bestfinance.ui.fragments.more.settings

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceFragmentCompat
import com.romandevyatov.bestfinance.R
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat(), OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.more_fragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)


        // Access preferences and listen for changes
        val preferenceManager = preferenceManager
        val sharedPreferences = preferenceManager.sharedPreferences
        sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Unregister the preference change listener
        preferenceManager.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            "pennyCount" -> {
                val pennyCount = sharedPreferences?.getInt(key, 0)
                // React to penny count preference change
            }
            "fastAI" -> {
                val fastAIEnabled = sharedPreferences?.getBoolean(key, false)
                // React to fast AI preference change
            }
            // Add more cases for other preferences if needed
        }
    }
}
