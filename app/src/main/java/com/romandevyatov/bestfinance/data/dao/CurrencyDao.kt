package com.romandevyatov.bestfinance.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.romandevyatov.bestfinance.data.entities.Currency

@Dao
interface CurrencyDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(currencies: List<Currency>)

    @Query("SELECT * FROM currency")
    fun getAllCurrencies(): List<Currency>

    @Query("SELECT * FROM currency")
    fun getAllCurrenciesLiveData(): LiveData<List<Currency>>
}
