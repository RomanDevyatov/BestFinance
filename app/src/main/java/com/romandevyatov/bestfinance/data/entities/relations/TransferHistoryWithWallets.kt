package com.romandevyatov.bestfinance.data.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.romandevyatov.bestfinance.data.entities.TransferHistoryEntity
import com.romandevyatov.bestfinance.data.entities.WalletEntity

data class TransferHistoryWithWallets(

    @Embedded
    var transferHistoryEntity: TransferHistoryEntity,

    @Relation(
        entity = WalletEntity::class,
        parentColumn = "from_wallet_id",
        entityColumn = "id")
    var walletEntityFrom: WalletEntity,

    @Relation(
        entity = WalletEntity::class,
        parentColumn = "to_wallet_id",
        entityColumn = "id")
    var walletEntityTo: WalletEntity
)
