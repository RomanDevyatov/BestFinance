package com.romandevyatov.bestfinance.data.repositories

import androidx.lifecycle.LiveData
import com.romandevyatov.bestfinance.data.dao.CurrencyDao
import com.romandevyatov.bestfinance.data.entities.CurrencyEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyRepository
@Inject
constructor(private val currencyDao: CurrencyDao) {

    fun getAllCurrencies(): List<CurrencyEntity> {
        return currencyDao.getAllCurrencies()
    }

    suspend fun insertAllCurrencies(currencies: List<CurrencyEntity>) {
        return currencyDao.insertAll(currencies)
    }

    fun getAllCurrenciesLiveData(): LiveData<List<CurrencyEntity>> {
        return currencyDao.getAllCurrenciesLiveData()
    }

}
