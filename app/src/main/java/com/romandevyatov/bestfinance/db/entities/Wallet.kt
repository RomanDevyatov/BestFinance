package com.romandevyatov.bestfinance.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.OffsetDateTime


@Entity(tableName = "wallet")
data class Wallet(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long? = null,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "balance")
    val balance: Double,

    @ColumnInfo(name = "in")
    val input: Double,

    @ColumnInfo(name = "out")
    val output: Double,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "archived_date")
    val archivedDate: OffsetDateTime? = null

)
