package com.romandevyatov.bestfinance.db.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.romandevyatov.bestfinance.db.dao.IncomeGroupDao
import com.romandevyatov.bestfinance.db.entities.IncomeGroup


@Database(
    entities = [
        IncomeGroup::class],
    version = 1,
    exportSchema = false)
abstract class BestFinanceDatabase : RoomDatabase() {

    abstract fun getIncomeGroupDao(): IncomeGroupDao

}