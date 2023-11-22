package com.romandevyatov.bestfinance.viewmodels.foreachmodel

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
class WalletViewModel @Inject constructor(
    storage: Storage,
    private val walletRepository: WalletRepository
): BaseViewModel(storage) {

    val currentDefaultCurrencyCode: String = getDefaultCurrencyCode()

    val allWalletsNotArchivedLiveData: LiveData<List<WalletEntity>> = walletRepository.getAllWalletsNotArchivedLiveData()

    fun insertWallet(walletEntity: WalletEntity) = viewModelScope.launch(Dispatchers.IO) {
        walletRepository.insertWallet(walletEntity)
    }

    fun updateWallet(walletEntity: WalletEntity) = viewModelScope.launch(Dispatchers.IO) {
        walletRepository.updateWallet(walletEntity)
    }

    fun getWalletByNameLiveData(walletName: String): LiveData<WalletEntity?> {
        return walletRepository.getWalletByNameLiveData(walletName)
    }

    fun unarchiveWallet(walletEntity: WalletEntity) = viewModelScope.launch(Dispatchers.IO) {
        val updatedWalletEntity = WalletEntity(
            id = walletEntity.id,
            name = walletEntity.name,
            description = walletEntity.description,
            balance = walletEntity.balance,
            input = walletEntity.input,
            output = walletEntity.output,
            archivedDate = null,
            currencyCode = walletEntity.currencyCode
        )
        updateWallet(updatedWalletEntity)
    }

    fun archiveWalletById(id: Long?, date: LocalDateTime) = viewModelScope.launch(Dispatchers.IO) {
        walletRepository.archiveWalletById(id, date)
    }

    fun unarchiveWalletById(id: Long?) = viewModelScope.launch(Dispatchers.IO) {
        walletRepository.unarchiveWalletById(id)
    }

}
