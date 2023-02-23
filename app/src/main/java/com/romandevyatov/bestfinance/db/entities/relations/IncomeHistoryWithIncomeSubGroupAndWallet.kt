package com.romandevyatov.bestfinance.db.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.romandevyatov.bestfinance.db.entities.IncomeHistory
import com.romandevyatov.bestfinance.db.entities.IncomeSubGroup
import com.romandevyatov.bestfinance.db.entities.Wallet

data class IncomeHistoryWithIncomeSubGroupAndWallet (

    @Embedded
    var incomeHistory: IncomeHistory,

    @Relation(
        entity = IncomeSubGroup::class,
        parentColumn = "income_sub_group_id",
        entityColumn = "id")
    var incomeSubGroup: IncomeSubGroup,

    @Relation(
        entity = Wallet::class,
        parentColumn = "wallet_id",
        entityColumn = "id")
    var wallet: Wallet

)
