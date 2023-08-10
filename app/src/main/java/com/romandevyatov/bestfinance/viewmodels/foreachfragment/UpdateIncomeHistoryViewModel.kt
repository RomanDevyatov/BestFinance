package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.IncomeGroup
import com.romandevyatov.bestfinance.data.entities.IncomeHistory
import com.romandevyatov.bestfinance.data.entities.IncomeSubGroup
import com.romandevyatov.bestfinance.data.entities.Wallet
import com.romandevyatov.bestfinance.data.entities.relations.IncomeGroupWithIncomeSubGroups
import com.romandevyatov.bestfinance.data.entities.relations.IncomeHistoryWithIncomeSubGroupAndWallet
import com.romandevyatov.bestfinance.data.repositories.IncomeGroupRepository
import com.romandevyatov.bestfinance.data.repositories.IncomeHistoryRepository
import com.romandevyatov.bestfinance.data.repositories.IncomeSubGroupRepository
import com.romandevyatov.bestfinance.data.repositories.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateIncomeHistoryViewModel @Inject constructor(
    private val incomeHistoryRepository: IncomeHistoryRepository,
    private val incomeGroupRepository: IncomeGroupRepository,
    private val incomeSubGroupRepository: IncomeSubGroupRepository,
    private val walletRepository: WalletRepository
): ViewModel() {

    val walletsNotArchivedLiveData: LiveData<List<Wallet>> = walletRepository.getAllWalletsNotArchivedLiveData()

    fun getIncomeHistoryWithIncomeSubGroupAndWalletById(incomeHistoryId: Long): LiveData<IncomeHistoryWithIncomeSubGroupAndWallet> {
        return incomeHistoryRepository.getIncomeHistoryWithIncomeSubGroupAndWalletByIdLiveData(incomeHistoryId)
    }

    fun getIncomeHistoryByIdLiveData(incomeHistoryId: Long): LiveData<IncomeHistory> {
        return incomeHistoryRepository.getIncomeHistoryByIdLiveData(incomeHistoryId)
    }

    fun updateIncomeHistoryAndWallet(updatedIncomeHistory: IncomeHistory) = viewModelScope.launch(Dispatchers.IO) {
        incomeHistoryRepository.updateIncomeHistory(
            updatedIncomeHistory
        )

        val wallet = walletRepository.getWalletById(updatedIncomeHistory.walletId)
        updateWallet(
            Wallet(
                id = wallet.id,
                name = wallet.name,
                balance = wallet.balance + updatedIncomeHistory.amount,
                input = wallet.input + updatedIncomeHistory.amount,
                output = wallet.output,
                description = wallet.description,
                archivedDate = wallet.archivedDate
            )
        )
    }

    fun updateWallet(updatedWallet: Wallet) = viewModelScope.launch(Dispatchers.IO){
        walletRepository.updateWallet(
            updatedWallet
        )
    }


    fun getIncomeGroupNotArchivedWithIncomeSubGroupsNotArchivedByIncomeGroupNameLiveData(name: String): LiveData<IncomeGroupWithIncomeSubGroups> {
        return incomeGroupRepository.getIncomeGroupNotArchivedWithIncomeSubGroupsNotArchivedByIncomeGroupNameLiveData(name)
    }

    fun getAllIncomeGroupNotArchived(): LiveData<List<IncomeGroup>> {
        return incomeGroupRepository.getAllIncomeGroupNotArchivedLiveData()
    }

    fun getIncomeSubGroupByIdLiveData(incomeSubGroupId: Long?): LiveData<IncomeSubGroup>? {
        return incomeSubGroupRepository.getIncomeSubGroupByIdLiveData(incomeSubGroupId)
    }

    fun getIncomeGroupById(incomeGroupId: Long): LiveData<IncomeGroup>? {
        return incomeGroupRepository.getIncomeGroupByIdLiveData(incomeGroupId)
    }

}