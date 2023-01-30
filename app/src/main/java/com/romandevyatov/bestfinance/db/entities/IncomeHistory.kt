package com.romandevyatov.bestfinance.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date


@Entity(tableName = "income_history",
    foreignKeys = [
        ForeignKey(
            entity = IncomeGroup::class,
            parentColumns = ["id"], // IncomeGroup
            childColumns = ["income_group_id"], // IncomeHistory
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Wallet::class,
            parentColumns = ["id"], // Wallet
            childColumns = ["wallet_id"], // IncomeHistory
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class IncomeHistory(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long? = null,

    @ColumnInfo(name = "income_group_id")
    val incomeGroupId: Long,

    @ColumnInfo(name = "amount")
    val amount: Long,

    @ColumnInfo(name = "comment")
    val comment: String?,

    @ColumnInfo(name = "date_tmstmp")
    val date: Date,

    @ColumnInfo(name = "wallet_id")
    val walletId: Long

)
