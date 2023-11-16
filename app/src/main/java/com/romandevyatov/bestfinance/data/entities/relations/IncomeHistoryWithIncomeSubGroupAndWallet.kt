package com.romandevyatov.bestfinance.data.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.romandevyatov.bestfinance.data.entities.IncomeHistoryEntity
import com.romandevyatov.bestfinance.data.entities.IncomeSubGroup
import com.romandevyatov.bestfinance.data.entities.WalletEntity

data class IncomeHistoryWithIncomeSubGroupAndWallet (

    @Embedded
    var incomeHistoryEntity: IncomeHistoryEntity,

    @Relation(
        entity = IncomeSubGroup::class,
        parentColumn = "income_sub_group_id",
        entityColumn = "id")
    var incomeSubGroup: IncomeSubGroup?,

    @Relation(
        entity = WalletEntity::class,
        parentColumn = "wallet_id",
        entityColumn = "id")
    var walletEntity: WalletEntity?
)
