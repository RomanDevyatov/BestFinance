package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.IncomeGroup
import com.romandevyatov.bestfinance.data.entities.IncomeSubGroup
import com.romandevyatov.bestfinance.data.entities.relations.IncomeGroupWithIncomeSubGroups
import com.romandevyatov.bestfinance.data.repositories.IncomeGroupRepository
import com.romandevyatov.bestfinance.data.repositories.IncomeSubGroupRepository
import com.romandevyatov.bestfinance.data.roomdb.converters.LocalDateTimeRoomTypeConverter
import com.romandevyatov.bestfinance.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class IncomeGroupsAndSubGroupsViewModel @Inject constructor(
    private val incomeGroupRepository: IncomeGroupRepository,
    private val incomeSubGroupRepository: IncomeSubGroupRepository
) : ViewModel() {

    fun unarchiveIncomeGroupByIdSpecific(id: Long?) = viewModelScope.launch(Dispatchers.IO) {
        incomeGroupRepository.unarchiveIncomeGroupById(id)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun archiveIncomeGroupByIdSpecific(id: Long?) = viewModelScope.launch(Dispatchers.IO) {
        val dateTime = (LocalDateTime.now()).format(LocalDateTimeRoomTypeConverter.dateTimeFormatter)
        incomeGroupRepository.updateArchivedDateById(id, dateTime)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun archiveIncomeSubGroupByIdSpecific(id: Long?) = viewModelScope.launch(Dispatchers.IO) {
        val dateTime = (LocalDateTime.now()).format(LocalDateTimeRoomTypeConverter.dateTimeFormatter)
        incomeSubGroupRepository.updateArchivedDateById(id, dateTime)
    }

    fun unarchiveIncomeSubGroupById(id: Long?) = viewModelScope.launch(Dispatchers.IO) {
        incomeSubGroupRepository.updateArchivedDateById(id, null)
    }

    val allIncomeGroupsWithIncomeSubGroupsLiveData: LiveData<List<IncomeGroupWithIncomeSubGroups>> = incomeGroupRepository.getAllIncomeGroupsWithIncomeSubGroupsLiveData()

    private var deletedItem: IncomeGroup? = null
    private val deletedItemList = mutableListOf<IncomeGroup>()

    fun deleteItem(id: Long) = viewModelScope.launch (Dispatchers.IO) {
        try {
            val itemToDelete = incomeGroupRepository.getIncomeGroupById(id)
            if (itemToDelete != null) {
                deletedItem = itemToDelete
                deletedItemList.add(itemToDelete)

                // Delay for the specified time before deletion
                delay(Constants.UNDO_DELAY)

                // After the delay, check if the item is still in the list and delete it
                if (deletedItemList.contains(itemToDelete)) {
                    incomeGroupRepository.deleteIncomeGroupById(id)
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

    private var deletedSubItem: IncomeSubGroup? = null
    private val deletedSubItemList = mutableListOf<IncomeSubGroup>()

    fun deleteSubItem(id: Long) = viewModelScope.launch (Dispatchers.IO) {
        try {
            val itemToDelete = incomeSubGroupRepository.getIncomeSubGroupById(id)
            if (itemToDelete != null) {
                deletedSubItem = itemToDelete
                deletedSubItemList.add(itemToDelete)

                // Delay for the specified time before deletion
                delay(Constants.UNDO_DELAY)

                // After the delay, check if the item is still in the list and delete it
                if (deletedSubItemList.contains(itemToDelete)) {
                    incomeSubGroupRepository.deleteIncomeSubGroupById(id)
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
