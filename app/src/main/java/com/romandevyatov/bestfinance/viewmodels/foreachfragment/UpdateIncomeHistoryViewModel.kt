package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.IncomeGroupEntity
import com.romandevyatov.bestfinance.data.entities.IncomeHistoryEntity
import com.romandevyatov.bestfinance.data.entities.WalletEntity
import com.romandevyatov.bestfinance.data.entities.relations.IncomeGroupWithIncomeSubGroups
import com.romandevyatov.bestfinance.data.entities.relations.IncomeHistoryWithIncomeSubGroupAndWallet
import com.romandevyatov.bestfinance.data.repositories.BaseCurrencyRatesRepository
import com.romandevyatov.bestfinance.data.repositories.IncomeGroupRepository
import com.romandevyatov.bestfinance.data.repositories.IncomeHistoryRepository
import com.romandevyatov.bestfinance.data.repositories.WalletRepository
import com.romandevyatov.bestfinance.utils.sharedpreferences.Storage
import com.romandevyatov.bestfinance.viewmodels.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateIncomeHistoryViewModel @Inject constructor(
    storage: Storage,
    private val incomeHistoryRepository: IncomeHistoryRepository,
    private val incomeGroupRepository: IncomeGroupRepository,
    private val walletRepository: WalletRepository,
    private val baseCurrencyRatesRepository: BaseCurrencyRatesRepository
): BaseViewModel(storage) {

    val currentDefaultCurrencySymbol: String = getDefaultCurrencySymbol()

    val walletsNotArchivedLiveData: LiveData<List<WalletEntity>> = walletRepository.getAllWalletsNotArchivedLiveData()

    fun getIncomeHistoryWithIncomeSubGroupAndWalletById(incomeHistoryId: Long): LiveData<IncomeHistoryWithIncomeSubGroupAndWallet?> {
        return incomeHistoryRepository.getIncomeHistoryWithIncomeSubGroupAndWalletByIdLiveData(incomeHistoryId)
    }

    fun updateIncomeHistoryAndWallet(updatedIncomeHistoryEntity: IncomeHistoryEntity) = viewModelScope.launch(Dispatchers.IO) {
        val wallet = walletRepository.getWalletByIdAsync(updatedIncomeHistoryEntity.walletId)
        if (wallet != null) {
            val defaultCurrencyCode = getDefaultCurrencyCode()
            val pairName = defaultCurrencyCode + wallet.currencyCode
            val baseCurrencyRate = baseCurrencyRatesRepository.getBaseCurrencyRateByPairName(pairName)

            if (baseCurrencyRate != null) {
                val amountBase = updatedIncomeHistoryEntity.amount / baseCurrencyRate.value

                val updatedAmountBaseIncomeHistory = updatedIncomeHistoryEntity.copy(
                    amountBase = amountBase
                )

                incomeHistoryRepository.updateIncomeHistory(
                    updatedAmountBaseIncomeHistory
                )

                val updatedWallet = wallet.copy(
                    balance = wallet.balance + updatedAmountBaseIncomeHistory.amount,
                    input = wallet.input + updatedAmountBaseIncomeHistory.amount
                )
                walletRepository.updateWallet(updatedWallet)
            }
        }
    }

    fun getWalletById(id: Long): LiveData<WalletEntity?> {
        return walletRepository.getWalletByIdLiveData(id)
    }

    fun updateWallet(updatedWalletEntity: WalletEntity) = viewModelScope.launch(Dispatchers.IO) {
        walletRepository.updateWallet(updatedWalletEntity)
    }

    fun getIncomeGroupNotArchivedWithIncomeSubGroupsNotArchivedByIncomeGroupNameLiveData(name: String): LiveData<IncomeGroupWithIncomeSubGroups?> {
        return incomeGroupRepository.getIncomeGroupNotArchivedWithIncomeSubGroupsNotArchivedByIncomeGroupNameLiveData(name)
    }

    fun getAllIncomeGroupNotArchived(): LiveData<List<IncomeGroupEntity>> {
        return incomeGroupRepository.getAllIncomeGroupNotArchivedLiveData()
    }

    fun getIncomeGroupById(incomeGroupId: Long): LiveData<IncomeGroupEntity?> {
        return incomeGroupRepository.getIncomeGroupByIdLiveData(incomeGroupId)
    }

    private var deletedItem: IncomeHistoryEntity? = null

    fun deleteItem(id: Long) = viewModelScope.launch (Dispatchers.IO) {
        try {
            val itemToDelete = incomeHistoryRepository.getIncomeHistoryById(id)
            deletedItem = itemToDelete
            incomeHistoryRepository.deleteIncomeHistoryById(id)
        } catch (_: Exception) { }
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
