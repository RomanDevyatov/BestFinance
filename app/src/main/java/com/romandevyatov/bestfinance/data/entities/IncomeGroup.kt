package com.romandevyatov.bestfinance.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.romandevyatov.bestfinance.data.entities.base.Group
import java.time.LocalDateTime

@Entity(
    tableName = "income_group",
    indices = [Index(value = ["name"], unique = true)]
)
data class IncomeGroup(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    override val id: Long? = null,

    @ColumnInfo(name = "name")
    override val name: String,

    @ColumnInfo(name = "is_passive")
    val isPassive: Boolean,

    @ColumnInfo(name = "description")
    override val description: String? = null,

    @ColumnInfo(name = "archived_date")
    override val archivedDate: LocalDateTime? = null
) : Group
