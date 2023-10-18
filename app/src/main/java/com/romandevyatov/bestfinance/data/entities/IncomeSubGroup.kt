package com.romandevyatov.bestfinance.data.entities

import androidx.room.*
import java.time.LocalDateTime

@Entity(tableName = "income_sub_group",
    foreignKeys = [
        ForeignKey(
            entity = IncomeGroup::class,
            parentColumns = ["id"],
            childColumns = ["income_group_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class IncomeSubGroup (

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long? = null,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "description")
    val description: String? = null,

    @ColumnInfo(name = "income_group_id")
    val incomeGroupId: Long,

    @ColumnInfo(name = "archived_date")
    val archivedDate: LocalDateTime? = null
)
