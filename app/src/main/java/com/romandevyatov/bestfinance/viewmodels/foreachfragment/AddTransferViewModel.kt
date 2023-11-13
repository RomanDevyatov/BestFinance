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
import com.romandevyatov.bestfinance.utils.sharedpreferences.Storage
import com.romandevyatov.bestfinance.viewmodels.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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

    fun getBaseCurrencyRateByPairName(pairName: String): BaseCurrencyRate? {
        return runBlocking {
            baseCurrencyRatesRepository.getBaseCurrencyRateByPairName(pairName)
        }
    }

    fun sendAndUpdateBaseAmount(transferHistory: TransferHistory) = viewModelScope.launch(Dispatchers.IO) {
        val wallet = walletRepository.getWalletById(transferHistory.fromWalletId)
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
}
