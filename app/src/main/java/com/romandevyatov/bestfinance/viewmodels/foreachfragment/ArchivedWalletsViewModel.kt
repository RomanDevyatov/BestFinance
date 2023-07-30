package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.Wallet
import com.romandevyatov.bestfinance.data.repositories.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArchivedWalletsViewModel @Inject constructor(
    private val walletRepository: WalletRepository
) : ViewModel() {

    val allWalletsArchivedLiveData: LiveData<List<Wallet>>? = walletRepository.getAllWalletsArchivedLiveData()

    fun getWalletsArchivedByNameLiveData(name: String): LiveData<Wallet>? {
        return walletRepository.getWalletByNameLiveData(name)
    }

    fun updateWallet(wallet: Wallet) = viewModelScope.launch(Dispatchers.IO) {
        walletRepository.updateWallet(wallet)
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

    fun getWalletArchivedByNameLiveData(name: String): LiveData<Wallet>? {
        return walletRepository.getWalletArchivedByNameLiveData(name)
    }

    fun deleteWalletById(id: Long?) = viewModelScope.launch(Dispatchers.IO) {
        walletRepository.deleteWalletById(id)
    }


}