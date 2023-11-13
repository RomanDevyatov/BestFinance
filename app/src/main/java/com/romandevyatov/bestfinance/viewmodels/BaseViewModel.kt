package com.romandevyatov.bestfinance.viewmodels

import androidx.lifecycle.ViewModel
import com.romandevyatov.bestfinance.utils.sharedpreferences.Storage
import javax.inject.Inject

open class BaseViewModel @Inject constructor(private val storage: Storage) : ViewModel() {

    fun getDefaultCurrencySymbol(): String {
        return getCurrencySymbolByCode(getDefaultCurrencyCode())
    }

    fun getDefaultCurrencyCode(): String {
        return storage.getDefaultCurrencyCode()
    }

    fun getCurrencySymbolByCode(code: String?): String {
        val currency = java.util.Currency.getInstance(code)
        return currency.symbol
    }

    fun setPreferredDefaultCurrencyCode(code: String) {
        storage.setPreferredDefaultCurrencyCode(code)
    }


}