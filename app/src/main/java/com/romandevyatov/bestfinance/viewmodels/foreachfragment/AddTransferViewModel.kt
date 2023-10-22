package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.TransferHistory
import com.romandevyatov.bestfinance.data.entities.Wallet
import com.romandevyatov.bestfinance.data.repositories.TransferHistoryRepository
import com.romandevyatov.bestfinance.data.repositories.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class AddTransferViewModel @Inject constructor(
    private val walletRepository: WalletRepository,
    private val transferHistoryRepository: TransferHistoryRepository
): ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    fun archiveWallet(name: String) = viewModelScope.launch(Dispatchers.IO) {
        val selectedWallet = walletRepository.getWalletByNameNotArchived(name)

        if (selectedWallet != null) {
            val selectedWalletArchived = Wallet(
                id = selectedWallet.id,
                name = selectedWallet.name,
                balance = selectedWallet.balance,
                archivedDate = LocalDateTime.now(),
                input = selectedWallet.input,
                output = selectedWallet.output,
                description = selectedWallet.description
            )

            walletRepository.updateWallet(selectedWalletArchived)
        }
    }

    fun insertWallet(wallet: Wallet) = viewModelScope.launch(Dispatchers.IO) {
        walletRepository.insertWallet(wallet)
    }

    fun insertTransferHistory(transferHistory: TransferHistory) = viewModelScope.launch(Dispatchers.IO) {
        transferHistoryRepository.insertTransferHistory(transferHistory)
    }
}
