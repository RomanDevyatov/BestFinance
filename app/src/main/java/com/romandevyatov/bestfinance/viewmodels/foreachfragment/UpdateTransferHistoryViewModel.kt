package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.BaseCurrencyRateEntity
import com.romandevyatov.bestfinance.data.entities.TransferHistoryEntity
import com.romandevyatov.bestfinance.data.entities.WalletEntity
import com.romandevyatov.bestfinance.data.entities.relations.TransferHistoryWithWallets
import com.romandevyatov.bestfinance.data.repositories.BaseCurrencyRatesRepository
import com.romandevyatov.bestfinance.data.repositories.TransferHistoryRepository
import com.romandevyatov.bestfinance.data.repositories.WalletRepository
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

    val allWalletsNotArchivedLiveData: LiveData<List<WalletEntity>> = walletRepository.getAllWalletsNotArchivedLiveData()

    fun getTransferHistoryWithWalletsByIdLiveData(transferHistoryId: Long): LiveData<TransferHistoryWithWallets?> {
        return transferHistoryRepository.getWithWalletsByIdLiveData(transferHistoryId)
    }

    fun updateTransferHistory(transferHistoryEntity: TransferHistoryEntity) = viewModelScope.launch (Dispatchers.IO) {
        transferHistoryRepository.updateTransferHistory(transferHistoryEntity)
    }

    fun updateWallet(walletEntity: WalletEntity) = viewModelScope.launch (Dispatchers.IO) {
        walletRepository.updateWallet(walletEntity)
    }

    fun updateTransferHistoryAndWallets(updatedTransferHistoryEntity: TransferHistoryEntity) = viewModelScope.launch (Dispatchers.IO) {
        val walletFrom = walletRepository.getWalletByIdAsync(updatedTransferHistoryEntity.fromWalletId)
        if (walletFrom != null) {
           val baseCurrencyRate = baseCurrencyRatesRepository.getBaseCurrencyRateByPairName(
                "${getDefaultCurrencyCode()}${walletFrom.currencyCode}"
            )
            if (baseCurrencyRate != null) {
                val amountBase = updatedTransferHistoryEntity.amount / baseCurrencyRate.value
                updateTransferHistory(
                    updatedTransferHistoryEntity.copy(
                        amountBase = amountBase
                    )
                )

                val amount = updatedTransferHistoryEntity.amount
                val amountTarget = updatedTransferHistoryEntity.amountTarget
                val to = walletRepository.getWalletByIdAsync(updatedTransferHistoryEntity.toWalletId)
                val from = walletRepository.getWalletByIdAsync(updatedTransferHistoryEntity.fromWalletId)

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

    private var deletedItem: TransferHistoryEntity? = null

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

    fun getWalletByIdLiveData(id: Long): LiveData<WalletEntity?> {
        return walletRepository.getWalletByIdLiveData(id)
    }

    fun getBaseCurrencyRateByPairNameLiveData(pairName: String): LiveData<BaseCurrencyRateEntity?> {
        return baseCurrencyRatesRepository.getBaseCurrencyRateByPairNameLiveData(pairName)
    }

    suspend fun getWalletById(id: Long?): WalletEntity? =
        withContext(Dispatchers.IO) {
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

    suspend fun getBaseCurrencyRateByPairNameAsync(pairName: String): BaseCurrencyRateEntity? {
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
