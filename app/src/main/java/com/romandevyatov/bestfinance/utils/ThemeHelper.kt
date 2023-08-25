package com.romandevyatov.bestfinance.utils

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

object ThemeHelper {
    private const val THEME_PREFS_NAME = "theme_prefs"
    private const val KEY_DARK_MODE = "dark_mode"

    fun isDarkModeEnabled(context: Context): Boolean {
        val sharedPrefs = context.getSharedPreferences(THEME_PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPrefs.getBoolean(KEY_DARK_MODE, false)
    }

    fun setDarkModeEnabled(context: Context, isEnabled: Boolean) {
        val sharedPrefs = context.getSharedPreferences(THEME_PREFS_NAME, Context.MODE_PRIVATE)

        val editor = sharedPrefs.edit()
        editor.putBoolean(KEY_DARK_MODE, isEnabled)
        editor.apply()
    }
}
