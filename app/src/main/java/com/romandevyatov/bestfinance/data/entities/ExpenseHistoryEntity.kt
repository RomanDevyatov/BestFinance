package com.romandevyatov.bestfinance.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "expense_history",
    foreignKeys = [
        ForeignKey(
            entity = ExpenseSubGroupEntity::class,
            parentColumns = ["id"], // ExpenseSubGroup
            childColumns = ["expense_sub_group_id"], // in ExpenseHistory
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = WalletEntity::class,
            parentColumns = ["id"], // Wallet
            childColumns = ["wallet_id"], // in ExpenseHistory
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ExpenseHistoryEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long? = null,

    @ColumnInfo(name = "expense_sub_group_id")
    val expenseSubGroupId: Long?,

    @ColumnInfo(name = "amount")
    val amount: Double,

    @ColumnInfo(name = "amount_base")
    val amountBase: Double,

    @ColumnInfo(name = "comment")
    val comment: String? = null,

    @ColumnInfo(name = "date")
    val date: LocalDateTime? = null,

    @ColumnInfo(name = "wallet_id")
    val walletId: Long,

    @ColumnInfo(name = "archived_date")
    val archivedDate: LocalDateTime? = null,

    @ColumnInfo(name = "created_date")
    val createdDate: LocalDateTime? = null
)
