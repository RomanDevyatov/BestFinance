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

    fun getIncomeGroupNotArchivedWithIncomeSubGroupsNotArchivedByIncomeGroupNameLiveData(name: String): LiveData<IncomeGroupWithIncomeSubGroups?> {
        return incomeGroupRepository.getIncomeGroupNotArchivedWithIncomeSubGroupsNotArchivedByIncomeGroupNameLiveData(name)
    }

    fun insertIncomeGroup(incomeGroup: IncomeGroup) = viewModelScope.launch(Dispatchers.IO) {
        incomeGroupRepository.insertIncomeGroup(incomeGroup)
    }

    fun insertIncomeSubGroup(incomeSubGroup: IncomeSubGroup) = viewModelScope.launch(Dispatchers.IO) {
        val existingIncomeSubGroup = incomeSubGroupRepository.getIncomeSubGroupByNameAndIncomeGroupId(incomeSubGroup.name, incomeSubGroup.incomeGroupId)

        if (existingIncomeSubGroup == null) {
            incomeSubGroupRepository.insertIncomeSubGroup(incomeSubGroup)
        } else if (existingIncomeSubGroup.archivedDate != null) {
            incomeSubGroupRepository.unarchiveIncomeSubGroup(existingIncomeSubGroup)
        }
    }

    fun insertWallet(wallet: Wallet) = viewModelScope.launch(Dispatchers.IO) {
        walletRepository.insertWallet(wallet)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun archiveIncomeGroup(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        val incomeGroupWithIncomeSubGroups = getIncomeGroupWithIncomeSubGroupsByIncomeGroupIdNotArchived(id)
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

    private fun getIncomeGroupWithIncomeSubGroupsByIncomeGroupIdNotArchived(id: Long): IncomeGroupWithIncomeSubGroups? {
        return incomeGroupRepository.getIncomeGroupWithIncomeSubGroupsByIncomeGroupIdNotArchived(id)
    }

    // income history zone
    private fun insertIncomeHistory(incomeHistory: IncomeHistory) = viewModelScope.launch(Dispatchers.IO) {
        incomeHistoryRepository.insertIncomeHistory(incomeHistory)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addIncomeHistoryAndUpdateWallet(incomeSubGroupId: Long,
                                        amountBinding: Double,
                                        commentBinding: String,
                                        parsedLocalDateTime: LocalDateTime,
                                        walletId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            insertIncomeHistoryRecord(
                incomeSubGroupId,
                amountBinding,
                commentBinding,
                parsedLocalDateTime,
                walletId
            )

            val wallet = walletRepository.getWalletById(walletId)
            if (wallet != null) {
                val updatedWallet = wallet.copy(
                    balance = wallet.balance + amountBinding,
                    input = wallet.input + amountBinding
                )

                updateWallet(updatedWallet)
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun insertIncomeHistoryRecord(incomeSubGroupId: Long, amountBinding: Double, commentBinding: String, parsedLocalDateTime: LocalDateTime, walletId: Long) {
        insertIncomeHistory(
            IncomeHistory(
                incomeSubGroupId = incomeSubGroupId,
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

    private fun getWalletByNameNotArchived(walletName: String): Wallet? {
        return walletRepository.getWalletByNameNotArchived(walletName)
    }

    fun updateWallet(wallet: Wallet) = viewModelScope.launch(Dispatchers.IO) {
        walletRepository.updateWallet(wallet)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun archive(incomeSubGroupNameBinding: String, amountBinding: Double, commentBinding: String, parsedLocalDateTime: LocalDateTime, walletNameBinding: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val incomeSubGroup = incomeSubGroupRepository.getByNameNotArchived(incomeSubGroupNameBinding)
            val incomeGroupId = incomeSubGroup?.id
            if (incomeGroupId != null) {
                val wallet = getWalletByNameNotArchived(walletNameBinding)

                val walletId = wallet?.id
                if (walletId != null) {
                    insertIncomeHistoryRecord(
                        incomeGroupId,
                        amountBinding,
                        commentBinding,
                        parsedLocalDateTime,
                        walletId
                    )

                    updateWallet(walletId, wallet, amountBinding)
                }
            }
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
    fun archiveIncomeSubGroup(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        val incomeSubGroup = incomeSubGroupRepository.getByIdNotArchived(id)

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
    fun archiveWallet(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        val selectedWallet = walletRepository.getWalletById(id)

        if (selectedWallet != null) {
            val selectedWalletArchived = selectedWallet.copy(
                archivedDate = LocalDateTime.now()
            )

            walletRepository.updateWallet(selectedWalletArchived)
        }
    }
}
