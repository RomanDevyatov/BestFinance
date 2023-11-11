package com.romandevyatov.bestfinance.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.OnConflictStrategy
import com.romandevyatov.bestfinance.data.entities.BaseCurrencyRate

@Dao
interface BaseCurrencyRatesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(currencies: List<BaseCurrencyRate>)

    @Query("SELECT * FROM base_currency_rate")
    fun getAll(): List<BaseCurrencyRate>

    @Query("SELECT * FROM base_currency_rate")
    fun getAllLiveData(): LiveData<List<BaseCurrencyRate>>

    @Query("DELETE FROM base_currency_rate")
    fun deleteAll()

    @Query("SELECT * FROM base_currency_rate WHERE pair_name = :pairName")
    fun getByPairNameLiveData(pairName: String): LiveData<BaseCurrencyRate?>

    @Query("SELECT * FROM base_currency_rate WHERE pair_name = :pairName")
    suspend fun getByPairName(pairName: String): BaseCurrencyRate?

//    @Query("SELECT * FROM base_currency_rate WHERE pair_name = :pairName")
//    fun getByPairName(pairName: String): BaseCurrencyRate?

}
