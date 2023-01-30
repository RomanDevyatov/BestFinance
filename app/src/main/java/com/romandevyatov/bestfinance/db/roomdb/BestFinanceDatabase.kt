package com.romandevyatov.bestfinance.db.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.romandevyatov.bestfinance.db.dao.ExpenseGroupDao
import com.romandevyatov.bestfinance.db.dao.IncomeGroupDao
import com.romandevyatov.bestfinance.db.dao.IncomeHistoryDao
import com.romandevyatov.bestfinance.db.dao.WalletDao
import com.romandevyatov.bestfinance.db.entities.*
import com.romandevyatov.bestfinance.db.roomdb.converters.Converters


@Database(
    entities = [
        IncomeGroup::class,
        ExpenseGroup::class,
        ExpenseSubGroup::class,
        Wallet::class,
        IncomeHistory::class],
    version = 4,
    exportSchema = true)
@TypeConverters(Converters::class)
abstract class BestFinanceDatabase : RoomDatabase() {

    abstract fun getIncomeGroupDao(): IncomeGroupDao

    abstract fun getExpenseGroupDao(): ExpenseGroupDao

    abstract fun getWalletDao(): WalletDao

    abstract fun getIncomeHistoryDao(): IncomeHistoryDao

}
