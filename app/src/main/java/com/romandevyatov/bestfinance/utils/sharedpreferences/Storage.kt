package com.romandevyatov.bestfinance.utils.sharedpreferences

import android.content.Context
import android.content.SharedPreferences
import com.romandevyatov.bestfinance.utils.localization.LocaleUtil
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Storage @Inject constructor(context: Context) {

    companion object {
        const val SELECTED_DEFAULT_CURRENCY_CODE: String = "DefaultCurrency.Helper.Selected.CurrencyCode"
    }
    private val SELECTED_LANGUAGE = "Locale.Helper.Selected.Language"
    private val IS_FIRST_LAUNCH = "isFirstLaunch"

    private val BESTFINANCE_APP_PREFRENCE = "BestFinanceAppPreferences"
    private var sharedPreferences: SharedPreferences = context.getSharedPreferences(BESTFINANCE_APP_PREFRENCE, Context.MODE_PRIVATE)

    private val defaultCurrencyCode: String = "USD"

    fun getPreferredLocale(): String {
        return sharedPreferences.getString(SELECTED_LANGUAGE, LocaleUtil.DEFAULT_PHONE_LANGUAGE)!!
    }

    fun setPreferredLocale(localeCode: String) {
        val editor = sharedPreferences.edit()
        editor.putString(SELECTED_LANGUAGE, localeCode)
        editor.apply()
    }

    fun getDefaultCurrencyCode(): String {
        return sharedPreferences.getString(SELECTED_DEFAULT_CURRENCY_CODE, defaultCurrencyCode)!!
    }

    fun setIsFirstLaunch(isFirstLaunch: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(IS_FIRST_LAUNCH, isFirstLaunch)
        editor.apply()
    }

    fun getIsFirstLaunch(): Boolean {
        return sharedPreferences.getBoolean(IS_FIRST_LAUNCH, true)
    }

    fun isFirstAppLaunch(): Boolean {
        val isFirstLaunch = sharedPreferences.getBoolean(IS_FIRST_LAUNCH, true)
        if (isFirstLaunch) {
            sharedPreferences.edit().putBoolean(IS_FIRST_LAUNCH, false).apply()
        }
        return isFirstLaunch
    }

    fun setPreferredDefaultCurrencyCode(currencyCode: String) {
        val editor = sharedPreferences.edit()
        editor.putString(SELECTED_DEFAULT_CURRENCY_CODE, currencyCode)
        editor.apply()
    }

    fun putString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getString(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    fun registerListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    fun unregisterListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
    }
}
