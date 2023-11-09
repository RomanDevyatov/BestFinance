package com.romandevyatov.bestfinance.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "base_currency_rates")
class BaseCurrencyRates (

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long? = null,

    @ColumnInfo(name = "pair_name")
    val pairName: String,

    @ColumnInfo(name = "value")
    val value: String

)
