package com.romandevyatov.bestfinance.db.entities

import androidx.room.*
import androidx.room.ForeignKey.Companion.CASCADE
import java.time.OffsetDateTime


@Entity(tableName = "expense_sub_group",
    foreignKeys = [
        ForeignKey(
            entity = ExpenseGroup::class,
            parentColumns = ["id"], // ExpenseGroup
            childColumns = ["expense_group_id"], // ExpenseSubGroup
            onDelete = CASCADE
        )
    ],
    indices = [Index(value = ["id", "name"], unique = true)]
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
    val archivedDate: OffsetDateTime? = null

)
