package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.romandevyatov.bestfinance.BuildConfig
import com.romandevyatov.bestfinance.data.entities.*
import com.romandevyatov.bestfinance.data.repositories.*
import com.romandevyatov.bestfinance.data.retrofit.repository.ExchangeRateRepository
import com.romandevyatov.bestfinance.ui.fragments.history.tabs.models.ExpenseHistoryByDate
import com.romandevyatov.bestfinance.ui.fragments.history.tabs.models.IncomesHistoryByDate
import com.romandevyatov.bestfinance.ui.fragments.history.tabs.models.TransferHistoryByDate
import com.romandevyatov.bestfinance.utils.Constants.supportedCurrencies
import com.romandevyatov.bestfinance.utils.TextFormatter.roundDoubleToTwoDecimalPlaces
import com.romandevyatov.bestfinance.utils.sharedpreferences.Storage
import com.romandevyatov.bestfinance.viewmodels.BaseViewModel
import com.romandevyatov.bestfinance.viewmodels.models.BaseCurrencyRate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SelectCurrencyViewModel @Inject constructor(
    storage: Storage,
    currencyRepository: CurrencyRepository,
    private val baseCurrencyRatesRepository: BaseCurrencyRatesRepository,
    private val walletRepository: WalletRepository,
    private val incomeHistoryRepository: IncomeHistoryRepository,
    private val expenseHistoryRepository: ExpenseHistoryRepository,
    private val transferHistoryRepository: TransferHistoryRepository,
    private val exchangeRateRepository: ExchangeRateRepository
) : BaseViewModel(storage) {

    private val _exchangeRates = MutableLiveData<Map<String, Double>>()
    val exchangeRates: LiveData<Map<String, Double>>
        get() = _exchangeRates

    private val _circleProgress = MutableLiveData<Int>()
    val circleProgress: LiveData<Int>
        get() = _circleProgress

    val allCurrenciesLiveData: LiveData<List<CurrencyEntity>> = currencyRepository.getAllCurrenciesLiveData()

    fun updateData(newValue: Int) {
        // Run this block on the main thread using postValue
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                _circleProgress.value = newValue
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun recalculateBaseAmountForHistory(
        code: String,
    ) = withContext (Dispatchers.IO) {
        setPreferredDefaultCurrencyCode(code)
        val apiKey = BuildConfig.API_KEY
        val baseCurrencyCode = getDefaultCurrencyCode()
        val currencies: String = supportedCurrencies.joinToString(separator = ",") { it.code }

        val response = exchangeRateRepository.getExchangeRates(apiKey, baseCurrencyCode, currencies)

        if (response.isSuccessful) {
            deleteAllAndInsert(mapToBaseCurrencyExchangeRates(response.body()?.data))
            updateData(10)
            updateAmountBaseOfIncomeHistories()
            updateData(40)
            updateAmountBaseOfExpenseHistories()
            updateData(70)
            updateAmountBaseOfTransferHistories()
            updateData(100)
        } else {
            Log.d("tag", "getBaseRates error: ${response.code()}")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun updateAmountBaseOfIncomeHistories() = withContext(Dispatchers.IO) {
        val incomeHistories = incomeHistoryRepository.getAllIncomeHistory()
        val sortedExpenseHistories = incomeHistories.sortedByDescending { it.date }
        val groupedIncomesHistoriesByDate = groupIncomesByDate(sortedExpenseHistories)

        val tick = (groupedIncomesHistoriesByDate.size / 30.0 * 100).toInt()

        groupedIncomesHistoriesByDate.forEach { dateWithIncomeHistories ->
            val date = dateWithIncomeHistories.date

            dateWithIncomeHistories.incomeHistories.forEach { incomeHistory ->
                val wallet = walletRepository.getWalletByIdAsync(incomeHistory.walletId)
                if (wallet != null) {
                    val currencyCode = wallet.currencyCode

                    val baseCurrencyRate = date?.let {
                        if (it == LocalDate.now()) {
                            getBaseCurrencyRateByPairNameByDateAsync(
                                currencyCode,
                                convertLocalDateToLocalDateTime(date)
                            )
                        } else {
                            getBaseCurrencyRateByPairNameAsync(
                                currencyCode
                            )
                        }
                    }

                    if (baseCurrencyRate != null) {
                        val updatedBaseAmount =
                            roundDoubleToTwoDecimalPlaces(incomeHistory.amount / baseCurrencyRate.value)

                        val updateBaseAmountIncomeHistory = incomeHistory.copy(
                            amountBase = updatedBaseAmount
                        )
                        incomeHistoryRepository.updateIncomeHistory(updateBaseAmountIncomeHistory)
                    }
                }
            }

            circleProgress.value?.plus(tick)?.let { updateData(it) }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun updateAmountBaseOfExpenseHistories() {
        val expenseHistories = expenseHistoryRepository.getAllExpenseHistory()
        val sortedExpenseHistories = expenseHistories.sortedByDescending { it.date }
        val groupedExpenseHistoriesByDate = groupExpenseByDate(sortedExpenseHistories)

        val tick = (groupedExpenseHistoriesByDate.size / 30.0 * 100).toInt()

        groupedExpenseHistoriesByDate.forEach { dateWithExpenseHistories ->
            val date = dateWithExpenseHistories.date
            dateWithExpenseHistories.incomeHistories.forEach { expenseHistory ->
                val wallet = walletRepository.getWalletByIdAsync(expenseHistory.walletId)
                if (wallet != null) {
                    val currencyCode = wallet.currencyCode

                    val baseCurrencyRate =
                        date?.let {
                            if (it == LocalDate.now()) {
                                getBaseCurrencyRateByPairNameByDateAsync(
                                    currencyCode,
                                    convertLocalDateToLocalDateTime(date)
                                )
                            } else {
                                getBaseCurrencyRateByPairNameAsync(
                                    currencyCode
                                )
                            }
                        }

                    if (baseCurrencyRate != null) {
                        val updatedBaseAmount =
                            roundDoubleToTwoDecimalPlaces(expenseHistory.amount / baseCurrencyRate.value)

                        val updateBaseAmountExpenseHistory = expenseHistory.copy(
                            amountBase = updatedBaseAmount
                        )
                        expenseHistoryRepository.updateExpenseHistory(updateBaseAmountExpenseHistory)
                    }
                }
            }

            circleProgress.value?.plus(tick)?.let { updateData(it) }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun updateAmountBaseOfTransferHistories() {
        val transferHistories = transferHistoryRepository.getAllTransferHistories()
        val sortedTransferHistories = transferHistories.sortedByDescending { it.date }
        val groupedTransferHistoriesByDate = groupTransfersByDate(sortedTransferHistories)

        val tick = (groupedTransferHistoriesByDate.size / 30.0 * 100).toInt()

        groupedTransferHistoriesByDate.forEach { dateWithTransferHistories ->
            val date = dateWithTransferHistories.date

            dateWithTransferHistories.transferHistories.forEach { transferHistory ->
                val walletFrom = walletRepository.getWalletByIdAsync(transferHistory.fromWalletId)
                if (walletFrom != null) {
                    val currencyCode = walletFrom.currencyCode

                    val baseCurrencyRate = date?.let {
                        if (it == LocalDate.now()) {
                            getBaseCurrencyRateByPairNameByDateAsync(
                                currencyCode,
                                convertLocalDateToLocalDateTime(date)
                            )
                        } else {
                            getBaseCurrencyRateByPairNameAsync(
                                currencyCode
                            )
                        }
                    }

                    if (baseCurrencyRate != null) {
                        val updatedBaseAmount =
                            roundDoubleToTwoDecimalPlaces(transferHistory.amount / baseCurrencyRate.value)

                        val updateBaseAmountTransferHistory = transferHistory.copy(
                            amountBase = updatedBaseAmount
                        )
                        transferHistoryRepository.updateTransferHistory(updateBaseAmountTransferHistory)
                    }
                }
            }
            circleProgress.value?.plus(tick)?.let { updateData(it) }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun groupTransfersByDate(transferHistoryEntities: List<TransferHistoryEntity>): MutableList<TransferHistoryByDate> {
        val groupedTransactions = transferHistoryEntities.groupBy {
            it.date?.toLocalDate()
        }

        val transactionHistoryByDateList = mutableListOf<TransferHistoryByDate>()

        for ((date, transactions) in groupedTransactions) {
            transactionHistoryByDateList.add(TransferHistoryByDate(date, transactions))
        }

        return transactionHistoryByDateList
    }

    suspend fun deleteAllAndInsert(baseCurrencyRateEntities: List<BaseCurrencyRateEntity>) {
        baseCurrencyRatesRepository.deleteAll()
        baseCurrencyRatesRepository.insertAllBaseCurrencyRate(baseCurrencyRateEntities)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun groupIncomesByDate(incomesHistories: List<IncomeHistoryEntity>): MutableList<IncomesHistoryByDate> {
        val groupedTransactions = incomesHistories.groupBy {
            it.date?.toLocalDate()
        }

        val transactionHistoryItemList = mutableListOf<IncomesHistoryByDate>()

        for ((date, transactions) in groupedTransactions) {
            transactionHistoryItemList.add(IncomesHistoryByDate(date, transactions))
        }

        return transactionHistoryItemList
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun groupExpenseByDate(expenseHistories: List<ExpenseHistoryEntity>): MutableList<ExpenseHistoryByDate> {
        val groupedTransactions = expenseHistories.groupBy {
            it.date?.toLocalDate()
        }

        val transactionHistoryItemList = mutableListOf<ExpenseHistoryByDate>()

        for ((date, transactions) in groupedTransactions) {
            transactionHistoryItemList.add(ExpenseHistoryByDate(date, transactions))
        }

        return transactionHistoryItemList
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertLocalDateToLocalDateTime(localDate: LocalDate?): LocalDateTime {
        val localTime = LocalTime.MIDNIGHT
        return LocalDateTime.of(localDate, localTime)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getBaseCurrencyRateByPairNameByDateAsync(currencyCode: String, date: LocalDateTime): BaseCurrencyRate? {
        val apiKey = BuildConfig.API_KEY
        val baseCurrencyCode = getDefaultCurrencyCode()
        val currencies: String = supportedCurrencies.joinToString(separator = ",") { it.code }
        val formattedDate: String = formatLocalDateTime(date)

        val response = exchangeRateRepository.getExchangeRatesByDate(apiKey, formattedDate, baseCurrencyCode, currencies)

        if (response.isSuccessful) {
            val data = response.body()?.data
            val rate = data?.get(formattedDate)?.get(currencyCode)
            rate?.let {
                return BaseCurrencyRate(currencyCode, it)
            }
        } else {
            Log.d("tag", "getBaseRates error: ${response.code()}")
        }
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getBaseCurrencyRateByPairNameAsync(currencyCode: String): BaseCurrencyRate? {
        val apiKey = BuildConfig.API_KEY
        val baseCurrencyCode = getDefaultCurrencyCode()
        val currencies: String = supportedCurrencies.joinToString(separator = ",") { it.code }

        val response = exchangeRateRepository.getExchangeRates(apiKey, baseCurrencyCode, currencies)

        if (response.isSuccessful) {
            val data = response.body()?.data
            val rate = data?.get(currencyCode)
            rate?.let {
                return BaseCurrencyRate(currencyCode, it)
            }
        } else {
            Log.d("tag", "getBaseRates error: ${response.code()}")
        }
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatLocalDateTime(localDateTime: LocalDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return localDateTime.format(formatter)
    }

    private fun mapToBaseCurrencyExchangeRates(exchangeRates: Map<String, Double>?): MutableList<BaseCurrencyRateEntity> {
        val currencyExchangeRates = mutableListOf<BaseCurrencyRateEntity>()

        val defaultCurrencySymbol = getDefaultCurrencyCode()

        exchangeRates?.forEach { (currencyCode, exchangeRate) ->
            val currencyExchangeRate = BaseCurrencyRateEntity(
                pairName = defaultCurrencySymbol + currencyCode,
                value = exchangeRate
            )
            currencyExchangeRates.add(currencyExchangeRate)
        }

        return currencyExchangeRates
    }

}
