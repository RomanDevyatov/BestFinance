package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.WalletEntity
import com.romandevyatov.bestfinance.data.repositories.WalletRepository
import com.romandevyatov.bestfinance.utils.sharedpreferences.Storage
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

    val allWalletsLiveData: LiveData<List<WalletEntity>> = walletRepository.getAllWalletLiveData()

    fun updateWallet(walletEntity: WalletEntity) = viewModelScope.launch(Dispatchers.IO) {
        walletRepository.updateWallet(walletEntity)
    }

    fun unarchiveWalletById(id: Long?) = viewModelScope.launch(Dispatchers.IO) {
        walletRepository.unarchiveWalletById(id)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun archiveWalletById(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        val wallet = walletRepository.getWalletByIdAsync(id)
        if (wallet != null) {
            val walletArchived = wallet.copy(
                archivedDate = LocalDateTime.now()
            )

            walletRepository.updateWallet(walletArchived)
        }
    }

    private var deletedWalletItemEntity: WalletEntity? = null

    fun deleteItem(id: Long) = viewModelScope.launch (Dispatchers.IO) {
        try {
            val itemToDelete = walletRepository.getWalletByIdAsync(id)
            deletedWalletItemEntity = itemToDelete
            walletRepository.deleteWalletById(id)
        } catch (_: Exception) {

        }
    }

    fun undoDeleteItem() = viewModelScope.launch (Dispatchers.IO) {
        deletedWalletItemEntity?.let { walletToRestore ->
            try {
                walletRepository.insertWallet(walletToRestore)
                deletedWalletItemEntity = null
            } catch (_: Exception) { }
        }
    }
}
