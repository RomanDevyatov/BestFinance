package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.IncomeSubGroup
import com.romandevyatov.bestfinance.data.entities.relations.IncomeGroupWithIncomeSubGroups
import com.romandevyatov.bestfinance.data.repositories.IncomeGroupRepository
import com.romandevyatov.bestfinance.data.repositories.IncomeSubGroupRepository
import com.romandevyatov.bestfinance.data.roomdb.converters.LocalDateTimeRoomTypeConverter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class IncomeGroupsAndSubGroupsViewModel @Inject constructor(
    private val incomeGroupRepository: IncomeGroupRepository,
    private val incomeSubGroupRepository: IncomeSubGroupRepository
) : ViewModel() {

    fun deleteIncomeSubGroupById(id: Long?) = viewModelScope.launch(Dispatchers.IO) {
        incomeSubGroupRepository.deleteIncomeSubGroupById(id)
    }

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

    val allIncomeGroupsWithIncomeSubGroupsLiveData: LiveData<List<IncomeGroupWithIncomeSubGroups>>? = incomeGroupRepository.getAllIncomeGroupsWithIncomeSubGroupsLiveData()

    private var deleteSubGroup: IncomeSubGroup? = null

    fun deleteSubItem(subId: Long) = viewModelScope.launch (Dispatchers.IO) {
        try {
            val subGroupToDelete = incomeSubGroupRepository.getIncomeSubGroupById(subId)
            deleteSubGroup = subGroupToDelete
            incomeSubGroupRepository.deleteIncomeSubGroupById(subId)
        } catch (_: Exception) { }
    }

    fun undoDeleteSubItem() = viewModelScope.launch (Dispatchers.IO) {
        deleteSubGroup?.let { subItemToRestore ->
            try {
                incomeSubGroupRepository.insertIncomeSubGroup(subItemToRestore)
                deleteSubGroup = null
            } catch (_: Exception) { }
        }
    }

    private var deleteItemWithSubItems: IncomeGroupWithIncomeSubGroups? = null

    fun deleteItem(id: Long) = viewModelScope.launch (Dispatchers.IO) {
        try {
            val incomeGroupWithSubGroups = incomeGroupRepository.getIncomeGroupWithIncomeSubGroupsByIncomeGroupId(id)
            deleteItemWithSubItems = incomeGroupWithSubGroups
            incomeGroupRepository.deleteIncomeGroupById(id)
        } catch (_: Exception) {

        }
    }

    fun undoDeleteItem() = viewModelScope.launch (Dispatchers.IO) {
        deleteItemWithSubItems?.let { incomeGroupWithSubGroups ->
            try {
                incomeGroupRepository.insertIncomeGroup(incomeGroupWithSubGroups.incomeGroup)
                incomeGroupWithSubGroups.incomeSubGroups.forEach { subGroup ->
//                    incomeGroupRepository.insert
                }
                deleteItemWithSubItems = null
            } catch (_: Exception) { }
        }
    }
}
