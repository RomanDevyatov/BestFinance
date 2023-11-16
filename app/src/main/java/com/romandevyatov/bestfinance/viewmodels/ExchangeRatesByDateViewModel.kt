package com.romandevyatov.bestfinance.viewmodels

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.BuildConfig
import com.romandevyatov.bestfinance.data.retrofit.repository.ExchangeRateRepository
import com.romandevyatov.bestfinance.utils.Constants
import com.romandevyatov.bestfinance.utils.sharedpreferences.Storage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ExchangeRatesByDateViewModel
@Inject
constructor(private val storage: Storage,
            private val exchangeRateRepository: ExchangeRateRepository
) : BaseViewModel(storage) {

    private val _exchangeRatesByDate = MutableLiveData<Map<String, Map<String, Double>>>()
    val exchangeRatesByDate: LiveData<Map<String, Map<String, Double>>>
        get() = _exchangeRatesByDate

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchExchangeRatesByDate(date: LocalDateTime) {
        val apiKey = BuildConfig.API_KEY
        val baseCurrencyCode = getDefaultCurrencyCode()

        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val date: String = date.format(formatter)
        val currencies: String = Constants.supportedCurrencies.joinToString(separator = ",") { it.code }

        viewModelScope.launch {
            val response = exchangeRateRepository.getExchangeRatesByDate(apiKey, date, baseCurrencyCode, currencies)

            if (response.isSuccessful) {
                _exchangeRatesByDate.value = response.body()?.data ?: emptyMap()
            } else {
                Log.d("tag", "getBaseRatesByDate error: ${response.code()}")
            }
        }
    }
}
