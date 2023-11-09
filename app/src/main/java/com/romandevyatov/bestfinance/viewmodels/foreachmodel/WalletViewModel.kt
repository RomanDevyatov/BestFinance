package com.romandevyatov.bestfinance.viewmodels.foreachmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.Wallet
import com.romandevyatov.bestfinance.data.repositories.WalletRepository
import com.romandevyatov.bestfinance.utils.localization.Storage
import com.romandevyatov.bestfinance.viewmodels.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    storage: Storage,
    private val walletRepository: WalletRepository
): BaseViewModel(storage) {

    val currentDefaultCurrencyCode: String = getDefaultCurrencyCode()

    val allWalletsNotArchivedLiveData: LiveData<List<Wallet>> = walletRepository.getAllWalletsNotArchivedLiveData()

    fun insertWallet(wallet: Wallet) = viewModelScope.launch(Dispatchers.IO) {
        walletRepository.insertWallet(wallet)
    }

    fun updateWallet(wallet: Wallet) = viewModelScope.launch(Dispatchers.IO) {
        walletRepository.updateWallet(wallet)
    }

    fun getWalletByNameLiveData(walletName: String): LiveData<Wallet?> {
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
            archivedDate = null,
            currencyCode = wallet.currencyCode
        )
        updateWallet(updatedWallet)
    }

    fun archiveWalletById(id: Long?, date: LocalDateTime) = viewModelScope.launch(Dispatchers.IO) {
        walletRepository.archiveWalletById(id, date)
    }

    fun unarchiveWalletById(id: Long?) = viewModelScope.launch(Dispatchers.IO) {
        walletRepository.unarchiveWalletById(id)
    }

}
