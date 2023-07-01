package com.romandevyatov.bestfinance.viewmodels

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

    val notArchivedWalletsLiveData: LiveData<List<Wallet>> = walletRepository.getAllWalletsNotArchivedLiveData()

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

}
