package com.romandevyatov.bestfinance.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "income_group")
data class IncomeGroup(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int?,

    @ColumnInfo(name = "name")
    val name: String?
)
