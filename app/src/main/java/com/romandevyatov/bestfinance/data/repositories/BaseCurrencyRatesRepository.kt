package com.romandevyatov.bestfinance.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.romandevyatov.bestfinance.data.dao.BaseCurrencyRatesDao
import com.romandevyatov.bestfinance.data.entities.BaseCurrencyRateEntity
import com.romandevyatov.bestfinance.viewmodels.models.BaseCurrencyRate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BaseCurrencyRatesRepository
@Inject
constructor(private val baseCurrencyRatesDao: BaseCurrencyRatesDao) {

    suspend fun insertAllBaseCurrencyRate(currencies: List<BaseCurrencyRateEntity>) {
        baseCurrencyRatesDao.insertAll(currencies)
    }

    fun getAllBaseCurrencyRateLiveData(): LiveData<List<BaseCurrencyRate>> {
        return Transformations.map(baseCurrencyRatesDao.getAllLiveData()) { entityList ->
            entityList.map { entity ->
                BaseCurrencyRate(
                    pairName = entity.pairName,
                    value = entity.value
                )
            }
        }
    }

    fun deleteAll() {
        baseCurrencyRatesDao.deleteAll()
    }

    suspend fun getBaseCurrencyRateByPairName(pairName: String): BaseCurrencyRateEntity? {
        return baseCurrencyRatesDao.getByPairName(pairName)
    }


    fun getBaseCurrencyRateByPairNameLiveData(pairName: String): LiveData<BaseCurrencyRateEntity?> {
        return baseCurrencyRatesDao.getByPairNameLiveData(pairName)
    }

}
