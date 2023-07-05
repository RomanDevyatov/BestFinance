package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.db.entities.IncomeGroup
import com.romandevyatov.bestfinance.db.entities.IncomeHistory
import com.romandevyatov.bestfinance.db.entities.Wallet
import com.romandevyatov.bestfinance.db.entities.relations.IncomeGroupWithIncomeSubGroups
import com.romandevyatov.bestfinance.repositories.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class AddIncomeHistoryViewModel @Inject constructor(
    private val incomeGroupRepository: IncomeGroupRepository,
    private val incomeSubGroupRepository: IncomeSubGroupRepository,
    private val incomeHistoryRepository: IncomeHistoryRepository,
    private val walletRepository: WalletRepository
) : ViewModel() {

    // income group zone
    fun getAllIncomeGroupNotArchived(): LiveData<List<IncomeGroup>> {
        return incomeGroupRepository.getAllIncomeGroupNotArchivedLiveData()
    }

    fun getIncomeGroupWithIncomeSubGroupsByIncomeGroupNameAndNotArchived(name: String): LiveData<IncomeGroupWithIncomeSubGroups> {
        return incomeGroupRepository.getIncomeGroupWithIncomeSubGroupsByIncomeGroupNameAndNotArchivedLiveData(name)
    }

    // income history zone
    fun insertIncomeHistory(incomeHistory: IncomeHistory) = viewModelScope.launch(Dispatchers.IO) {
        incomeHistoryRepository.insertIncomeHistory(incomeHistory)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addIncomeHistory(incomeSubGroupNameBinding: String, amountBinding: Double, commentBinding: String, dateBinding: String, walletNameBinding: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val incomeSubGroup = incomeSubGroupRepository.getByName(incomeSubGroupNameBinding)
            val incomeGroupId = incomeSubGroup.id!!.toLong()

            val wallet = getWalletByNameNotArchived(walletNameBinding)
            val walletId = wallet.id!!

            insertHistoryRecord(incomeGroupId, amountBinding, commentBinding, dateBinding, walletId)

            updateWallet(walletId, wallet, amountBinding)
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun insertHistoryRecord(incomeGroupId: Long, amountBinding: Double, commentBinding: String, dateBinding: String, walletId: Long) {
        val iso8601DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        insertIncomeHistory(
            IncomeHistory(
                incomeSubGroupId = incomeGroupId,
                amount = amountBinding,
                description = commentBinding,
                createdDate = OffsetDateTime.from(iso8601DateTimeFormatter.parse(dateBinding)),
                walletId = walletId
            )
        )
    }

    private fun updateWallet(walletId: Long, wallet: Wallet, amountBinding: Double) {
        updateWallet(
            Wallet(
                id = walletId,
                name = wallet.name,
                balance = wallet.balance + amountBinding,
                archivedDate = wallet.archivedDate,
                input = wallet.input + amountBinding,
                output = wallet.output,
                description = wallet.description
            )
        )
    }

    val walletsNotArchivedLiveData: LiveData<List<Wallet>> = walletRepository.getAllWalletsNotArchivedLiveData()

    private fun getWalletByNameNotArchived(walletName: String): Wallet {
        return walletRepository.getWalletByNameNotArchived(walletName)
    }

    fun updateWallet(wallet: Wallet) = viewModelScope.launch(Dispatchers.IO) {
        walletRepository.updateWallet(wallet)
    }

}
