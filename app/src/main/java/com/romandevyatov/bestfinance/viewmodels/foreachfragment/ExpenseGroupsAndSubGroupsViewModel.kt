package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.ExpenseGroupEntity
import com.romandevyatov.bestfinance.data.entities.ExpenseSubGroup
import com.romandevyatov.bestfinance.data.entities.relations.ExpenseGroupWithExpenseSubGroups
import com.romandevyatov.bestfinance.data.repositories.ExpenseGroupRepository
import com.romandevyatov.bestfinance.data.repositories.ExpenseSubGroupRepository
import com.romandevyatov.bestfinance.data.roomdb.converters.LocalDateTimeRoomTypeConverter
import com.romandevyatov.bestfinance.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ExpenseGroupsAndSubGroupsViewModel @Inject constructor(
    private val expenseGroupRepository: ExpenseGroupRepository,
    private val expenseSubGroupRepository: ExpenseSubGroupRepository
) : ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    fun archiveExpenseSubGroup(name: String) = viewModelScope.launch(Dispatchers.IO) {
        val expenseSubGroup = expenseSubGroupRepository.getByNameNotArchived(name)

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

    fun unarchiveExpenseSubGroupById(id: Long?) = viewModelScope.launch(Dispatchers.IO) {
        expenseSubGroupRepository.unarchiveExpenseSubGroupById(id)
    }

    fun deleteExpenseSubGroupById(id: Long?) = viewModelScope.launch(Dispatchers.IO) {
        expenseSubGroupRepository.deleteExpenseSubGroupById(id)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun archiveExpenseGroupByIdSpecific(id: Long?) = viewModelScope.launch(Dispatchers.IO) {
        val dateTime = (LocalDateTime.now()).format(LocalDateTimeRoomTypeConverter.dateTimeFormatter)
        expenseGroupRepository.updateArchivedDateById(id, dateTime)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun archiveExpenseGroupById(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        val selectExpenseGroup = expenseGroupRepository.getExpenseGroupById(id)

        if (selectExpenseGroup != null) {
            val selectedExpenseGroupEntityArchived = ExpenseGroupEntity(
                id = selectExpenseGroup.id,
                name = selectExpenseGroup.name,
                description = selectExpenseGroup.description,
                archivedDate = LocalDateTime.now()
            )

            expenseGroupRepository.updateExpenseGroup(selectedExpenseGroupEntityArchived)
        }
    }

    fun unarchiveExpenseGroupById(id: Long?) = viewModelScope.launch(Dispatchers.IO) {
        expenseGroupRepository.unarchiveExpenseGroupById(id)
    }

    fun deleteExpenseGroupById(id: Long?) = viewModelScope.launch(Dispatchers.IO) {
        expenseGroupRepository.deleteExpenseGroupById(id)
    }

    val allExpenseGroupsWithExpenseSubGroupsLiveData: LiveData<List<ExpenseGroupWithExpenseSubGroups>> = expenseGroupRepository.getAllExpenseGroupsWithExpenseSubGroupsLiveData()

    private var deletedItem: ExpenseGroupEntity? = null
    private val deletedItemList = mutableListOf<ExpenseGroupEntity>()

    fun deleteItem(id: Long) = viewModelScope.launch (Dispatchers.IO) {
        try {
            val itemToDelete = expenseGroupRepository.getExpenseGroupById(id)
            if (itemToDelete != null) {
                deletedItem = itemToDelete
                deletedItemList.add(itemToDelete)

                // Delay for the specified time before deletion
                delay(Constants.UNDO_DELAY)

                // After the delay, check if the item is still in the list and delete it
                if (deletedItemList.contains(itemToDelete)) {
                    expenseGroupRepository.deleteExpenseGroupById(id)
                    deletedItemList.remove(itemToDelete)
                }
            }
        } catch (_: Exception) { }
    }

    fun undoDeleteItem() = viewModelScope.launch(Dispatchers.IO) {
        if (deletedItemList.contains(deletedItem)) {
            deletedItemList.remove(deletedItem)
            deletedItem = null
        }
    }

    private var deletedSubItem: ExpenseSubGroup? = null
    private val deletedSubItemList = mutableListOf<ExpenseSubGroup>()

    fun deleteSubItem(id: Long) = viewModelScope.launch (Dispatchers.IO) {
        try {
            val itemToDelete = expenseSubGroupRepository.getExpenseSubGroupById(id)
            if (itemToDelete != null) {
                deletedSubItem = itemToDelete
                deletedSubItemList.add(itemToDelete)

                // Delay for the specified time before deletion
                delay(Constants.UNDO_DELAY)

                // After the delay, check if the item is still in the list and delete it
                if (deletedSubItemList.contains(itemToDelete)) {
                    expenseSubGroupRepository.deleteExpenseSubGroupById(id)
                    deletedSubItemList.remove(itemToDelete)
                }
            }
        } catch (_: Exception) { }
    }

    fun undoDeleteSubItem() = viewModelScope.launch(Dispatchers.IO) {
        if (deletedSubItemList.contains(deletedSubItem)) {
            deletedSubItemList.remove(deletedSubItem)
            deletedSubItem = null
        }
    }
}
