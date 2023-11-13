package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.BuildConfig
import com.romandevyatov.bestfinance.data.entities.BaseCurrencyRate
import com.romandevyatov.bestfinance.data.entities.Currency
import com.romandevyatov.bestfinance.data.repositories.*
import com.romandevyatov.bestfinance.data.retrofit.repository.ExchangeRateRepository
import com.romandevyatov.bestfinance.utils.Constants
import com.romandevyatov.bestfinance.utils.TextFormatter.roundDoubleToTwoDecimalPlaces
import com.romandevyatov.bestfinance.utils.sharedpreferences.Storage
import com.romandevyatov.bestfinance.viewmodels.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectCurrencyViewModel @Inject constructor(
    private val storage: Storage,
    private val currencyRepository: CurrencyRepository,
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

    val allCurrenciesLiveData: LiveData<List<Currency>> = currencyRepository.getAllCurrenciesLiveData()

    val currentDefaultCurrencyCode: String = getDefaultCurrencyCode()


    fun recalculateBaseAmountForHistory(code: String) = viewModelScope.launch(Dispatchers.IO + SupervisorJob()) {
        try {
            setPreferredDefaultCurrencyCode(code)
            val apiKey = BuildConfig.API_KEY
            val baseCurrencyCode = storage.getDefaultCurrencyCode()
            val currencies: String = Constants.supportedCurrencies.joinToString(separator = ",") { it.code }

            val exchangeRates2 = async { exchangeRateRepository.getExchangeRates(apiKey, baseCurrencyCode, currencies) }
            val response = exchangeRates2.await()
            if (response.isSuccessful) {
                baseCurrencyRatesRepository.deleteAll()
                baseCurrencyRatesRepository.insertAllBaseCurrencyRate(mapToBaseCurrencyExchangeRates(response.body()?.data))

                val incomeHistories = incomeHistoryRepository.getAllIncomeHistory()
                incomeHistories.forEach { incomeHistory ->
                    val wallet = walletRepository.getWalletById(incomeHistory.walletId)
                    if (wallet != null) {
                        val pairName = "${code}${wallet.currencyCode}"

                        val baseCurrencyRate = baseCurrencyRatesRepository.getBaseCurrencyRateByPairName(pairName)

                        if (baseCurrencyRate != null) {
                            val updatedBaseAmount = roundDoubleToTwoDecimalPlaces(incomeHistory.amount / baseCurrencyRate.value)

                            val updateBaseAmountIncomeHistory = incomeHistory.copy(
                                amountBase = updatedBaseAmount
                            )
                            incomeHistoryRepository.updateIncomeHistory(updateBaseAmountIncomeHistory)
                        }
                    }
                }

                val expenseHistories = expenseHistoryRepository.getAllExpenseHistory()
                expenseHistories.forEach { expenseHistory ->
                    val wallet = walletRepository.getWalletById(expenseHistory.walletId)
                    if (wallet != null) {
                        val pairName = "${storage.getDefaultCurrencyCode()}${wallet.currencyCode}"
                        val baseCurrencyRate = baseCurrencyRatesRepository.getBaseCurrencyRateByPairName(pairName)

                        if (baseCurrencyRate != null) {
                            val updatedBaseAmount = roundDoubleToTwoDecimalPlaces(expenseHistory.amount / baseCurrencyRate.value)

                            val updateBaseAmountExpenseHistory = expenseHistory.copy(
                                amountBase = updatedBaseAmount
                            )
                            expenseHistoryRepository.updateExpenseHistory(updateBaseAmountExpenseHistory)
                        }
                    }
                }
            } else {
                Log.d("tag", "getBaseRates error: ${response.code()}")
            }

            // Now proceed with database operations
            // for transaction and transfers
            // get rates by date
            // fact amount -> new base amount
        } catch (e: Exception) {
            Log.e("tag2", "Error recalculateBaseAmountForHistory", e)
        }
    }

    private fun mapToBaseCurrencyExchangeRates(exchangeRates: Map<String, Double>?): MutableList<BaseCurrencyRate> {
        val currencyExchangeRates = mutableListOf<BaseCurrencyRate>()

        val defaultCurrencySymbol = getDefaultCurrencyCode()

        exchangeRates?.forEach { (currencyCode, exchangeRate) ->
            val currencyExchangeRate = BaseCurrencyRate(
                pairName = defaultCurrencySymbol + currencyCode,
                value = exchangeRate
            )
            currencyExchangeRates.add(currencyExchangeRate)
        }

        return currencyExchangeRates
    }

}
