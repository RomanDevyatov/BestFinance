package com.romandevyatov.bestfinance.data.retrofit.api

import com.romandevyatov.bestfinance.data.retrofit.response.ExchangeRatesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenExchangeApi {

    @GET("latest.json")
    suspend fun getExchangeRates(
        @Query("app_id") apiKey: String,
        @Query("base") baseCurrencyCode: String,
        @Query("symbols") symbols: String = "RUB,USD,EUR",
        @Query("prettyprint") prettyprint: Boolean = false,
        @Query("show_alternative") show_alternative: Boolean = false
    ): Response<ExchangeRatesResponse>

}
