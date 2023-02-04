package com.romandevyatov.bestfinance.di

import android.content.Context
import androidx.room.Room
import com.romandevyatov.bestfinance.db.roomdb.BestFinanceDatabase
import com.romandevyatov.bestfinance.utils.Constants.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providesBudgetDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
            context,
            BestFinanceDatabase::class.java,
            DATABASE_NAME)
        .createFromAsset("database/bestfinance_database_pre.db")
        .build()

    @Provides
    @Singleton
    fun providesIncomeGroupDao(db: BestFinanceDatabase) = db.getIncomeGroupDao()

    @Provides
    @Singleton
    fun providesExpenseGroupDao(db: BestFinanceDatabase) = db.getExpenseGroupDao()

    @Provides
    @Singleton
    fun providesWalletDao(db: BestFinanceDatabase) = db.getWalletDao()

    @Provides
    @Singleton
    fun providesIncomeHistoryWithIncomeGroupAndWalletDao(db: BestFinanceDatabase) = db.getIncomeHistoryWithIncomeGroupAndWalletDao()
//        .fallbackToDestructiveMigration()
}
