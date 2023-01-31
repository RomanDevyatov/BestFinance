package com.romandevyatov.bestfinance.repositories

import androidx.lifecycle.LiveData
import com.romandevyatov.bestfinance.db.dao.relation.IncomeHistoryWithIncomeGroupAndWalletDao
import com.romandevyatov.bestfinance.db.entities.relations.IncomeHistoryWithIncomeGroupAndWallet
import javax.inject.Inject


class IncomeHistoryWithIncomeGroupAndWalletRepository @Inject constructor(
    private val incomeHistoryWithIncomeGroupAndWalletDao: IncomeHistoryWithIncomeGroupAndWalletDao
) {

    fun getAllIncomeHistoryWithIncomeGroupAndWallet(): LiveData<List<IncomeHistoryWithIncomeGroupAndWallet>> {
        return incomeHistoryWithIncomeGroupAndWalletDao.getAllIncomeHistoryWithIncomeGroupAndWallet()
    }


}