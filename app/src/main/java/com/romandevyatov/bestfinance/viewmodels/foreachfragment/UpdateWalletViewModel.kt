package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.ExpenseHistory
import com.romandevyatov.bestfinance.data.entities.IncomeHistory
import com.romandevyatov.bestfinance.data.entities.Wallet
import com.romandevyatov.bestfinance.data.repositories.ExpenseHistoryRepository
import com.romandevyatov.bestfinance.data.repositories.IncomeHistoryRepository
import com.romandevyatov.bestfinance.data.repositories.WalletRepository
import com.romandevyatov.bestfinance.utils.Constants.UNDO_DELAY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateWalletViewModel @Inject constructor(
    private val walletRepository: WalletRepository,
    private val incomeHistoryRepository: IncomeHistoryRepository,
    private val expenseHistoryRepository: ExpenseHistoryRepository
) : ViewModel() {

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

    fun updateNameAndDescriptionAndBalanceWalletById(updatedWalletBinding: Wallet) = viewModelScope.launch(Dispatchers.IO) {
        val wallet = walletRepository.getWalletByIdAsync(updatedWalletBinding.id)

        if (wallet != null) {
            val updatedWallet = wallet.copy(
                name = updatedWalletBinding.name,
                description = updatedWalletBinding.description,
                balance = updatedWalletBinding.balance
            )

            updateWallet(updatedWallet)
        }
    }

    fun addOnlyWalletIncomeHistoryRecord(incomeHistory: IncomeHistory) = viewModelScope.launch(Dispatchers.IO) {
        incomeHistoryRepository.insertIncomeHistory(incomeHistory)
    }

    fun addOnlyWalletExpenseHistoryRecord(expenseHistory: ExpenseHistory) = viewModelScope.launch(Dispatchers.IO) {
        expenseHistoryRepository.insertExpenseHistory(expenseHistory)
    }

    private var deletedItem: Wallet? = null
    private val deletedItemList = mutableListOf<Wallet>()

    fun deleteItem(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val itemToDelete = walletRepository.getWalletByIdAsync(id)
                if (itemToDelete != null) {
                    deletedItem = itemToDelete
                    deletedItemList.add(itemToDelete)

                    // Delay for the specified time before deletion
                    delay(UNDO_DELAY)

                    // After the delay, check if the item is still in the list and delete it
                    if (deletedItemList.contains(itemToDelete)) {
                        walletRepository.deleteWalletById(id)
                        deletedItemList.remove(itemToDelete)
                    }
                }
            } catch (_: Exception) {
            }
        }
    }

    fun undoDeleteItem() = viewModelScope.launch(Dispatchers.IO) {
        if (deletedItemList.contains(deletedItem)) {
            deletedItemList.remove(deletedItem)
            deletedItem = null
        }
    }

}
