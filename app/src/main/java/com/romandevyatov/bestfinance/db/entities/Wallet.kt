package com.romandevyatov.bestfinance.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallet")
data class Wallet(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long? = null,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "balance")
    val balance: Double,

    @ColumnInfo(name = "is_archived")
    val isArchived: Int = 0

)
