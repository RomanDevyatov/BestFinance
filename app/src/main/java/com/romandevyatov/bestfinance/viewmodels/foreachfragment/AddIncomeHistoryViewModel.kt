package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.IncomeGroupEntity
import com.romandevyatov.bestfinance.data.entities.IncomeHistoryEntity
import com.romandevyatov.bestfinance.data.entities.IncomeSubGroup
import com.romandevyatov.bestfinance.data.entities.WalletEntity
import com.romandevyatov.bestfinance.data.entities.relations.IncomeGroupWithIncomeSubGroups
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
class AddIncomeHistoryViewModel @Inject constructor(
    storage: Storage,
    private val incomeGroupRepository: IncomeGroupRepository,
    private val incomeSubGroupRepository: IncomeSubGroupRepository,
    private val incomeHistoryRepository: IncomeHistoryRepository,
    private val walletRepository: WalletRepository,
    private val baseRatesRepository: BaseCurrencyRatesRepository
) : BaseViewModel(storage) {

    // income group zone
    fun getAllIncomeGroupNotArchivedLiveData(): LiveData<List<IncomeGroupEntity>> {
        return incomeGroupRepository.getAllIncomeGroupNotArchivedLiveData()
    }

    fun getIncomeGroupNotArchivedWithIncomeSubGroupsNotArchivedByIncomeGroupNameLiveData(name: String): LiveData<IncomeGroupWithIncomeSubGroups?> {
        return incomeGroupRepository.getIncomeGroupNotArchivedWithIncomeSubGroupsNotArchivedByIncomeGroupNameLiveData(name)
    }

    fun insertIncomeGroup(incomeGroupEntity: IncomeGroupEntity) = viewModelScope.launch(Dispatchers.IO) {
        incomeGroupRepository.insertIncomeGroup(incomeGroupEntity)
    }

    fun insertIncomeSubGroup(incomeSubGroup: IncomeSubGroup) = viewModelScope.launch(Dispatchers.IO) {
        val existingIncomeSubGroup = incomeSubGroupRepository.getIncomeSubGroupByNameAndIncomeGroupId(incomeSubGroup.name, incomeSubGroup.incomeGroupId)

        if (existingIncomeSubGroup == null) {
            incomeSubGroupRepository.insertIncomeSubGroup(incomeSubGroup)
        } else if (existingIncomeSubGroup.archivedDate != null) {
            incomeSubGroupRepository.unarchiveIncomeSubGroup(existingIncomeSubGroup)
        }
    }

    fun insertWallet(walletEntity: WalletEntity) = viewModelScope.launch(Dispatchers.IO) {
        walletRepository.insertWallet(walletEntity)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun archiveIncomeGroup(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        val incomeGroupWithIncomeSubGroups = getIncomeGroupWithIncomeSubGroupsByIncomeGroupIdNotArchived(id)
        if (incomeGroupWithIncomeSubGroups != null) {
            val incomeGroup = incomeGroupWithIncomeSubGroups.incomeGroupEntity
            val incomeSubGroups = incomeGroupWithIncomeSubGroups.incomeSubGroups

            val archivedDate = LocalDateTime.now()

            val incomeGroupEntityArchived = IncomeGroupEntity(
                id = incomeGroup.id,
                name = incomeGroup.name,
                isPassive = incomeGroup.isPassive,
                description = incomeGroup.description,
                archivedDate = archivedDate
            )
            updateIncomeGroup(incomeGroupEntityArchived)

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
    private fun insertIncomeHistory(incomeHistoryEntity: IncomeHistoryEntity) = viewModelScope.launch(Dispatchers.IO) {
        incomeHistoryRepository.insertIncomeHistory(incomeHistoryEntity)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addIncomeHistoryAndUpdateWallet(incomeSubGroupId: Long,
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

                    insertIncomeHistoryRecord(
                        incomeSubGroupId,
                        amountBinding,
                        commentBinding,
                        parsedLocalDateTime,
                        walletId,
                        amountBase
                    )

                    val updatedWallet = wallet.copy(
                        balance = wallet.balance + amountBinding,
                        input = wallet.input + amountBinding
                    )

                    updateWallet(updatedWallet)
                } else {
                    Log.d("baseCurrencyRate", "baseCurrencyRate.value == null")
                }
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun insertIncomeHistoryRecord(
        incomeSubGroupId: Long,
        amountBinding: Double,
        commentBinding: String,
        parsedLocalDateTime: LocalDateTime,
        walletId: Long,
        amountBase: Double) {
        insertIncomeHistory(
            IncomeHistoryEntity(
                incomeSubGroupId = incomeSubGroupId,
                amount = amountBinding,
                comment = commentBinding,
                date = parsedLocalDateTime,
                walletId = walletId,
                createdDate = LocalDateTime.now(),
                amountBase = amountBase
            )
        )
    }

    fun getWalletById(id: Long): LiveData<WalletEntity?> {
        return walletRepository.getWalletByIdLiveData(id)
    }

    val walletsNotArchivedLiveData: LiveData<List<WalletEntity>> = walletRepository.getAllWalletsNotArchivedLiveData()

    fun updateWallet(walletEntity: WalletEntity) = viewModelScope.launch(Dispatchers.IO) {
        walletRepository.updateWallet(walletEntity)
    }

//    @RequiresApi(Build.VERSION_CODES.O)
//    fun archive(incomeSubGroupNameBinding: String, amountBinding: Double, commentBinding: String, parsedLocalDateTime: LocalDateTime, walletNameBinding: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            val incomeSubGroup = incomeSubGroupRepository.getByNameNotArchived(incomeSubGroupNameBinding)
//            val incomeGroupId = incomeSubGroup?.id
//            if (incomeGroupId != null) {
//                val wallet = getWalletByNameNotArchived(walletNameBinding)
//
//                val walletId = wallet?.id
//                if (walletId != null) {
//                    insertIncomeHistoryRecord(
//                        incomeGroupId,
//                        amountBinding,
//                        commentBinding,
//                        parsedLocalDateTime,
//                        walletId,
//                        amountBinding
//                    )
//
//                    updateWallet(walletId, wallet, amountBinding)
//                }
//            }
//        }
//
//    }

    fun updateIncomeSubGroup(incomeSubGroup: IncomeSubGroup) {
        viewModelScope.launch(Dispatchers.IO) {
            incomeSubGroupRepository.updateIncomeSubGroup(incomeSubGroup)
        }
    }

    fun updateIncomeGroup(incomeGroupEntityArchived: IncomeGroupEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            incomeGroupRepository.updateIncomeGroup(incomeGroupEntityArchived)
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
        val selectedWallet = walletRepository.getWalletByIdAsync(id)

        if (selectedWallet != null) {
            val selectedWalletArchived = selectedWallet.copy(
                archivedDate = LocalDateTime.now()
            )

            walletRepository.updateWallet(selectedWalletArchived)
        }
    }
}
