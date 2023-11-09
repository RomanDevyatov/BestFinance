package com.romandevyatov.bestfinance.data.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.romandevyatov.bestfinance.data.dao.IncomeGroupDao
import com.romandevyatov.bestfinance.data.dao.ExpenseGroupDao
import com.romandevyatov.bestfinance.data.dao.ExpenseSubGroupDao
import com.romandevyatov.bestfinance.data.dao.IncomeSubGroupDao
import com.romandevyatov.bestfinance.data.dao.WalletDao
import com.romandevyatov.bestfinance.data.dao.IncomeHistoryDao
import com.romandevyatov.bestfinance.data.dao.ExpenseHistoryDao
import com.romandevyatov.bestfinance.data.dao.TransferHistoryDao
import com.romandevyatov.bestfinance.data.dao.CurrencyDao
import com.romandevyatov.bestfinance.data.entities.IncomeGroup
import com.romandevyatov.bestfinance.data.entities.ExpenseGroupEntity
import com.romandevyatov.bestfinance.data.entities.IncomeSubGroup
import com.romandevyatov.bestfinance.data.entities.ExpenseSubGroup
import com.romandevyatov.bestfinance.data.entities.Wallet
import com.romandevyatov.bestfinance.data.entities.IncomeHistory
import com.romandevyatov.bestfinance.data.entities.ExpenseHistory
import com.romandevyatov.bestfinance.data.entities.TransferHistory
import com.romandevyatov.bestfinance.data.entities.Currency
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
        Currency::class
    ],
    version = 38,
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
}
