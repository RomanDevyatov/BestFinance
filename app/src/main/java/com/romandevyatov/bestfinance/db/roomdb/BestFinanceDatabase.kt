package com.romandevyatov.bestfinance.db.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.romandevyatov.bestfinance.db.dao.*
import com.romandevyatov.bestfinance.db.entities.*
import com.romandevyatov.bestfinance.db.roomdb.converters.LocalDateTimeRoomTypeConverter

@Database(
    entities = [
        IncomeGroup::class,
        ExpenseGroup::class,
        IncomeSubGroup::class,
        ExpenseSubGroup::class,
        Wallet::class,
        IncomeHistory::class,
        ExpenseHistory::class,
        TransferHistory::class
    ],
    version = 30,
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

}
