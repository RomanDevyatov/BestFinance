package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.ExpenseGroupEntity
import com.romandevyatov.bestfinance.data.entities.ExpenseHistoryEntity
import com.romandevyatov.bestfinance.data.entities.WalletEntity
import com.romandevyatov.bestfinance.data.entities.relations.ExpenseGroupWithExpenseSubGroups
import com.romandevyatov.bestfinance.data.entities.relations.ExpenseHistoryWithExpenseSubGroupAndWallet
import com.romandevyatov.bestfinance.data.repositories.BaseCurrencyRatesRepository
import com.romandevyatov.bestfinance.data.repositories.ExpenseGroupRepository
import com.romandevyatov.bestfinance.data.repositories.ExpenseHistoryRepository
import com.romandevyatov.bestfinance.data.repositories.WalletRepository
import com.romandevyatov.bestfinance.utils.sharedpreferences.Storage
import com.romandevyatov.bestfinance.viewmodels.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateExpenseHistoryViewModel @Inject constructor(
    storage: Storage,
    private val expenseHistoryRepository: ExpenseHistoryRepository,
    private val expenseGroupRepository: ExpenseGroupRepository,
    private val walletRepository: WalletRepository,
    private val baseCurrencyRatesRepository: BaseCurrencyRatesRepository
): BaseViewModel(storage) {

    val currentDefaultCurrencySymbol: String = getDefaultCurrencySymbol()

    val walletsNotArchivedLiveData: LiveData<List<WalletEntity>> = walletRepository.getAllWalletsNotArchivedLiveData()

    fun getExpenseHistoryWithExpenseSubGroupAndWalletById(expenseHistoryId: Long): LiveData<ExpenseHistoryWithExpenseSubGroupAndWallet?> {
        return expenseHistoryRepository.getExpenseHistoryWithExpenseSubGroupAndWalletByIdLiveData(expenseHistoryId)
    }

    fun updateExpenseHistoryAndWallet(updatedExpenseHistoryEntity: ExpenseHistoryEntity) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val wallet = walletRepository.getWalletByIdAsync(updatedExpenseHistoryEntity.walletId)
            if (wallet != null) {
                val defaultCurrencyCode = getDefaultCurrencyCode()
                val pairName = defaultCurrencyCode + wallet.currencyCode
                val baseCurrencyRate =
                    baseCurrencyRatesRepository.getBaseCurrencyRateByPairName(pairName)

                if (baseCurrencyRate != null) {
                    val amountBase = updatedExpenseHistoryEntity.amount / baseCurrencyRate.value

                    val updatedAmountBaseIncomeHistory = updatedExpenseHistoryEntity.copy(
                        amountBase = amountBase
                    )
                    updateExpenseHistory(updatedAmountBaseIncomeHistory)


                    val updatedWallet = wallet.copy(
                        balance = wallet.balance - updatedAmountBaseIncomeHistory.amount,
                        output = wallet.output + updatedAmountBaseIncomeHistory.amount
                    )
                    walletRepository.updateWallet(updatedWallet)
                }
            }
        } catch (_: Exception) { }
    }

    fun updateExpenseHistory(expenseHistoryEntity: ExpenseHistoryEntity) = viewModelScope.launch(Dispatchers.IO) {
        expenseHistoryRepository.updateExpenseHistory(
            expenseHistoryEntity
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

    fun updateWallet(walletEntity: WalletEntity) = viewModelScope.launch (Dispatchers.IO) {
        walletRepository.updateWallet(walletEntity)
    }

    private var deletedItem: ExpenseHistoryEntity? = null

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
