package com.romandevyatov.bestfinance.data.repositories

import androidx.lifecycle.LiveData
import com.romandevyatov.bestfinance.data.dao.BaseCurrencyRatesDao
import com.romandevyatov.bestfinance.data.entities.BaseCurrencyRate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BaseCurrencyRatesRepository
@Inject
constructor(private val baseCurrencyRatesDao: BaseCurrencyRatesDao) {

    suspend fun insertAllBaseCurrencyRate(currencies: List<BaseCurrencyRate>) {
        baseCurrencyRatesDao.insertAll(currencies)
    }

    fun getAllBaseCurrencyRateLiveData(): LiveData<List<BaseCurrencyRate>> {
        return baseCurrencyRatesDao.getAllLiveData()
    }

    fun deleteAll() {
        baseCurrencyRatesDao.deleteAll()
    }

    suspend fun getBaseCurrencyRateByPairName(pairName: String): BaseCurrencyRate? {
        return baseCurrencyRatesDao.getByPairName(pairName)
    }

    fun getBaseCurrencyRateByPairNameLiveData(pairName: String): LiveData<BaseCurrencyRate?> {
        return baseCurrencyRatesDao.getByPairNameLiveData(pairName)
    }


}