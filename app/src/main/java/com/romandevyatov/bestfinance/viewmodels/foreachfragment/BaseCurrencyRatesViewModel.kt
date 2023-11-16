package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.BaseCurrencyRateEntity
import com.romandevyatov.bestfinance.data.repositories.BaseCurrencyRatesRepository
import com.romandevyatov.bestfinance.ui.adapters.rates.CurrencyExchangeRateItem
import com.romandevyatov.bestfinance.utils.sharedpreferences.Storage
import com.romandevyatov.bestfinance.viewmodels.BaseViewModel
import com.romandevyatov.bestfinance.viewmodels.models.BaseCurrencyRate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BaseCurrencyRatesViewModel
@Inject
constructor(
    storage: Storage,
    private val baseCurrencyRatesRepository: BaseCurrencyRatesRepository
) : BaseViewModel(storage) {

    val allBaseCurrencyRate: LiveData<List<BaseCurrencyRate>> =
        baseCurrencyRatesRepository.getAllBaseCurrencyRateLiveData()

    fun insertAllBaseCurrencyRates(baseCurrencyRateEntities: List<BaseCurrencyRateEntity>) = viewModelScope.launch(Dispatchers.IO) {
        baseCurrencyRatesRepository.insertAllBaseCurrencyRate(baseCurrencyRateEntities)
    }

    fun deleteAll() = viewModelScope.launch(Dispatchers.IO) {
        baseCurrencyRatesRepository.deleteAll()
    }

    fun deleteAllAndInsert(baseCurrencyRateEntities: List<BaseCurrencyRateEntity>) = viewModelScope.launch(Dispatchers.IO) {
        baseCurrencyRatesRepository.deleteAll()
        baseCurrencyRatesRepository.insertAllBaseCurrencyRate(baseCurrencyRateEntities)
    }

    fun mapToBaseCurrencyExchangeRates(exchangeRates: Map<String, Double>?): List<BaseCurrencyRateEntity> {
        val currencyExchangeRates = mutableListOf<BaseCurrencyRateEntity>()

        val defaultCurrencySymbol = getDefaultCurrencyCode()

        exchangeRates?.forEach { (currencyCode, exchangeRate) ->
            val currencyExchangeRate = BaseCurrencyRateEntity(
                pairName = defaultCurrencySymbol + currencyCode,
                value = exchangeRate
            )
            currencyExchangeRates.add(currencyExchangeRate)
        }

        return currencyExchangeRates
    }

    fun mapToCurrencyExchangeRateItemList(exchangeRates: List<BaseCurrencyRate>?): MutableList<CurrencyExchangeRateItem> {
        val currencyExchangeRateItems = mutableListOf<CurrencyExchangeRateItem>()

        exchangeRates?.forEach { it ->
            val currencyExchangeRateItem = CurrencyExchangeRateItem(
                currencyCode = it.pairName,
                rate = it.value
            )
            currencyExchangeRateItems.add(currencyExchangeRateItem)
        }

        return currencyExchangeRateItems
    }
}
