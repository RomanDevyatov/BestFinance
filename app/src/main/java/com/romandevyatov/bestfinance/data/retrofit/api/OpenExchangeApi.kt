package com.romandevyatov.bestfinance.data.retrofit.api

import com.romandevyatov.bestfinance.data.retrofit.response.ExchangeRatesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenExchangeApi {

    @GET("latest")
    suspend fun getExchangeRates(
        @Query("apikey") apiKey: String,
        @Query("base_currency") baseCurrencyCode: String,
        @Query("currencies") symbols: String
    ): Response<ExchangeRatesResponse>

}
