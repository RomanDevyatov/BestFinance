package com.romandevyatov.bestfinance.repositories

import androidx.lifecycle.LiveData
import com.romandevyatov.bestfinance.db.dao.IncomeHistoryDao
import com.romandevyatov.bestfinance.db.dao.relation.IncomeHistoryWithIncomeGroupAndWalletDao
import com.romandevyatov.bestfinance.db.entities.IncomeHistory
import com.romandevyatov.bestfinance.db.entities.relations.IncomeHistoryWithIncomeGroupAndWallet
import javax.inject.Inject


class IncomeHistoryRepository @Inject constructor(
    private val incomeHistoryDao: IncomeHistoryDao
) {

    fun getAllIncomeHistoryWithIncomeGroupAndWallet(): LiveData<List<IncomeHistoryWithIncomeGroupAndWallet>> {
        return incomeHistoryDao.getAllIncomeHistoryWithIncomeGroupAndWallet()
    }

    fun getAllIncomeHistory(): LiveData<List<IncomeHistory>> {
        return incomeHistoryDao.getAll()
    }

    suspend fun insertIncomeHistory(incomeHistory: IncomeHistory) {
        incomeHistoryDao.insert(incomeHistory)
    }


}