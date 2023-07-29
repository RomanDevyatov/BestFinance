package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import android.os.Build
import androidx.annotation.RequiresApi
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

    fun getIncomeGroupNotArchivedWithIncomeSubGroupsNotArchivedByIncomeGroupNameLiveData(name: String): LiveData<IncomeGroupWithIncomeSubGroups> {
        return incomeGroupRepository.getIncomeGroupNotArchivedWithIncomeSubGroupsNotArchivedByIncomeGroupNameLiveData(name)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun archiveIncomeGroup(name: String) = viewModelScope.launch(Dispatchers.IO) {
        val incomeGroupWithIncomeSubGroups = getIncomeGroupWithIncomeSubGroupsByIncomeGroupNameNotArchived(name)
        if (incomeGroupWithIncomeSubGroups != null) {
            val incomeGroup = incomeGroupWithIncomeSubGroups.incomeGroup
            val incomeSubGroups = incomeGroupWithIncomeSubGroups.incomeSubGroups

            val archivedDate = LocalDateTime.now()

            val incomeGroupArchived = IncomeGroup(
                id = incomeGroup.id,
                name = incomeGroup.name,
                isPassive = incomeGroup.isPassive,
                description = incomeGroup.description,
                archivedDate = archivedDate
            )
            updateIncomeGroup(incomeGroupArchived)

            incomeSubGroups.forEach { subGroup ->
                val incomeSubGroupArchived = IncomeSubGroup(
                    id = subGroup.id,
                    name = subGroup.name,
                    description = subGroup.description,
                    incomeGroupId = subGroup.incomeGroupId,
                    archivedDate = archivedDate
                )

                updateIncomeSubGroup(incomeSubGroupArchived)
            }
        }
    }

    private fun getIncomeGroupWithIncomeSubGroupsByIncomeGroupNameNotArchived(name: String): IncomeGroupWithIncomeSubGroups {
        return incomeGroupRepository.getIncomeGroupWithIncomeSubGroupsByIncomeGroupNameNotArchived(name)
    }

    // income history zone
    fun insertIncomeHistory(incomeHistory: IncomeHistory) = viewModelScope.launch(Dispatchers.IO) {
        incomeHistoryRepository.insertIncomeHistory(incomeHistory)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addIncomeHistory(incomeSubGroupNameBinding: String, amountBinding: Double, commentBinding: String, parsedLocalDateTime: LocalDateTime, walletNameBinding: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val incomeSubGroup = incomeSubGroupRepository.getByNameNotArchived(incomeSubGroupNameBinding)
            val incomeGroupId = incomeSubGroup.id!!.toLong()

            val wallet = getWalletByNameNotArchived(walletNameBinding)
            val walletId = wallet.id!!

            insertIncomeHistoryRecord(incomeGroupId, amountBinding, commentBinding, parsedLocalDateTime, walletId)

            updateWallet(walletId, wallet, amountBinding)
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun insertIncomeHistoryRecord(incomeGroupId: Long, amountBinding: Double, commentBinding: String, parsedLocalDateTime: LocalDateTime, walletId: Long) {
        insertIncomeHistory(
            IncomeHistory(
                incomeSubGroupId = incomeGroupId,
                amount = amountBinding,
                comment = commentBinding,
                date = parsedLocalDateTime,
                walletId = walletId,
                createdDate = LocalDateTime.now()
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun archive(incomeSubGroupNameBinding: String, amountBinding: Double, commentBinding: String, parsedLocalDateTime: LocalDateTime, walletNameBinding: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val incomeSubGroup = incomeSubGroupRepository.getByNameNotArchived(incomeSubGroupNameBinding)
            val incomeGroupId = incomeSubGroup.id!!.toLong()

            val wallet = getWalletByNameNotArchived(walletNameBinding)
            val walletId = wallet.id!!

            insertIncomeHistoryRecord(incomeGroupId, amountBinding, commentBinding, parsedLocalDateTime, walletId)

            updateWallet(walletId, wallet, amountBinding)
        }

    }

    fun updateIncomeSubGroup(incomeSubGroup: IncomeSubGroup) {
        viewModelScope.launch(Dispatchers.IO) {
            incomeSubGroupRepository.updateIncomeSubGroup(incomeSubGroup)
        }
    }

    fun updateIncomeGroup(incomeGroupArchived: IncomeGroup) {
        viewModelScope.launch(Dispatchers.IO) {
            incomeGroupRepository.updateIncomeGroup(incomeGroupArchived)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun archiveIncomeSubGroup(name: String) = viewModelScope.launch(Dispatchers.IO) {
        val incomeSubGroup = incomeSubGroupRepository.getByNameNotArchived(name)

        if (incomeSubGroup != null) {
            val incomeSubGroupArchived = IncomeSubGroup(
                id = incomeSubGroup.id,
                name = incomeSubGroup.name,
                description = incomeSubGroup.description,
                incomeGroupId = incomeSubGroup.incomeGroupId,
                archivedDate = LocalDateTime.now()
            )

            incomeSubGroupRepository.updateIncomeSubGroup(incomeSubGroupArchived)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun archiveWallet(name: String) = viewModelScope.launch(Dispatchers.IO) {
        val selectedWallet = walletRepository.getWalletByNameNotArchived(name)

        val selectedWalletArchived = Wallet(
            id = selectedWallet.id,
            name = selectedWallet.name,
            balance = selectedWallet.balance,
            archivedDate = LocalDateTime.now(),
            input = selectedWallet.input,
            output = selectedWallet.output,
            description = selectedWallet.description
        )

        walletRepository.updateWallet(selectedWalletArchived)

//        Snackbar.make(viewHolder.itemView, "Wallet with name ${selectedWallet.name} is archived", Snackbar.LENGTH_LONG).apply {
//            setAction("UNDO") {
//                walletRepository.updateWallet(selectedWallet)
//            }
//            show()
//        }

    }

}
