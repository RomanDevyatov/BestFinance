package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.db.entities.Wallet
import com.romandevyatov.bestfinance.repositories.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import javax.inject.Inject

@HiltViewModel
class AddTransferViewModel @Inject constructor(
    private val walletRepository: WalletRepository
): ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    fun archiveWallet(name: String) = viewModelScope.launch(Dispatchers.IO) {
        val selectedWallet = walletRepository.getWalletByNameNotArchived(name)

        val selectedWalletArchived = Wallet(
            id = selectedWallet.id,
            name = selectedWallet.name,
            balance = selectedWallet.balance,
            archivedDate = OffsetDateTime.now(),
            input = selectedWallet.input,
            output = selectedWallet.output,
            description = selectedWallet.description
        )

        walletRepository.updateWallet(selectedWalletArchived)
    }
}