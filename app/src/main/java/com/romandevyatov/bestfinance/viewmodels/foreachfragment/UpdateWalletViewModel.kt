package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.ExpenseHistoryEntity
import com.romandevyatov.bestfinance.data.entities.IncomeHistoryEntity
import com.romandevyatov.bestfinance.data.entities.WalletEntity
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

    fun updateNameAndDescriptionAndBalanceWalletById(updatedWalletBindingEntity: WalletEntity) = viewModelScope.launch(Dispatchers.IO) {
        val wallet = walletRepository.getWalletByIdAsync(updatedWalletBindingEntity.id)

        if (wallet != null) {
            val updatedWallet = wallet.copy(
                name = updatedWalletBindingEntity.name,
                description = updatedWalletBindingEntity.description,
                balance = updatedWalletBindingEntity.balance
            )

            updateWallet(updatedWallet)
        }
    }

    fun addOnlyWalletIncomeHistoryRecord(incomeHistoryEntity: IncomeHistoryEntity) = viewModelScope.launch(Dispatchers.IO) {
        incomeHistoryRepository.insertIncomeHistory(incomeHistoryEntity)
    }

    fun addOnlyWalletExpenseHistoryRecord(expenseHistoryEntity: ExpenseHistoryEntity) = viewModelScope.launch(Dispatchers.IO) {
        expenseHistoryRepository.insertExpenseHistory(expenseHistoryEntity)
    }

    private var deletedItem: WalletEntity? = null
    private val deletedItemList = mutableListOf<WalletEntity>()

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
