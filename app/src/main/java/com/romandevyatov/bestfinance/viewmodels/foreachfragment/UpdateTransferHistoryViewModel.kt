package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.TransferHistory
import com.romandevyatov.bestfinance.data.entities.Wallet
import com.romandevyatov.bestfinance.data.entities.relations.TransferHistoryWithWallets
import com.romandevyatov.bestfinance.data.repositories.TransferHistoryRepository
import com.romandevyatov.bestfinance.data.repositories.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateTransferHistoryViewModel @Inject constructor(
    private val transferHistoryRepository: TransferHistoryRepository,
    private val walletRepository: WalletRepository
): ViewModel() {

    val allWalletsNotArchivedLiveData: LiveData<List<Wallet>> = walletRepository.getAllWalletsNotArchivedLiveData()

    fun getTransferHistoryWithWalletsByIdLiveData(transferHistoryId: Long): LiveData<TransferHistoryWithWallets> {
        return transferHistoryRepository.getWithWalletsByIdLiveData(transferHistoryId)
    }

    fun updateTransferHistory(transferHistory: TransferHistory) = viewModelScope.launch (Dispatchers.IO) {
        transferHistoryRepository.updateTransferHistory(transferHistory)
    }

    fun updateWallet(wallet: Wallet) = viewModelScope.launch (Dispatchers.IO) {
        walletRepository.updateWallet(wallet)
    }

    fun updateTransferHistoryAndWallets(updatedTransferHistory: TransferHistory) = viewModelScope.launch (Dispatchers.IO) {
        updateTransferHistory(updatedTransferHistory)

        val amount = updatedTransferHistory.amount
        val to = walletRepository.getWalletById(updatedTransferHistory.toWalletId)
        val from = walletRepository.getWalletById(updatedTransferHistory.fromWalletId)

        val updatedWalletToBalance = to.balance.plus(amount)
        val updatedWalletToInput = to.input.plus(amount)

        val updatedWalletTo = Wallet(
            id = to.id,
            name = to.name,
            balance = updatedWalletToBalance,
            input = updatedWalletToInput,
            output = to.output,
            description = to.description,
            archivedDate = to.archivedDate
        )
        updateWallet(updatedWalletTo)

        val updatedWalletFromBalance = from.balance.minus(amount)
        val updatedWalletFromOutput = from.output.plus(amount)

        val updatedWalletFrom = Wallet(
            id = from.id,
            name = from.name,
            balance = updatedWalletFromBalance,
            input = from.input,
            output = updatedWalletFromOutput,
            description = from.description,
            archivedDate = from.archivedDate
        )
        updateWallet(updatedWalletFrom)
    }
}
