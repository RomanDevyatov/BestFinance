package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.BaseCurrencyRate
import com.romandevyatov.bestfinance.data.repositories.BaseCurrencyRatesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RatesViewModel
@Inject
constructor(
    private val baseCurrencyRatesRepository: BaseCurrencyRatesRepository
) : ViewModel() {

    val allBaseCurrencyRate: LiveData<List<BaseCurrencyRate>> =
        baseCurrencyRatesRepository.getAllBaseCurrencyRateLiveData()

    fun insertAllBaseCurrencyRates(baseCurrencyRates: List<BaseCurrencyRate>) = viewModelScope.launch(Dispatchers.IO) {
        baseCurrencyRatesRepository.insertAllBaseCurrencyRate(baseCurrencyRates)
    }

    fun deleteAll() = viewModelScope.launch(Dispatchers.IO) {
        baseCurrencyRatesRepository.deleteAll()
    }

}