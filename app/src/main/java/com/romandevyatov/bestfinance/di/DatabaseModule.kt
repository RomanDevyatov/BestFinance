package com.romandevyatov.bestfinance.di

import android.content.Context
import androidx.room.Room
import com.romandevyatov.bestfinance.BuildConfig
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
    fun provideBudgetDatabase(
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
    fun provideIncomeGroupDao(db: BestFinanceDatabase) = db.getIncomeGroupDao()

    @Provides
    @Singleton
    fun provideExpenseGroupDao(db: BestFinanceDatabase) = db.getExpenseGroupDao()

    @Provides
    @Singleton
    fun provideExpenseSubGroupDao(db: BestFinanceDatabase) = db.getExpenseSubGroupDao()

    @Provides
    @Singleton
    fun provideIncomeSubGroupDao(db: BestFinanceDatabase) = db.getIncomeSubGroupDao()

    @Provides
    @Singleton
    fun provideWalletDao(db: BestFinanceDatabase) = db.getWalletDao()

    @Provides
    @Singleton
    fun provideIncomeHistoryDao(db: BestFinanceDatabase) = db.getIncomeHistoryDao()

    @Provides
    @Singleton
    fun provideExpenseHistoryDao(db: BestFinanceDatabase) = db.getExpenseHistoryDao()

    @Provides
    @Singleton
    fun provideTransferHistoryDao(db: BestFinanceDatabase) = db.getTransferHistoryDao()

    @Provides
    @Singleton
    fun provideCurrencyDao(db: BestFinanceDatabase) = db.getCurrencyDao()

    @Provides
    @Singleton
    fun provideBaseCurrencyRatesDao(db: BestFinanceDatabase) = db.getBaseCurrencyRatesDao()

    @Provides
    @Singleton
    fun provideStorage(@ApplicationContext context: Context): Storage {
        return Storage(context)
    }

    @Provides
    fun provideBaseUrl() = BuildConfig.BASE_FREECURRENCY_API_URL

    @Provides
    @Singleton
    fun provideOpenExchangeApi(BASE_URL: String): OpenExchangeApi =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenExchangeApi::class.java)

}
