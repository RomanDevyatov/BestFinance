package com.romandevyatov.bestfinance.data.retrofit

import com.romandevyatov.bestfinance.data.retrofit.models.ExchangeRatesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenExchangeApi {

    @GET("latest.json")
    suspend fun getExchangeRates(
        @Query("app_id") apiKey: String,
        @Query("base") baseCurrencyCode: String
    ): Response<ExchangeRatesResponse>

}
