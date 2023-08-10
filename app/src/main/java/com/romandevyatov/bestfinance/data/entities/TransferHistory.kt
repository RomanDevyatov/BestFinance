package com.romandevyatov.bestfinance.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "transfer_history",
    foreignKeys = [
        ForeignKey(
            entity = Wallet::class,
            parentColumns = ["id"],
            childColumns = ["from_wallet_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Wallet::class,
            parentColumns = ["id"],
            childColumns = ["to_wallet_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TransferHistory (

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long? = null,

    @ColumnInfo(name = "amount")
    val amount: Double,

    @ColumnInfo(name = "from_wallet_id")
    val fromWalletId: Long,

    @ColumnInfo(name = "to_wallet_id")
    val toWalletId: Long,

    @ColumnInfo(name = "date")
    val date: LocalDateTime? = null,

    @ColumnInfo(name = "comment")
    val comment: String? = null,

    @ColumnInfo(name = "archived_date")
    val archivedDate: LocalDateTime? = null,

    @ColumnInfo(name = "created_date")
    val createdDate: LocalDateTime? = null

)
