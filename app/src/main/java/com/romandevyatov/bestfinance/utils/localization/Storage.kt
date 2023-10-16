package com.romandevyatov.bestfinance.utils.localization

import android.content.Context
import android.content.SharedPreferences

class Storage(context: Context) {

    private val LANGUAGE_PREFS_NAME = "language_prefs"
    private var preferences: SharedPreferences = context.getSharedPreferences(LANGUAGE_PREFS_NAME, Context.MODE_PRIVATE)

    private val SELECTED_LANGUAGE = "Locale.Helper.Selected.Language"

    fun getPreferredLocale(): String {
        return preferences.getString(SELECTED_LANGUAGE, LocaleUtil.DEFAULT_PHONE_LANGUAGE)!!
    }

    fun setPreferredLocale(localeCode: String) {
        preferences.edit().putString(SELECTED_LANGUAGE, localeCode).apply()
    }
}