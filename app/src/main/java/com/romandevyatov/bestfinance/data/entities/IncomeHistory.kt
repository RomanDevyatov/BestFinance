package com.romandevyatov.bestfinance.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "income_history",
    foreignKeys = [
        ForeignKey(
            entity = IncomeSubGroup::class,
            parentColumns = ["id"], // IncomeGroup
            childColumns = ["income_sub_group_id"], // in IncomeHistory
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Wallet::class,
            parentColumns = ["id"], // Wallet
            childColumns = ["wallet_id"], // in IncomeHistory
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class IncomeHistory(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long? = null,

    @ColumnInfo(name = "income_sub_group_id")
    val incomeSubGroupId: Long?,

    @ColumnInfo(name = "amount")
    val amount: Double,

    @ColumnInfo(name = "comment")
    val comment: String?,

    @ColumnInfo(name = "date")
    val date: LocalDateTime? = null,

    @ColumnInfo(name = "wallet_id")
    val walletId: Long,

    @ColumnInfo(name = "archived_date")
    val archivedDate: LocalDateTime? = null,

    @ColumnInfo(name = "created_date")
    val createdDate: LocalDateTime? = null
)
