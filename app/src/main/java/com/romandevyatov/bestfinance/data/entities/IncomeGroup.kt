package com.romandevyatov.bestfinance.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime


@Entity(
    tableName = "income_group",
    indices = [Index(value = ["name"], unique = true)]
)
data class IncomeGroup(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long? = null,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "is_passive")
    val isPassive: Boolean,

    @ColumnInfo(name = "description")
    val description: String? = null,

    @ColumnInfo(name = "archived_date")
    val archivedDate: LocalDateTime? = null
)
