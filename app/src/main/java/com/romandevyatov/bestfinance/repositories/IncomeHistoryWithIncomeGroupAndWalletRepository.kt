package com.romandevyatov.bestfinance.repositories

import androidx.lifecycle.LiveData
import com.romandevyatov.bestfinance.db.entities.relations.IncomeHistoryWithIncomeGroupAndWallet
import javax.inject.Inject


class IncomeHistoryWithIncomeGroupAndWalletRepository @Inject constructor(
    private val bestFinanceDao: BestFinanceDao
) {

    suspend fun getAllIncomeHistoryWithIncomeGroupAndWallet(): LiveData<List<IncomeHistoryWithIncomeGroupAndWallet>> {
        return bestFinanceDao.getAllIncomeHistoryWithIncomeGroupAndWallet()
    }


}