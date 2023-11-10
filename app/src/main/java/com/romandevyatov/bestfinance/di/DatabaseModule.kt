package com.romandevyatov.bestfinance.di

import android.content.Context
import androidx.room.Room
import com.romandevyatov.bestfinance.data.retrofit.api.OpenExchangeApi
import com.romandevyatov.bestfinance.data.roomdb.BestFinanceDatabase
import com.romandevyatov.bestfinance.utils.Constants
import com.romandevyatov.bestfinance.utils.Constants.DATABASE_NAME
import com.romandevyatov.bestfinance.utils.localization.Storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton
import retrofit2.converter.gson.GsonConverterFactory

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
//        .fallbackToDestructiveMigration()
        .build()

//        .createFromAsset("database/bestfinance_database_pre.db")

    @Provides
    @Singleton
    fun providesIncomeGroupDao(db: BestFinanceDatabase) = db.getIncomeGroupDao()

    @Provides
    @Singleton
    fun providesExpenseGroupDao(db: BestFinanceDatabase) = db.getExpenseGroupDao()

    @Provides
    @Singleton
    fun providesExpenseSubGroupDao(db: BestFinanceDatabase) = db.getExpenseSubGroupDao()

    @Provides
    @Singleton
    fun providesIncomeSubGroupDao(db: BestFinanceDatabase) = db.getIncomeSubGroupDao()

    @Provides
    @Singleton
    fun providesWalletDao(db: BestFinanceDatabase) = db.getWalletDao()

    @Provides
    @Singleton
    fun providesIncomeHistoryDao(db: BestFinanceDatabase) = db.getIncomeHistoryDao()

    @Provides
    @Singleton
    fun providesExpenseHistoryDao(db: BestFinanceDatabase) = db.getExpenseHistoryDao()

    @Provides
    @Singleton
    fun providesTransferHistoryDao(db: BestFinanceDatabase) = db.getTransferHistoryDao()

    @Provides
    @Singleton
    fun providesCurrencyDao(db: BestFinanceDatabase) = db.getCurrencyDao()

    @Provides
    @Singleton
    fun providesStorage(@ApplicationContext context: Context): Storage {
        return Storage(context)
    }

    @Provides
    fun provideBaseUrl() = Constants.BASE_URL

    @Provides
    @Singleton
    fun provideOpenExchangeApi(BASE_URL: String): OpenExchangeApi =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenExchangeApi::class.java)

}
