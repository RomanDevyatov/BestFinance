package com.romandevyatov.bestfinance.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.OffsetDateTime

@Entity(tableName = "transfer_history")
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

    @ColumnInfo(name = "comment")
    val comment: String? = null,

    @ColumnInfo(name = "archived_date")
    val archivedDate: OffsetDateTime? = null,

    @ColumnInfo(name = "created_date")
    val createdDate: OffsetDateTime? = null

)
