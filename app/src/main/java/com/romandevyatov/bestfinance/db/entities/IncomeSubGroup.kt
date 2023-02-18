package com.romandevyatov.bestfinance.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

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

    @ColumnInfo(name = "income_group_id")
    val incomeGroupId: Long,

    @ColumnInfo(name = "is_archived")
    val isArchived: Int = 0

)
