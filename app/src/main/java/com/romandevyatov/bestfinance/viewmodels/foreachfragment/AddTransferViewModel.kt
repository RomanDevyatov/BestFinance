package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.BaseCurrencyRateEntity
import com.romandevyatov.bestfinance.data.entities.TransferHistoryEntity
import com.romandevyatov.bestfinance.data.entities.WalletEntity
import com.romandevyatov.bestfinance.data.repositories.BaseCurrencyRatesRepository
import com.romandevyatov.bestfinance.data.repositories.TransferHistoryRepository
import com.romandevyatov.bestfinance.data.repositories.WalletRepository
import com.romandevyatov.bestfinance.utils.TextFormatter
import com.romandevyatov.bestfinance.utils.sharedpreferences.Storage
import com.romandevyatov.bestfinance.viewmodels.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

    fun insertWallet(walletEntity: WalletEntity) = viewModelScope.launch(Dispatchers.IO) {
        walletRepository.insertWallet(walletEntity)
    }

    fun insertTransferHistory(transferHistoryEntity: TransferHistoryEntity) = viewModelScope.launch(Dispatchers.IO) {
        transferHistoryRepository.insertTransferHistory(transferHistoryEntity)
    }

    fun getWalletByIdLiveData(id: Long): LiveData<WalletEntity?> {
        return walletRepository.getWalletByIdLiveData(id)
    }

    suspend fun getBaseCurrencyRateByPairName(pairName: String): BaseCurrencyRateEntity? {
        return baseCurrencyRatesRepository.getBaseCurrencyRateByPairName(pairName)
    }

    fun sendAndUpdateBaseAmount(transferHistoryEntity: TransferHistoryEntity) = viewModelScope.launch(Dispatchers.IO) {
        val wallet = walletRepository.getWalletByIdAsync(transferHistoryEntity.fromWalletId)
        wallet?.let {
            val defaultCurrencyCode =
                getDefaultCurrencyCode()
            val pairName = defaultCurrencyCode + it.currencyCode
            val baseCurrencyRate =
                getBaseCurrencyRateByPairName(
                    pairName
                )
            if (baseCurrencyRate != null) {
                val amountBase = transferHistoryEntity.amount / baseCurrencyRate.value // in usd

                insertTransferHistory(transferHistoryEntity.copy(
                    amountBase = amountBase
                ))
            }
        }
    }

    suspend fun getWalletById(id: Long?): WalletEntity? = withContext(Dispatchers.IO) {
            walletRepository.getWalletByIdAsync(id)
    }

    suspend fun calculateTransferAmount(
        amount: Double,
        fromWalletEntity: WalletEntity?,
        toWalletEntity: WalletEntity?
    ): Double = withContext(Dispatchers.IO) {
        fromWalletEntity?.let { fromWlt ->
            val defaultCurrencyCode = getDefaultCurrencyCode()
            val pairName = defaultCurrencyCode + fromWlt.currencyCode
            val baseCurrencyRate = getBaseCurrencyRateByPairNameAsync(pairName)

            toWalletEntity?.let { toWlt ->
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

    suspend fun getBaseCurrencyRateByPairNameAsync(pairName: String): BaseCurrencyRateEntity? {
        return withContext(Dispatchers.IO) {
            baseCurrencyRatesRepository.getBaseCurrencyRateByPairName(pairName)
        }
    }
}
