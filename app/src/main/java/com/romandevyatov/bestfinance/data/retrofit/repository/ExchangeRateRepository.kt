package com.romandevyatov.bestfinance.data.retrofit.repository

import com.romandevyatov.bestfinance.data.retrofit.api.OpenExchangeApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExchangeRateRepository @Inject constructor(private val openExchangeApi: OpenExchangeApi) {

    suspend fun getExchangeRates(apiKey: String, base: String, currencies: String) = openExchangeApi.getExchangeRates(apiKey, base, currencies)

    suspend fun getExchangeRatesByDate(apiKey: String, date: String, base: String, currencies: String) = openExchangeApi.getExchangeRatesByDate(apiKey, date, base, currencies)

}
