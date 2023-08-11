package com.romandevyatov.bestfinance.viewmodels.foreachmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.TransferHistory
import com.romandevyatov.bestfinance.data.repositories.TransferHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransferHistoryViewModel @Inject constructor(
    private val transferHistoryRepository: TransferHistoryRepository
) : ViewModel() {

    val allTransferHistoriesLiveData: LiveData<List<TransferHistory>> = transferHistoryRepository.getAllTransferHistories()

    fun insertTransferHistory(transferHistory: TransferHistory) = viewModelScope.launch(Dispatchers.IO) {
        transferHistoryRepository.insertTransferHistory(transferHistory)
    }

}
