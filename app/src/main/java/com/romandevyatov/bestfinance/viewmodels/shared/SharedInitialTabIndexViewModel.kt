package com.romandevyatov.bestfinance.viewmodels.shared

import androidx.lifecycle.ViewModel
import javax.inject.Inject

class SharedInitialTabIndexViewModel @Inject constructor(): ViewModel() {

    var initialTabIndex: Int? = null
        private set

    fun set(tabIndex: Int?) {
        initialTabIndex = tabIndex
    }
}