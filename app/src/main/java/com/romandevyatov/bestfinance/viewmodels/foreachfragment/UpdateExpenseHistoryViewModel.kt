package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.ExpenseGroupEntity
import com.romandevyatov.bestfinance.data.entities.ExpenseHistory
import com.romandevyatov.bestfinance.data.entities.Wallet
import com.romandevyatov.bestfinance.data.entities.relations.ExpenseGroupWithExpenseSubGroups
import com.romandevyatov.bestfinance.data.entities.relations.ExpenseHistoryWithExpenseSubGroupAndWallet
import com.romandevyatov.bestfinance.data.repositories.ExpenseGroupRepository
import com.romandevyatov.bestfinance.data.repositories.ExpenseHistoryRepository
import com.romandevyatov.bestfinance.data.repositories.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateExpenseHistoryViewModel @Inject constructor(
    private val expenseHistoryRepository: ExpenseHistoryRepository,
    private val expenseGroupRepository: ExpenseGroupRepository,
    private val walletRepository: WalletRepository
): ViewModel() {

    val walletsNotArchivedLiveData: LiveData<List<Wallet>> = walletRepository.getAllWalletsNotArchivedLiveData()

    fun getExpenseHistoryWithExpenseSubGroupAndWalletById(expenseHistoryId: Long): LiveData<ExpenseHistoryWithExpenseSubGroupAndWallet?> {
        return expenseHistoryRepository.getExpenseHistoryWithExpenseSubGroupAndWalletByIdLiveData(expenseHistoryId)
    }

    fun updateExpenseHistoryAndWallet(updatedExpenseHistory: ExpenseHistory) = viewModelScope.launch(Dispatchers.IO) {
        try {
            updateExpenseHistory(updatedExpenseHistory)

            val wallet = walletRepository.getWalletById(updatedExpenseHistory.walletId)
            if (wallet != null) {
                val updatedWallet = wallet.copy(
                    balance = wallet.balance - updatedExpenseHistory.amount,
                    output = wallet.output + updatedExpenseHistory.amount
                )
                walletRepository.updateWallet(updatedWallet)
            }
        } catch (_: Exception) { }
    }

    fun updateExpenseHistory(expenseHistory: ExpenseHistory) = viewModelScope.launch(Dispatchers.IO) {
        expenseHistoryRepository.updateExpenseHistory(
            expenseHistory
        )
    }

    fun getExpenseGroupNotArchivedWithExpenseSubGroupsNotArchivedByExpenseGroupNameLiveData(name: String): LiveData<ExpenseGroupWithExpenseSubGroups?> {
        return expenseGroupRepository.getExpenseGroupNotArchivedWithExpenseSubGroupsNotArchivedByExpenseGroupNameLiveData(name)
    }

    fun getAllExpenseGroupNotArchived(): LiveData<List<ExpenseGroupEntity>> {
        return expenseGroupRepository.getAllExpenseGroupNotArchivedLiveData()
    }

    fun getExpenseGroupByIdLiveData(expenseGroupId: Long): LiveData<ExpenseGroupEntity?> {
        return expenseGroupRepository.getExpenseGroupByIdLiveData(expenseGroupId)
    }

    fun updateWallet(wallet: Wallet) = viewModelScope.launch (Dispatchers.IO) {
        walletRepository.updateWallet(wallet)
    }

    private var deletedItem: ExpenseHistory? = null

    fun deleteItem(id: Long) = viewModelScope.launch (Dispatchers.IO) {
        try {
            val itemToDelete = expenseHistoryRepository.getExpenseHistoryById(id)
            deletedItem = itemToDelete
            expenseHistoryRepository.deleteExpenseHistory(id)
        } catch (_: Exception) { }
    }

    fun undoDeleteItem() = viewModelScope.launch (Dispatchers.IO) {
        deletedItem?.let { itemToRestore ->
            try {
                expenseHistoryRepository.insertExpenseHistory(itemToRestore)
                deletedItem = null
            } catch (_: Exception) { }
        }
    }
}
