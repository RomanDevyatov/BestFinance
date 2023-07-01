package com.romandevyatov.bestfinance.viewmodels.newapproach

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.romandevyatov.bestfinance.db.entities.IncomeGroup
import com.romandevyatov.bestfinance.db.entities.IncomeHistory
import com.romandevyatov.bestfinance.db.entities.Wallet
import com.romandevyatov.bestfinance.db.entities.relations.IncomeGroupWithIncomeSubGroups
import com.romandevyatov.bestfinance.repositories.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddIncomeHistoryViewModel @Inject constructor(
    private val incomeGroupRepository: IncomeGroupRepository,
    private val incomeSubGroupRepository: IncomeSubGroupRepository,
    private val incomeHistoryRepository: IncomeHistoryRepository,
    private val walletRepository: WalletRepository
) : ViewModel() {

    // income group zone
    val incomeHistoryLiveData: LiveData<List<IncomeHistory>> = incomeHistoryRepository.getAllIncomeHistory()

    fun getAllIncomeGroupNotArchived(): LiveData<List<IncomeGroup>> {
        return incomeGroupRepository.getAllIncomeGroupNotArchived()
    }

    fun getAllIncomeGroupWithIncomeSubGroupsByIncomeGroupNameAndNotArchived(name: String): LiveData<IncomeGroupWithIncomeSubGroups> {
        return incomeGroupRepository.getAllIncomeGroupWithIncomeSubGroupsByIncomeGroupNameAndNotArchived(name)
    }

    // income sub group zone


    // income history zone


    // wallet zone
    val walletsLiveData:            LiveData<List<Wallet>> = walletRepository.getAllWallets()
    val notArchivedWalletsLiveData: LiveData<List<Wallet>> = walletRepository.getAllWalletsNotArchivedLiveData()


}