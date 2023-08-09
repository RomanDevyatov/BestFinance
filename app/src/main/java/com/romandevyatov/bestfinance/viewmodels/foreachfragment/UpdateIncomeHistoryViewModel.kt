package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.IncomeGroup
import com.romandevyatov.bestfinance.data.entities.IncomeHistory
import com.romandevyatov.bestfinance.data.entities.IncomeSubGroup
import com.romandevyatov.bestfinance.data.entities.Wallet
import com.romandevyatov.bestfinance.data.entities.relations.IncomeGroupWithIncomeSubGroups
import com.romandevyatov.bestfinance.data.repositories.IncomeGroupRepository
import com.romandevyatov.bestfinance.data.repositories.IncomeHistoryRepository
import com.romandevyatov.bestfinance.data.repositories.IncomeSubGroupRepository
import com.romandevyatov.bestfinance.data.repositories.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class UpdateIncomeHistoryViewModel @Inject constructor(
    private val incomeHistoryRepository: IncomeHistoryRepository,
    private val incomeGroupRepository: IncomeGroupRepository,
    private val incomeSubGroupRepository: IncomeSubGroupRepository,
    private val walletRepository: WalletRepository
): ViewModel() {

    val walletsNotArchivedLiveData: LiveData<List<Wallet>> = walletRepository.getAllWalletsNotArchivedLiveData()

    fun getIncomeHistoryByIdLiveData(incomeHistoryId: Long): LiveData<IncomeHistory> {
        return incomeHistoryRepository.getIncomeHistoryByIdLiveData(incomeHistoryId)
    }

    fun updateIncomeHistory(incomeHistory: IncomeHistory) = viewModelScope.launch(Dispatchers.IO){
        incomeHistoryRepository.updateIncomeHistory(
            incomeHistory
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