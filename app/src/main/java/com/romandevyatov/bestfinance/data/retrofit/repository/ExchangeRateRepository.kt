package com.romandevyatov.bestfinance.data.retrofit.repository

import com.romandevyatov.bestfinance.data.retrofit.OpenExchangeApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExchangeRateRepository @Inject constructor(private val openExchangeApi: OpenExchangeApi) {

    suspend fun getExchangeRates(apiKey: String, defaultCurrencyCode: String) = openExchangeApi.getExchangeRates(apiKey, defaultCurrencyCode)

}
