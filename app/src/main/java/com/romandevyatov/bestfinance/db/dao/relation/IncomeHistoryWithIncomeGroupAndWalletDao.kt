package com.romandevyatov.bestfinance.db.dao.relation

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.romandevyatov.bestfinance.db.entities.relations.IncomeHistoryWithIncomeGroupAndWallet


@Dao
interface IncomeHistoryWithIncomeGroupAndWalletDao {

    @Transaction
    @Query("SELECT * FROM income_history")
    suspend fun getAllIncomeHistoryWithIncomeGroupAndWallet(): LiveData<List<IncomeHistoryWithIncomeGroupAndWallet>>

}