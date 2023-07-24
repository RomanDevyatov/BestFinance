package com.romandevyatov.bestfinance.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "wallet",
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
    val archivedDate: LocalDateTime? = null

)
