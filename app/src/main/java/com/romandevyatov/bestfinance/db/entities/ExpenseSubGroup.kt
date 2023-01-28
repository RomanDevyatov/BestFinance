package com.romandevyatov.bestfinance.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey

@Entity(tableName = "expense_sub_group",
    foreignKeys = [
        ForeignKey(
            entity = ExpenseGroup::class,
            parentColumns = ["id"], // ExpenseGroup
            childColumns = ["expense_group_id"], // ExpenseSubGroup
            onDelete = CASCADE
        )
    ])
data class ExpenseSubGroup(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long? = null,

    @ColumnInfo(name = "name")
    val name: String?,

    @ColumnInfo(name = "expense_group_id")
    val expenseGroupId: Long
)
