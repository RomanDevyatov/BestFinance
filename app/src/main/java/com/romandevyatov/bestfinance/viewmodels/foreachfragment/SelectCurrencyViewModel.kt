package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.romandevyatov.bestfinance.data.entities.Currency
import com.romandevyatov.bestfinance.data.entities.Wallet
import com.romandevyatov.bestfinance.data.repositories.CurrencyRepository
import com.romandevyatov.bestfinance.utils.localization.Storage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SelectCurrencyViewModel @Inject constructor(
    private val storage: Storage,
    private val currencyRepository: CurrencyRepository
) : ViewModel() {

    val allCurrenciesLiveData: LiveData<List<Currency>> = currencyRepository.getAllCurrenciesLiveData()

    fun setDefaultCurrencyCode(code: String) {
        storage.setPreferredDefaultCurrencyCode(code)
    }

}