package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.ExpenseGroup
import com.romandevyatov.bestfinance.data.entities.ExpenseHistory
import com.romandevyatov.bestfinance.data.entities.ExpenseSubGroup
import com.romandevyatov.bestfinance.data.entities.Wallet
import com.romandevyatov.bestfinance.data.entities.relations.ExpenseGroupWithExpenseSubGroups
import com.romandevyatov.bestfinance.data.repositories.ExpenseGroupRepository
import com.romandevyatov.bestfinance.data.repositories.ExpenseHistoryRepository
import com.romandevyatov.bestfinance.data.repositories.ExpenseSubGroupRepository
import com.romandevyatov.bestfinance.data.repositories.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class AddExpenseHistoryViewModel @Inject constructor(
    private val expenseGroupRepository: ExpenseGroupRepository,
    private val expenseSubGroupRepository: ExpenseSubGroupRepository,
    private val expenseHistoryRepository: ExpenseHistoryRepository,
    private val walletRepository: WalletRepository
) : ViewModel() {

    // expense group zone
    fun getAllExpenseGroupNotArchivedLiveData(): LiveData<List<ExpenseGroup>> {
        return expenseGroupRepository.getAllExpenseGroupsNotArchivedLiveData()
    }

    fun getExpenseGroupNotArchivedWithExpenseSubGroupsNotArchivedByExpenseGroupNameLiveData(name: String): LiveData<ExpenseGroupWithExpenseSubGroups?> {
        return expenseGroupRepository.getExpenseGroupNotArchivedWithExpenseSubGroupsNotArchivedByExpenseGroupNameLiveData(name)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun archiveExpenseGroup(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        val expenseGroupWithExpenseSubGroups = getExpenseGroupWithExpenseSubGroupsByExpenseGroupIdNotArchived(id)
        if (expenseGroupWithExpenseSubGroups != null) {
            val expenseGroup = expenseGroupWithExpenseSubGroups.expenseGroup
            val expenseSubGroups = expenseGroupWithExpenseSubGroups.expenseSubGroups

            val archivedDate = LocalDateTime.now()

            val expenseGroupArchived = ExpenseGroup(
                id = expenseGroup.id,
                name = expenseGroup.name,
                description = expenseGroup.description,
                archivedDate = archivedDate
            )
            updateExpenseGroup(expenseGroupArchived)

            expenseSubGroups.forEach { subGroup ->
                val expenseSubGroupArchived = ExpenseSubGroup(
                    id = subGroup.id,
                    name = subGroup.name,
                    description = subGroup.description,
                    expenseGroupId = subGroup.expenseGroupId,
                    archivedDate = archivedDate
                )

                updateExpenseSubGroup(expenseSubGroupArchived)
            }
        }
    }

    private fun getExpenseGroupWithExpenseSubGroupsByExpenseGroupIdNotArchived(id: Long): ExpenseGroupWithExpenseSubGroups? {
        return expenseGroupRepository.getExpenseGroupWithExpenseSubGroupsByExpenseGroupIdNotArchived(id)
    }

    // expense history zone
    fun insertExpenseHistory(expenseHistory: ExpenseHistory) = viewModelScope.launch(Dispatchers.IO) {
        expenseHistoryRepository.insertExpenseHistory(expenseHistory)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addExpenseHistoryAndUpdateWallet(expenseSubGroupId: Long,
                                         amountBinding: Double,
                                         commentBinding: String,
                                         parsedLocalDateTime: LocalDateTime,
                                         walletId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            insertExpenseHistoryRecord(
                expenseSubGroupId,
                amountBinding,
                commentBinding,
                parsedLocalDateTime,
                walletId
            )

            val wallet = walletRepository.getWalletById(walletId)
            if (wallet != null) {
                val updatedWallet = wallet.copy(
                    balance = wallet.balance - amountBinding,
                    output = wallet.output + amountBinding
                )

                updateWallet(updatedWallet)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun insertExpenseHistoryRecord(expenseGroupId: Long, amountBinding: Double, commentBinding: String, parsedLocalDateTime: LocalDateTime, walletId: Long) {
        insertExpenseHistory(
            ExpenseHistory(
                expenseSubGroupId = expenseGroupId,
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
                balance = wallet.balance - amountBinding,
                archivedDate = wallet.archivedDate,
                input = wallet.input,
                output = wallet.output + amountBinding,
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
    fun archive(expenseSubGroupNameBinding: String, amountBinding: Double, commentBinding: String, parsedLocalDateTime: LocalDateTime, walletNameBinding: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val expenseSubGroup = expenseSubGroupRepository.getExpenseSubGroupByNameNotArchived(expenseSubGroupNameBinding)
            if (expenseSubGroup != null) {
                val expenseSubGroupId = expenseSubGroup.id

                if (expenseSubGroupId != null) {
                    val wallet = getWalletByNameNotArchived(walletNameBinding)

                    if (wallet != null) {
                        val walletId = wallet.id!!

                        insertExpenseHistoryRecord(
                            expenseSubGroupId,
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

    }

    fun updateExpenseSubGroup(expenseSubGroup: ExpenseSubGroup) {
        viewModelScope.launch(Dispatchers.IO) {
            expenseSubGroupRepository.updateExpenseSubGroup(expenseSubGroup)
        }
    }

    fun updateExpenseGroup(expenseGroupArchived: ExpenseGroup) {
        viewModelScope.launch(Dispatchers.IO) {
            expenseGroupRepository.updateExpenseGroup(expenseGroupArchived)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun archiveExpenseSubGroup(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        val expenseSubGroup = expenseSubGroupRepository.getExpenseSubGroupByIdNotArchived(id)

        if (expenseSubGroup != null) {
            val expenseSubGroupArchived = ExpenseSubGroup(
                id = expenseSubGroup.id,
                name = expenseSubGroup.name,
                description = expenseSubGroup.description,
                expenseGroupId = expenseSubGroup.expenseGroupId,
                archivedDate = LocalDateTime.now()
            )

            expenseSubGroupRepository.updateExpenseSubGroup(expenseSubGroupArchived)
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

    fun insertExpenseGroup(expenseGroup: ExpenseGroup) = viewModelScope.launch(Dispatchers.IO) {
        expenseGroupRepository.insertExpenseGroup(expenseGroup)
    }

    fun insertExpenseSubGroup(expenseSubGroup: ExpenseSubGroup) = viewModelScope.launch(Dispatchers.IO) {
        val existingExpenseSubGroup = expenseSubGroupRepository.getExpenseSubGroupByNameAndExpenseGroupId(expenseSubGroup.name, expenseSubGroup.expenseGroupId)

        if (existingExpenseSubGroup == null) {
            expenseSubGroupRepository.insertExpenseSubGroup(expenseSubGroup)
        } else if (existingExpenseSubGroup.archivedDate != null) {
            expenseSubGroupRepository.unarchiveExpenseSubGroup(existingExpenseSubGroup)
        }
    }

    fun insertWallet(wallet: Wallet) = viewModelScope.launch(Dispatchers.IO) {
        walletRepository.insertWallet(wallet)
    }
}
