package com.romandevyatov.bestfinance.viewmodels

import androidx.lifecycle.ViewModel
import com.romandevyatov.bestfinance.utils.localization.Storage
import javax.inject.Inject

open class BaseViewModel @Inject constructor(private val storage: Storage) : ViewModel() {

    fun getCurrencySymbol(): String {
        val currency = java.util.Currency.getInstance(storage.getDefaultCurrencyCode())
        return currency.symbol
    }

    fun getCurrencyCode(): String {
        return storage.getDefaultCurrencyCode()
    }
}