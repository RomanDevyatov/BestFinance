package com.romandevyatov.bestfinance.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.romandevyatov.bestfinance.BuildConfig
import com.romandevyatov.bestfinance.data.retrofit.repository.ExchangeRateRepository
import com.romandevyatov.bestfinance.utils.Constants.supportedCurrencies
import com.romandevyatov.bestfinance.utils.sharedpreferences.Storage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExchangeRatesViewModel
@Inject
constructor(
    private val storage: Storage,
    private val exchangeRateRepository: ExchangeRateRepository
    ) : BaseViewModel(storage) {

    private val _exchangeRates = MutableLiveData<Map<String, Double>>()
    val exchangeRates: LiveData<Map<String, Double>>
        get() = _exchangeRates

    fun fetchExchangeRates() {
        val apiKey = BuildConfig.API_KEY
        val baseCurrencyCode = getDefaultCurrencyCode()
        val currencies: String = supportedCurrencies.joinToString(separator = ",") { it.code }

        viewModelScope.launch {
            val response = exchangeRateRepository.getExchangeRates(apiKey, baseCurrencyCode, currencies)

            if (response.isSuccessful) {
                _exchangeRates.value = response.body()?.data ?: emptyMap()
            } else {
                Log.d("tag", "getBaseRates error: ${response.code()}")
            }
        }
    }
}
