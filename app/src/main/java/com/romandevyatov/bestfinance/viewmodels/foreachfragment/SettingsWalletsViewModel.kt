package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import android.os.Build
import androidx.annotation.RequiresApi
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
class SettingsWalletsViewModel @Inject constructor(
    storage: Storage,
    private val walletRepository: WalletRepository
): BaseViewModel(storage) {

    val currentDefaultCurrencySymbol: String = getDefaultCurrencySymbol()

    val allWalletsLiveData: LiveData<List<Wallet>> = walletRepository.getAllWalletLiveData()

    fun updateWallet(wallet: Wallet) = viewModelScope.launch(Dispatchers.IO) {
        walletRepository.updateWallet(wallet)
    }

    fun unarchiveWalletById(id: Long?) = viewModelScope.launch(Dispatchers.IO) {
        walletRepository.unarchiveWalletById(id)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun archiveWalletById(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        val wallet = walletRepository.getWalletById(id)
        if (wallet != null) {
            val walletArchived = wallet.copy(
                archivedDate = LocalDateTime.now()
            )

            walletRepository.updateWallet(walletArchived)
        }
    }

    private var deletedWalletItem: Wallet? = null

    fun deleteItem(id: Long) = viewModelScope.launch (Dispatchers.IO) {
        try {
            val itemToDelete = walletRepository.getWalletById(id)
            deletedWalletItem = itemToDelete
            walletRepository.deleteWalletById(id)
        } catch (_: Exception) {

        }
    }

    fun undoDeleteItem() = viewModelScope.launch (Dispatchers.IO) {
        deletedWalletItem?.let { walletToRestore ->
            try {
                walletRepository.insertWallet(walletToRestore)
                deletedWalletItem = null
            } catch (_: Exception) { }
        }
    }
}
