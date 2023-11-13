package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.BaseCurrencyRate
import com.romandevyatov.bestfinance.data.entities.TransferHistory
import com.romandevyatov.bestfinance.data.entities.Wallet
import com.romandevyatov.bestfinance.data.entities.relations.TransferHistoryWithWallets
import com.romandevyatov.bestfinance.data.repositories.BaseCurrencyRatesRepository
import com.romandevyatov.bestfinance.data.repositories.TransferHistoryRepository
import com.romandevyatov.bestfinance.data.repositories.WalletRepository
import com.romandevyatov.bestfinance.utils.TextFormatter
import com.romandevyatov.bestfinance.utils.TextFormatter.roundDoubleToTwoDecimalPlaces
import com.romandevyatov.bestfinance.utils.sharedpreferences.Storage
import com.romandevyatov.bestfinance.viewmodels.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UpdateTransferHistoryViewModel @Inject constructor(
    storage: Storage,
    private val transferHistoryRepository: TransferHistoryRepository,
    private val walletRepository: WalletRepository,
    private val baseCurrencyRatesRepository: BaseCurrencyRatesRepository
): BaseViewModel(storage) {

    val currentDefaultCurrencySymbol: String = getDefaultCurrencySymbol()

    val allWalletsNotArchivedLiveData: LiveData<List<Wallet>> = walletRepository.getAllWalletsNotArchivedLiveData()

    fun getTransferHistoryWithWalletsByIdLiveData(transferHistoryId: Long): LiveData<TransferHistoryWithWallets?> {
        return transferHistoryRepository.getWithWalletsByIdLiveData(transferHistoryId)
    }

    fun updateTransferHistory(transferHistory: TransferHistory) = viewModelScope.launch (Dispatchers.IO) {
        transferHistoryRepository.updateTransferHistory(transferHistory)
    }

    fun updateWallet(wallet: Wallet) = viewModelScope.launch (Dispatchers.IO) {
        walletRepository.updateWallet(wallet)
    }

    fun updateTransferHistoryAndWallets(updatedTransferHistory: TransferHistory) = viewModelScope.launch (Dispatchers.IO) {
        val walletFrom = walletRepository.getWalletById(updatedTransferHistory.fromWalletId)
        if (walletFrom != null) {
           val baseCurrencyRate = baseCurrencyRatesRepository.getBaseCurrencyRateByPairName(
                "${getDefaultCurrencyCode()}${walletFrom.currencyCode}"
            )
            if (baseCurrencyRate != null) {
                val amountBase = updatedTransferHistory.amount / baseCurrencyRate.value
                updateTransferHistory(
                    updatedTransferHistory.copy(
                        amountBase = amountBase
                    )
                )

                val amount = updatedTransferHistory.amount
                val amountTarget = updatedTransferHistory.amountTarget
                val to = walletRepository.getWalletById(updatedTransferHistory.toWalletId)
                val from = walletRepository.getWalletById(updatedTransferHistory.fromWalletId)

                if (to != null && from != null) {
                    val updatedWalletToBalance = to.balance.plus(amountTarget)
                    val updatedWalletToInput = to.input.plus(amountTarget)

                    val updatedWalletTo = to.copy(
                        balance = updatedWalletToBalance,
                        input = updatedWalletToInput
                    )
                    updateWallet(updatedWalletTo)

                    val updatedWalletFromBalance = from.balance.minus(amount)
                    val updatedWalletFromOutput = from.output.plus(amount)

                    val updatedWalletFrom = from.copy(
                        balance = updatedWalletFromBalance,
                        output = updatedWalletFromOutput
                    )
                    updateWallet(updatedWalletFrom)
                }
            }
        }
    }

    private var deletedItem: TransferHistory? = null

    fun deleteItem(id: Long) = viewModelScope.launch (Dispatchers.IO) {
        try {
            val itemToDelete = transferHistoryRepository.getTransferHistoryById(id)
            deletedItem = itemToDelete
            transferHistoryRepository.deleteTransferHistoryById(id)
        } catch (_: Exception) { }
    }

    fun undoDeleteItem() = viewModelScope.launch (Dispatchers.IO) {
        deletedItem?.let { itemToRestore ->
            try {
                transferHistoryRepository.insertTransferHistory(itemToRestore)
                deletedItem = null
            } catch (_: Exception) { }
        }
    }

    fun getWalletByIdLiveData(id: Long): LiveData<Wallet?> {
        return walletRepository.getWalletByIdLiveData(id)
    }

    fun getBaseCurrencyRateByPairNameLiveData(pairName: String): LiveData<BaseCurrencyRate?> {
        return baseCurrencyRatesRepository.getBaseCurrencyRateByPairNameLiveData(pairName)
    }

    suspend fun getWalletById(id: Long?): Wallet? =
        withContext(Dispatchers.IO) {
            walletRepository.getWalletById(id)
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

                val result: Double

                if (baseCurrencyRate != null && baseCurrencyRateTarget != null) {
                    val amountBase = amount / baseCurrencyRate.value
                    result = roundDoubleToTwoDecimalPlaces(amountBase * baseCurrencyRateTarget.value)
                } else {
                    result = 0.0
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
// -------------------
//    suspend fun getBaseCurrencyRateByDateByPairNameAsync(pairName: String): BaseCurrencyRate? {
//        return withContext(Dispatchers.IO) {
//            baseCurrencyRatesRepository.getBaseCurrencyRateByPairName(pairName)
//        }
//    }
}
