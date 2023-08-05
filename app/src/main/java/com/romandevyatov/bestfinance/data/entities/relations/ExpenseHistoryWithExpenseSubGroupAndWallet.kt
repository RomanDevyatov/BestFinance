package com.romandevyatov.bestfinance.data.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.romandevyatov.bestfinance.data.entities.ExpenseSubGroup
import com.romandevyatov.bestfinance.data.entities.ExpenseHistory
import com.romandevyatov.bestfinance.data.entities.Wallet

data class ExpenseHistoryWithExpenseSubGroupAndWallet (
    
    @Embedded
    var expenseHistory: ExpenseHistory,

    @Relation(entity = ExpenseSubGroup::class, parentColumn = "expense_sub_group_id", entityColumn = "id")
    var expenseSubGroup: ExpenseSubGroup,

    @Relation(entity = Wallet::class, parentColumn = "wallet_id", entityColumn = "id")
    var wallet: Wallet

)
