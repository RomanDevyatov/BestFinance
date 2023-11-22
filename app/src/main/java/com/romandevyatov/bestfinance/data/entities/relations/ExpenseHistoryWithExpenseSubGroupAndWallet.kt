package com.romandevyatov.bestfinance.data.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.romandevyatov.bestfinance.data.entities.ExpenseSubGroupEntity
import com.romandevyatov.bestfinance.data.entities.ExpenseHistoryEntity
import com.romandevyatov.bestfinance.data.entities.WalletEntity

data class ExpenseHistoryWithExpenseSubGroupAndWallet (

    @Embedded
    var expenseHistoryEntity: ExpenseHistoryEntity,

    @Relation(entity = ExpenseSubGroupEntity::class, parentColumn = "expense_sub_group_id", entityColumn = "id")
    var expenseSubGroupEntity: ExpenseSubGroupEntity?,

    @Relation(entity = WalletEntity::class, parentColumn = "wallet_id", entityColumn = "id")
    var walletEntity: WalletEntity?
)
