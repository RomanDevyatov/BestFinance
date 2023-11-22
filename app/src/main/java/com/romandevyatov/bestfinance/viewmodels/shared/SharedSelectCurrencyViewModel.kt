package com.romandevyatov.bestfinance.viewmodels.shared

import androidx.lifecycle.ViewModel
import javax.inject.Inject

class SharedSelectCurrencyViewModel @Inject constructor(): ViewModel() {

    var currencyCode: String? = null
        private set

    fun set(transferForm: String?) {
        currencyCode = transferForm
    }
}