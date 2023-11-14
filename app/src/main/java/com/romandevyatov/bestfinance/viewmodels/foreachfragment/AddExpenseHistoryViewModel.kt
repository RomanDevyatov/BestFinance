package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.ExpenseGroupEntity
import com.romandevyatov.bestfinance.data.entities.ExpenseHistory
import com.romandevyatov.bestfinance.data.entities.ExpenseSubGroup
import com.romandevyatov.bestfinance.data.entities.Wallet
import com.romandevyatov.bestfinance.data.entities.relations.ExpenseGroupWithExpenseSubGroups
import com.romandevyatov.bestfinance.data.repositories.*
import com.romandevyatov.bestfinance.utils.TextFormatter.roundDoubleToTwoDecimalPlaces
import com.romandevyatov.bestfinance.utils.sharedpreferences.Storage
import com.romandevyatov.bestfinance.viewmodels.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class AddExpenseHistoryViewModel @Inject constructor(
    storage: Storage,
    private val expenseGroupRepository: ExpenseGroupRepository,
    private val expenseSubGroupRepository: ExpenseSubGroupRepository,
    private val expenseHistoryRepository: ExpenseHistoryRepository,
    private val walletRepository: WalletRepository,
    private val baseRatesRepository: BaseCurrencyRatesRepository
) : BaseViewModel(storage) {

    // expense group zone
    fun getAllExpenseGroupNotArchivedLiveData(): LiveData<List<ExpenseGroupEntity>> {
        return expenseGroupRepository.getAllExpenseGroupsNotArchivedLiveData()
    }

    fun getExpenseGroupNotArchivedWithExpenseSubGroupsNotArchivedByExpenseGroupNameLiveData(name: String): LiveData<ExpenseGroupWithExpenseSubGroups?> {
        return expenseGroupRepository.getExpenseGroupNotArchivedWithExpenseSubGroupsNotArchivedByExpenseGroupNameLiveData(name)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun archiveExpenseGroup(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        val expenseGroupWithExpenseSubGroups = getExpenseGroupWithExpenseSubGroupsByExpenseGroupIdNotArchived(id)
        if (expenseGroupWithExpenseSubGroups != null) {
            val expenseGroup = expenseGroupWithExpenseSubGroups.expenseGroupEntity
            val expenseSubGroups = expenseGroupWithExpenseSubGroups.expenseSubGroups

            val archivedDate = LocalDateTime.now()

            val expenseGroupEntityArchived = ExpenseGroupEntity(
                id = expenseGroup.id,
                name = expenseGroup.name,
                description = expenseGroup.description,
                archivedDate = archivedDate
            )
            updateExpenseGroup(expenseGroupEntityArchived)

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
            val wallet = walletRepository.getWalletByIdAsync(walletId)
            if (wallet != null) {
                val defaultCurrencyCode = getDefaultCurrencyCode()
                val pairName = defaultCurrencyCode + wallet.currencyCode
                val baseCurrencyRate = baseRatesRepository.getBaseCurrencyRateByPairName(pairName)
                if (baseCurrencyRate != null) {
                    val amountBase = roundDoubleToTwoDecimalPlaces(amountBinding / baseCurrencyRate.value)

                    insertExpenseHistoryRecord(
                        expenseSubGroupId,
                        amountBinding,
                        commentBinding,
                        parsedLocalDateTime,
                        walletId,
                        amountBase
                    )


                    val updatedWallet = wallet.copy(
                        balance = wallet.balance - amountBinding,
                        output = wallet.output + amountBinding
                    )

                    updateWallet(updatedWallet)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun insertExpenseHistoryRecord(
        expenseGroupId: Long,
        amountBinding: Double,
        commentBinding: String,
        parsedLocalDateTime: LocalDateTime,
        walletId: Long,
        amountBase: Double
    ) {
        insertExpenseHistory(
            ExpenseHistory(
                expenseSubGroupId = expenseGroupId,
                amount = amountBinding,
                comment = commentBinding,
                date = parsedLocalDateTime,
                walletId = walletId,
                createdDate = LocalDateTime.now(),
                amountBase = amountBase
            )
        )
    }

    val walletsNotArchivedLiveData: LiveData<List<Wallet>> = walletRepository.getAllWalletsNotArchivedLiveData()

    fun updateWallet(wallet: Wallet) = viewModelScope.launch(Dispatchers.IO) {
        walletRepository.updateWallet(wallet)
    }

//    @RequiresApi(Build.VERSION_CODES.O)
//    fun archive(expenseSubGroupNameBinding: String, amountBinding: Double, commentBinding: String, parsedLocalDateTime: LocalDateTime, walletNameBinding: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            val expenseSubGroup = expenseSubGroupRepository.getExpenseSubGroupByNameNotArchived(expenseSubGroupNameBinding)
//            if (expenseSubGroup != null) {
//                val expenseSubGroupId = expenseSubGroup.id
//
//                if (expenseSubGroupId != null) {
//                    val wallet = getWalletByNameNotArchived(walletNameBinding)
//
//                    if (wallet != null) {
//                        val walletId = wallet.id!!
//
//                        insertExpenseHistoryRecord(
//                            expenseSubGroupId,
//                            amountBinding,
//                            commentBinding,
//                            parsedLocalDateTime,
//                            walletId
//                        )
//
//                        updateWallet(walletId, wallet, amountBinding)
//                    }
//                }
//            }
//        }
//    }

    fun updateExpenseSubGroup(expenseSubGroup: ExpenseSubGroup) {
        viewModelScope.launch(Dispatchers.IO) {
            expenseSubGroupRepository.updateExpenseSubGroup(expenseSubGroup)
        }
    }

    fun updateExpenseGroup(expenseGroupEntityArchived: ExpenseGroupEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            expenseGroupRepository.updateExpenseGroup(expenseGroupEntityArchived)
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
        val selectedWallet = walletRepository.getWalletByIdAsync(id)

        if (selectedWallet != null) {
            val selectedWalletArchived = selectedWallet.copy(
                archivedDate = LocalDateTime.now()
            )

            walletRepository.updateWallet(selectedWalletArchived)
        }
    }

    fun insertExpenseGroup(expenseGroupEntity: ExpenseGroupEntity) = viewModelScope.launch(Dispatchers.IO) {
        expenseGroupRepository.insertExpenseGroup(expenseGroupEntity)
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

    fun getWalletById(id: Long): LiveData<Wallet?> {
        return walletRepository.getWalletByIdLiveData(id)
    }

}
