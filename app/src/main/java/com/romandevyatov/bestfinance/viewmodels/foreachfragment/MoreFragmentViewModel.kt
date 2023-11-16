package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import androidx.lifecycle.ViewModel
import com.romandevyatov.bestfinance.utils.sharedpreferences.Storage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MoreFragmentViewModel @Inject constructor(
    private val storage: Storage
) : ViewModel() {

    fun getDefaultCurrencyCode(): String {
        return storage.getDefaultCurrencyCode()
    }
}