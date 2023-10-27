package com.romandevyatov.bestfinance.data.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.romandevyatov.bestfinance.data.entities.TransferHistory
import com.romandevyatov.bestfinance.data.entities.Wallet

data class TransferHistoryWithWallets(

    @Embedded
    var transferHistory: TransferHistory,

    @Relation(
        entity = Wallet::class,
        parentColumn = "from_wallet_id",
        entityColumn = "id")
    var walletFrom: Wallet,

    @Relation(
        entity = Wallet::class,
        parentColumn = "to_wallet_id",
        entityColumn = "id")
    var walletTo: Wallet
)
