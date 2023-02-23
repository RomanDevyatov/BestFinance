package com.romandevyatov.bestfinance.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.OffsetDateTime


@Entity(
    tableName = "expense_history",
    foreignKeys = [
        ForeignKey(
            entity = ExpenseSubGroup::class,
            parentColumns = ["id"], // ExpenseSubGroup
            childColumns = ["expense_sub_group_id"], // in ExpenseHistory
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Wallet::class,
            parentColumns = ["id"], // Wallet
            childColumns = ["wallet_id"], // in ExpenseHistory
            onDelete = ForeignKey.CASCADE
        )
    ]
)
class ExpenseHistory (

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long? = null,

    @ColumnInfo(name = "expense_sub_group_id")
    val expenseSubGroupId: Long,

    @ColumnInfo(name = "amount")
    val amount: Double,

    @ColumnInfo(name = "description")
    val description: String? = null,

    @ColumnInfo(name = "created_date")
    val createdDate: OffsetDateTime? = null,

    @ColumnInfo(name = "wallet_id")
    val walletId: Long,

    @ColumnInfo(name = "archived_date")
    val archivedDate: OffsetDateTime? = null

)
