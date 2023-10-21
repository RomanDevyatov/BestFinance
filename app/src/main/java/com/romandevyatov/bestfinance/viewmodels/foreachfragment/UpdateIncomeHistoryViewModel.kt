package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.romandevyatov.bestfinance.data.entities.ExpenseHistory
import com.romandevyatov.bestfinance.data.entities.IncomeGroup
import com.romandevyatov.bestfinance.data.entities.IncomeHistory
import com.romandevyatov.bestfinance.data.entities.Wallet
import com.romandevyatov.bestfinance.data.entities.relations.IncomeGroupWithIncomeSubGroups
import com.romandevyatov.bestfinance.data.entities.relations.IncomeHistoryWithIncomeSubGroupAndWallet
import com.romandevyatov.bestfinance.data.repositories.IncomeGroupRepository
import com.romandevyatov.bestfinance.data.repositories.IncomeHistoryRepository
import com.romandevyatov.bestfinance.data.repositories.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateIncomeHistoryViewModel @Inject constructor(
    private val incomeHistoryRepository: IncomeHistoryRepository,
    private val incomeGroupRepository: IncomeGroupRepository,
    private val walletRepository: WalletRepository
): ViewModel() {

    val walletsNotArchivedLiveData: LiveData<List<Wallet>> = walletRepository.getAllWalletsNotArchivedLiveData()

    fun getIncomeHistoryWithIncomeSubGroupAndWalletById(incomeHistoryId: Long): LiveData<IncomeHistoryWithIncomeSubGroupAndWallet>? {
        return incomeHistoryRepository.getIncomeHistoryWithIncomeSubGroupAndWalletByIdLiveData(incomeHistoryId)
    }

    fun updateIncomeHistoryAndWallet(updatedIncomeHistory: IncomeHistory) = viewModelScope.launch(Dispatchers.IO) {
        incomeHistoryRepository.updateIncomeHistory(
            updatedIncomeHistory
        )

        val wallet = walletRepository.getWalletById(updatedIncomeHistory.walletId)
        if (wallet != null) {
            val updatedWallet = wallet.copy(
                balance = wallet.balance + updatedIncomeHistory.amount,
                input = wallet.input + updatedIncomeHistory.amount
                )
            walletRepository.updateWallet(updatedWallet)
        }
    }

    fun updateWallet(updatedWallet: Wallet) = viewModelScope.launch(Dispatchers.IO) {
        walletRepository.updateWallet(updatedWallet)
    }

    fun getIncomeGroupNotArchivedWithIncomeSubGroupsNotArchivedByIncomeGroupNameLiveData(name: String): LiveData<IncomeGroupWithIncomeSubGroups>? {
        return incomeGroupRepository.getIncomeGroupNotArchivedWithIncomeSubGroupsNotArchivedByIncomeGroupNameLiveData(name)
    }

    fun getAllIncomeGroupNotArchived(): LiveData<List<IncomeGroup>> {
        return incomeGroupRepository.getAllIncomeGroupNotArchivedLiveData()
    }

    fun getIncomeGroupById(incomeGroupId: Long): LiveData<IncomeGroup>? {
        return incomeGroupRepository.getIncomeGroupByIdLiveData(incomeGroupId)
    }

    private var deletedItem: IncomeHistory? = null

    fun deleteItem(id: Long) = viewModelScope.launch (Dispatchers.IO) {
        try {
            val itemToDelete = incomeHistoryRepository.getIncomeHistoryById(id)
            deletedItem = itemToDelete
            incomeHistoryRepository.deleteIncomeHistoryById(id)
        } catch (_: Exception) {

        }
    }

    fun undoDeleteItem() = viewModelScope.launch (Dispatchers.IO) {
        deletedItem?.let { itemToRestore ->
            try {
                incomeHistoryRepository.insertIncomeHistory(itemToRestore)
                deletedItem = null
            } catch (_: Exception) { }
        }
    }
}
