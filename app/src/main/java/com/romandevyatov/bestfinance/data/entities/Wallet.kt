package com.romandevyatov.bestfinance.data.entities

import androidx.room.*
import java.time.LocalDateTime

@Entity(
    tableName = "wallet",
    foreignKeys = [
        ForeignKey(
            entity = Currency::class,
            parentColumns = ["code"],
            childColumns = ["currency_code"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["name"], unique = true)]
)
data class Wallet(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long? = null,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "balance")
    val balance: Double,

    @ColumnInfo(name = "in")
    val input: Double = 0.0,

    @ColumnInfo(name = "out")
    val output: Double = 0.0,

    @ColumnInfo(name = "description")
    val description: String? = null,

    @ColumnInfo(name = "archived_date")
    val archivedDate: LocalDateTime? = null,

    @ColumnInfo(name = "currency_code")
    val currencyCode: String

)
