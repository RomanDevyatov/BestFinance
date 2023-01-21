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

//    abstract fun getExpenseGroupDao(): ExpenseGroupDao
//
//    private class WordDatabaseCallback(
//        private val scope: CoroutineScope
//    ) : RoomDatabase.Callback() {
//
//        override fun onCreate(db: SupportSQLiteDatabase) {
//            super.onCreate(db)
//            INSTANCE?.let { database ->
//                scope.launch {
//                    populateDatabase(database.getIncomeGroupDao())
//                }
//            }
//        }
//
//        suspend fun populateDatabase(incomeGroupDao: IncomeGroupDao) {
//            // Delete all content here.
//            incomeGroupDao.deleteAll()
//
//            // Add sample words.
//            var group = IncomeGroup(null,"Labor")
//            incomeGroupDao.insertIncomeGroup(group)
//            group = IncomeGroup(null, "Portfolio!")
//            incomeGroupDao.insertIncomeGroup(group)
//
//            // TODO: Add your own words!
//        }
//    }
//
//    companion object {
//        @Volatile
//        private var INSTANCE: BestFinanceDatabase? = null
//        private const val DB_NAME = "best_finance"
//
//        fun getDatabase(context: Context): BestFinanceDatabase {
////            if (INSTANCE != null) return INSTANCE!!
//
//            // if the INSTANCE is not null, then return it,
//            // if it is, then create the database
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    BestFinanceDatabase::class.java,
//                    "word_database"
//                )
//                    .build()
//                INSTANCE = instance
//                // return instance
//                instance
//            }
//        }
//    }
}