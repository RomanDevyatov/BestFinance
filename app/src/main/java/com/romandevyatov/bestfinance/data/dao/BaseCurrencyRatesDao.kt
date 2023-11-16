package com.romandevyatov.bestfinance.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.OnConflictStrategy
import com.romandevyatov.bestfinance.data.entities.BaseCurrencyRateEntity

@Dao
interface BaseCurrencyRatesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(currencies: List<BaseCurrencyRateEntity>)

    @Query("SELECT * FROM base_currency_rate")
    fun getAll(): List<BaseCurrencyRateEntity>

    @Query("SELECT * FROM base_currency_rate")
    fun getAllLiveData(): LiveData<List<BaseCurrencyRateEntity>>

    @Query("DELETE FROM base_currency_rate")
    fun deleteAll()

    @Query("SELECT * FROM base_currency_rate WHERE pair_name = :pairName")
    fun getByPairNameLiveData(pairName: String): LiveData<BaseCurrencyRateEntity?>

    @Query("SELECT * FROM base_currency_rate WHERE pair_name = :pairName")
    suspend fun getByPairName(pairName: String): BaseCurrencyRateEntity?

//    @Query("SELECT * FROM base_currency_rate WHERE pair_name = :pairName")
//    fun getByPairName(pairName: String): BaseCurrencyRate?

}
