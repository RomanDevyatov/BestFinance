package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.BaseCurrencyRate
import com.romandevyatov.bestfinance.data.repositories.BaseCurrencyRatesRepository
import com.romandevyatov.bestfinance.utils.sharedpreferences.Storage
import com.romandevyatov.bestfinance.viewmodels.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RatesViewModel
@Inject
constructor(
    storage: Storage,
    private val baseCurrencyRatesRepository: BaseCurrencyRatesRepository
) : BaseViewModel(storage) {

    val allBaseCurrencyRate: LiveData<List<BaseCurrencyRate>> =
        baseCurrencyRatesRepository.getAllBaseCurrencyRateLiveData()

    fun insertAllBaseCurrencyRates(baseCurrencyRates: List<BaseCurrencyRate>) = viewModelScope.launch(Dispatchers.IO) {
        baseCurrencyRatesRepository.insertAllBaseCurrencyRate(baseCurrencyRates)
    }

    fun deleteAll() = viewModelScope.launch(Dispatchers.IO) {
        baseCurrencyRatesRepository.deleteAll()
    }

    fun mapToBaseCurrencyExchangeRates(exchangeRates: Map<String, Double>?): MutableList<BaseCurrencyRate> {
        val currencyExchangeRates = mutableListOf<BaseCurrencyRate>()

        val defaultCurrencySymbol = getDefaultCurrencyCode()

        exchangeRates?.forEach { (currencyCode, exchangeRate) ->
            val currencyExchangeRate = BaseCurrencyRate(
                pairName = defaultCurrencySymbol + currencyCode,
                value = exchangeRate
            )
            currencyExchangeRates.add(currencyExchangeRate)
        }

        return currencyExchangeRates
    }

}