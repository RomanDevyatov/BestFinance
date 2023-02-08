package com.romandevyatov.bestfinance.db.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.romandevyatov.bestfinance.db.entities.IncomeGroup
import com.romandevyatov.bestfinance.db.entities.IncomeHistory
import com.romandevyatov.bestfinance.db.entities.Wallet


data class IncomeHistoryWithIncomeGroupAndWallet(

    @Embedded
    var incomeHistory: IncomeHistory,

    @Relation(entity = IncomeGroup::class, parentColumn = "income_group_id", entityColumn = "id")
    var incomeGroup: IncomeGroup,

    @Relation(entity = Wallet::class, parentColumn = "wallet_id", entityColumn = "id")
    var wallet: Wallet

)
