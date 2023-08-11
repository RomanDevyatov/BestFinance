package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.Wallet
import com.romandevyatov.bestfinance.data.repositories.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class SettingsWalletsViewModel @Inject constructor(
    private val walletRepository: WalletRepository
) : ViewModel() {

    val allWalletsLiveData: LiveData<List<Wallet>>? = walletRepository.getAllWalletLiveData()

    fun updateWallet(wallet: Wallet) = viewModelScope.launch(Dispatchers.IO) {
        walletRepository.updateWallet(wallet)
    }

    fun deleteWalletById(id: Long?) = viewModelScope.launch(Dispatchers.IO) {
        walletRepository.deleteWalletById(id)
    }

    fun unarchiveWalletById(id: Long?) = viewModelScope.launch(Dispatchers.IO) {
        walletRepository.unarchiveWalletById(id)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun archiveWalletById(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        val wallet = walletRepository.getWalletById(id)

        val walletArchived = Wallet(
            id = wallet.id,
            name = wallet.name,
            balance = wallet.balance,
            archivedDate = LocalDateTime.now(),
            input = wallet.input,
            output = wallet.output,
            description = wallet.description
        )

        walletRepository.updateWallet(walletArchived)
    }

}
