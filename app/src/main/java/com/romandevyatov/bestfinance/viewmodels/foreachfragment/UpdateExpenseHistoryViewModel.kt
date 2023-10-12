package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.ExpenseGroup
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

    fun getExpenseHistoryWithExpenseSubGroupAndWalletById(expenseHistoryId: Long): LiveData<ExpenseHistoryWithExpenseSubGroupAndWallet> {
        return expenseHistoryRepository.getExpenseHistoryWithExpenseSubGroupAndWalletByIdLiveData(expenseHistoryId)
    }

    fun updateExpenseHistoryAndWallet(updatedExpenseHistory: ExpenseHistory) = viewModelScope.launch(Dispatchers.IO) {
        updateExpenseHistory(
            updatedExpenseHistory
        )

        val wallet = walletRepository.getWalletById(updatedExpenseHistory.walletId)
        if (wallet != null) {
            val updatedWallet = wallet.copy(
                balance = wallet.balance - updatedExpenseHistory.amount,
                output = wallet.output + updatedExpenseHistory.amount
            )
            walletRepository.updateWallet(updatedWallet)
        }
    }

    fun updateExpenseHistory(expenseHistory: ExpenseHistory) = viewModelScope.launch(Dispatchers.IO) {
        expenseHistoryRepository.updateExpenseHistory(
            expenseHistory
        )
    }

    fun getExpenseGroupNotArchivedWithExpenseSubGroupsNotArchivedByExpenseGroupNameLiveData(name: String): LiveData<ExpenseGroupWithExpenseSubGroups> {
        return expenseGroupRepository.getExpenseGroupNotArchivedWithExpenseSubGroupsNotArchivedByExpenseGroupNameLiveData(name)
    }

    fun getAllExpenseGroupNotArchived(): LiveData<List<ExpenseGroup>> {
        return expenseGroupRepository.getAllExpenseGroupNotArchivedLiveData()
    }

    fun getExpenseGroupByIdLiveData(expenseGroupId: Long): LiveData<ExpenseGroup>? {
        return expenseGroupRepository.getExpenseGroupByIdLiveData(expenseGroupId)
    }

    fun updateWallet(wallet: Wallet) = viewModelScope.launch (Dispatchers.IO) {
        walletRepository.updateWallet(wallet)
    }

}
