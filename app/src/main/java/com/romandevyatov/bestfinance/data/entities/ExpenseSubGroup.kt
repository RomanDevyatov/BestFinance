package com.romandevyatov.bestfinance.data.entities

import androidx.room.*
import java.time.LocalDateTime

@Entity(tableName = "expense_sub_group",
    foreignKeys = [
        ForeignKey(
            entity = ExpenseGroup::class,
            parentColumns = ["id"], // ExpenseGroup
            childColumns = ["expense_group_id"], // ExpenseSubGroup
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ExpenseSubGroup(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long? = null,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "description")
    val description: String? = null,

    @ColumnInfo(name = "expense_group_id")
    val expenseGroupId: Long,

    @ColumnInfo(name = "archived_date")
    val archivedDate: LocalDateTime? = null

)
