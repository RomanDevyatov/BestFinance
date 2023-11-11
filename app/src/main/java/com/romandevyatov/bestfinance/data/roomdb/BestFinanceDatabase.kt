package com.romandevyatov.bestfinance.data.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.romandevyatov.bestfinance.data.dao.*
import com.romandevyatov.bestfinance.data.entities.*
import com.romandevyatov.bestfinance.data.roomdb.converters.LocalDateTimeRoomTypeConverter

@Database(
    entities = [
        IncomeGroup::class,
        ExpenseGroupEntity::class,
        IncomeSubGroup::class,
        ExpenseSubGroup::class,
        Wallet::class,
        IncomeHistory::class,
        ExpenseHistory::class,
        TransferHistory::class,
        Currency::class,
        BaseCurrencyRate::class
    ],
    version = 40,
    exportSchema = true
)
@TypeConverters(LocalDateTimeRoomTypeConverter::class)
abstract class BestFinanceDatabase : RoomDatabase() {

    abstract fun getIncomeGroupDao(): IncomeGroupDao

    abstract fun getExpenseGroupDao(): ExpenseGroupDao

    abstract fun getExpenseSubGroupDao(): ExpenseSubGroupDao

    abstract fun getIncomeSubGroupDao(): IncomeSubGroupDao

    abstract fun getWalletDao(): WalletDao

    abstract fun getIncomeHistoryDao(): IncomeHistoryDao

    abstract fun getExpenseHistoryDao(): ExpenseHistoryDao

    abstract fun getTransferHistoryDao(): TransferHistoryDao

    abstract fun getCurrencyDao(): CurrencyDao

    abstract fun getBaseCurrencyRatesDao(): BaseCurrencyRatesDao

}
