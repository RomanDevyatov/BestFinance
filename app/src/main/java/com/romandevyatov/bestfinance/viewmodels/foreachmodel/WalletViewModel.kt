package com.romandevyatov.bestfinance.viewmodels.foreachmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.db.entities.Wallet
import com.romandevyatov.bestfinance.repositories.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val walletRepository: WalletRepository
) : ViewModel() {

    val walletsLiveData: LiveData<List<Wallet>> = walletRepository.getAllWallets()

    val allWalletsNotArchivedLiveData: LiveData<List<Wallet>> = walletRepository.getAllWalletsNotArchivedLiveData()

    fun insertWallet(wallet: Wallet) = viewModelScope.launch(Dispatchers.IO) {
        walletRepository.insertWallet(wallet)
    }

    fun updateWallet(wallet: Wallet) = viewModelScope.launch(Dispatchers.IO) {
        walletRepository.updateWallet(wallet)
    }

    fun deleteWallet(wallet: Wallet) = viewModelScope.launch(Dispatchers.IO) {
        walletRepository.deleteWallet(wallet)
    }

    fun getWalletByNameNotArchivedLiveData(walletName: String): LiveData<Wallet> {
        return walletRepository.getWalletByNameNotArchivedLiveData(walletName)
    }

    fun getWalletByNameLiveData(walletName: String): LiveData<Wallet>? {
        return walletRepository.getWalletByNameLiveData(walletName)
    }

    fun unarchiveWallet(wallet: Wallet) = viewModelScope.launch(Dispatchers.IO) {
        val updatedWallet = Wallet(
            id = wallet.id,
            name = wallet.name,
            description = wallet.description,
            balance = wallet.balance,
            input = wallet.input,
            output = wallet.output,
            archivedDate = null
        )
        updateWallet(updatedWallet)
    }

    fun updateWalletById(updatedWalletBinding: Wallet) = viewModelScope.launch(Dispatchers.IO) {
        val wallet = walletRepository.getWalletById(updatedWalletBinding.id)

        val updatedWallet = Wallet(
            id = wallet.id,
            name = updatedWalletBinding.name,
            description = updatedWalletBinding.description,
            balance = updatedWalletBinding.balance,
            input = wallet.input,
            output = wallet.output,
            archivedDate = wallet.archivedDate
        )

        updateWallet(updatedWallet)
    }

}
