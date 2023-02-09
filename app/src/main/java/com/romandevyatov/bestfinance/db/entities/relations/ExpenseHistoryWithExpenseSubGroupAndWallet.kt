package com.romandevyatov.bestfinance.db.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.romandevyatov.bestfinance.db.entities.ExpenseSubGroup
import com.romandevyatov.bestfinance.db.entities.ExpenseGroup
import com.romandevyatov.bestfinance.db.entities.ExpenseHistory
import com.romandevyatov.bestfinance.db.entities.Wallet

data class ExpenseHistoryWithExpenseSubGroupAndWallet (
    
    @Embedded
    var expenseHistory: ExpenseHistory,

    @Relation(entity = ExpenseSubGroup::class, parentColumn = "expense_sub_group_id", entityColumn = "id")
    var expenseSubGroup: ExpenseSubGroup,

    @Relation(entity = Wallet::class, parentColumn = "wallet_id", entityColumn = "id")
    var wallet: Wallet
)
