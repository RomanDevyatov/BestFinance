package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun deleteIncomeGroupById(id: Long?) = viewModelScope.launch(Dispatchers.IO) {
        incomeGroupRepository.deleteIncomeGroupById(id)
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

}
