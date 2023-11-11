package com.romandevyatov.bestfinance.data.repositories

import androidx.lifecycle.LiveData
import com.romandevyatov.bestfinance.data.dao.BaseCurrencyRatesDao
import com.romandevyatov.bestfinance.data.entities.BaseCurrencyRate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BaseCurrencyRatesRepository
@Inject
constructor(private val currencyDao: BaseCurrencyRatesDao) {

    suspend fun insertAllBaseCurrencyRate(currencies: List<BaseCurrencyRate>) {
        currencyDao.insertAll(currencies)
    }

    fun getAllBaseCurrencyRate(): List<BaseCurrencyRate> {
        return currencyDao.getAll()
    }

    fun getAllBaseCurrencyRateLiveData(): LiveData<List<BaseCurrencyRate>> {
        return currencyDao.getAllLiveData()
    }

    fun deleteAll() {
        currencyDao.deleteAll()
    }

}