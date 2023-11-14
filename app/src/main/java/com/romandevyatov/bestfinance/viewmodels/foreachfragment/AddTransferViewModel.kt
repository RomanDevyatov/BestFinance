package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.BaseCurrencyRate
import com.romandevyatov.bestfinance.data.entities.TransferHistory
import com.romandevyatov.bestfinance.data.entities.Wallet
import com.romandevyatov.bestfinance.data.repositories.BaseCurrencyRatesRepository
import com.romandevyatov.bestfinance.data.repositories.TransferHistoryRepository
import com.romandevyatov.bestfinance.data.repositories.WalletRepository
import com.romandevyatov.bestfinance.utils.TextFormatter
import com.romandevyatov.bestfinance.utils.sharedpreferences.Storage
import com.romandevyatov.bestfinance.viewmodels.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class AddTransferViewModel @Inject constructor(
    storage: Storage,
    private val walletRepository: WalletRepository,
    private val transferHistoryRepository: TransferHistoryRepository,
    private val baseCurrencyRatesRepository: BaseCurrencyRatesRepository
): BaseViewModel(storage) {

    @RequiresApi(Build.VERSION_CODES.O)
    fun archiveWalletById(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        walletRepository.archiveWalletById(id, LocalDateTime.now())
    }

    fun insertWallet(wallet: Wallet) = viewModelScope.launch(Dispatchers.IO) {
        walletRepository.insertWallet(wallet)
    }

    fun insertTransferHistory(transferHistory: TransferHistory) = viewModelScope.launch(Dispatchers.IO) {
        transferHistoryRepository.insertTransferHistory(transferHistory)
    }

    fun getWalletByIdLiveData(id: Long): LiveData<Wallet?> {
        return walletRepository.getWalletByIdLiveData(id)
    }

    suspend fun getBaseCurrencyRateByPairName(pairName: String): BaseCurrencyRate? {
        return baseCurrencyRatesRepository.getBaseCurrencyRateByPairName(pairName)
    }

    fun sendAndUpdateBaseAmount(transferHistory: TransferHistory) = viewModelScope.launch(Dispatchers.IO) {
        val wallet = walletRepository.getWalletByIdAsync(transferHistory.fromWalletId)
        wallet?.let {
            val defaultCurrencyCode =
                getDefaultCurrencyCode()
            val pairName = defaultCurrencyCode + it.currencyCode
            val baseCurrencyRate =
                getBaseCurrencyRateByPairName(
                    pairName
                )
            if (baseCurrencyRate != null) {
                val amountBase = transferHistory.amount / baseCurrencyRate.value // in usd

                insertTransferHistory(transferHistory.copy(
                    amountBase = amountBase
                ))
            }
        }
    }

    suspend fun getWalletById(id: Long?): Wallet? = withContext(Dispatchers.IO) {
            walletRepository.getWalletByIdAsync(id)
    }

    suspend fun calculateTransferAmount(
        amount: Double,
        fromWallet: Wallet?,
        toWallet: Wallet?
    ): Double = withContext(Dispatchers.IO) {
        fromWallet?.let { fromWlt ->
            val defaultCurrencyCode = getDefaultCurrencyCode()
            val pairName = defaultCurrencyCode + fromWlt.currencyCode
            val baseCurrencyRate = getBaseCurrencyRateByPairNameAsync(pairName)

            toWallet?.let { toWlt ->
                val pairName2 = defaultCurrencyCode + toWlt.currencyCode
                val baseCurrencyRateTarget = getBaseCurrencyRateByPairNameAsync(pairName2)

                val result = if (baseCurrencyRate != null && baseCurrencyRateTarget != null) {
                    val amountBase = amount / baseCurrencyRate.value
                    TextFormatter.roundDoubleToTwoDecimalPlaces(amountBase * baseCurrencyRateTarget.value)
                } else {
                    0.0
                }

                return@withContext result
            } ?: 0.0
        } ?: 0.0
    }

    suspend fun getBaseCurrencyRateByPairNameAsync(pairName: String): BaseCurrencyRate? {
        return withContext(Dispatchers.IO) {
            baseCurrencyRatesRepository.getBaseCurrencyRateByPairName(pairName)
        }
    }
}
