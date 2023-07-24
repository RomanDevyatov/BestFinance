package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.db.entities.ExpenseGroup
import com.romandevyatov.bestfinance.db.entities.ExpenseHistory
import com.romandevyatov.bestfinance.db.entities.ExpenseSubGroup
import com.romandevyatov.bestfinance.db.entities.Wallet
import com.romandevyatov.bestfinance.db.entities.relations.ExpenseGroupWithExpenseSubGroups
import com.romandevyatov.bestfinance.repositories.ExpenseGroupRepository
import com.romandevyatov.bestfinance.repositories.ExpenseHistoryRepository
import com.romandevyatov.bestfinance.repositories.ExpenseSubGroupRepository
import com.romandevyatov.bestfinance.repositories.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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

    fun getExpenseGroupNotArchivedWithExpenseSubGroupsNotArchivedByExpenseGroupNameLiveData(name: String): LiveData<ExpenseGroupWithExpenseSubGroups> {
        return expenseGroupRepository.getExpenseGroupNotArchivedWithExpenseSubGroupsNotArchivedByExpenseGroupNameLiveData(name)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun archiveExpenseGroup(name: String) = viewModelScope.launch(Dispatchers.IO) {
        val expenseGroupWithExpenseSubGroups = getExpenseGroupWithExpenseSubGroupsByExpenseGroupNameNotArchived(name)

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

    private fun getExpenseGroupWithExpenseSubGroupsByExpenseGroupNameNotArchived(name: String): ExpenseGroupWithExpenseSubGroups {
        return expenseGroupRepository.getExpenseGroupWithExpenseSubGroupsByExpenseGroupNameNotArchived(name)
    }

    // expense history zone
    fun insertExpenseHistory(expenseHistory: ExpenseHistory) = viewModelScope.launch(Dispatchers.IO) {
        expenseHistoryRepository.insertExpenseHistory(expenseHistory)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addExpenseHistory(expenseSubGroupNameBinding: String, amountBinding: Double, commentBinding: String, parsedLocalDateTime: LocalDateTime, walletNameBinding: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val expenseSubGroup = expenseSubGroupRepository.getExpenseSubGroupByNameNotArchived(expenseSubGroupNameBinding)
            val expenseSubGroupId = expenseSubGroup.id!!.toLong()

            val wallet = getWalletByNameNotArchived(walletNameBinding)
            val walletId = wallet.id!!

            insertExpenseHistoryRecord(expenseSubGroupId, amountBinding, commentBinding, parsedLocalDateTime, walletId)

            updateWallet(walletId, wallet, amountBinding)
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun insertExpenseHistoryRecord(expenseGroupId: Long, amountBinding: Double, commentBinding: String, parsedLocalDateTime: LocalDateTime, walletId: Long) {
        insertExpenseHistory(
            ExpenseHistory(
                expenseSubGroupId = expenseGroupId,
                amount = amountBinding,
                description = commentBinding,
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
    fun archive(expenseSubGroupNameBinding: String, amountBinding: Double, commentBinding: String, parsedLocalDateTime: LocalDateTime, walletNameBinding: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val expenseSubGroup = expenseSubGroupRepository.getExpenseSubGroupByNameNotArchived(expenseSubGroupNameBinding)
            val expenseSubGroupId = expenseSubGroup.id!!.toLong()

            val wallet = getWalletByNameNotArchived(walletNameBinding)
            val walletId = wallet.id!!

            insertExpenseHistoryRecord(expenseSubGroupId, amountBinding, commentBinding, parsedLocalDateTime, walletId)

            updateWallet(walletId, wallet, amountBinding)
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
    fun archiveExpenseSubGroup(name: String) = viewModelScope.launch(Dispatchers.IO) {
        val expenseSubGroup = expenseSubGroupRepository.getExpenseSubGroupByNameNotArchived(name)

        val expenseSubGroupArchived = ExpenseSubGroup(
            id = expenseSubGroup.id,
            name = expenseSubGroup.name,
            description = expenseSubGroup.description,
            expenseGroupId = expenseSubGroup.expenseGroupId,
            archivedDate = LocalDateTime.now()
        )

        expenseSubGroupRepository.updateExpenseSubGroup(expenseSubGroupArchived)
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
