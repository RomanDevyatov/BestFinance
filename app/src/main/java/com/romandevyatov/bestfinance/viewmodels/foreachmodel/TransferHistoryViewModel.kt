package com.romandevyatov.bestfinance.viewmodels.foreachmodel

import androidx.lifecycle.LiveData
import com.romandevyatov.bestfinance.data.entities.relations.TransferHistoryWithWallets
import com.romandevyatov.bestfinance.data.repositories.TransferHistoryRepository
import com.romandevyatov.bestfinance.utils.localization.Storage
import com.romandevyatov.bestfinance.viewmodels.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TransferHistoryViewModel @Inject constructor(
    storage: Storage,
    transferHistoryRepository: TransferHistoryRepository
) : BaseViewModel(storage) {

    val currentCurrencySymbol: String = getCurrencySymbol()

    val allTransferHistoryWithWalletsLiveData: LiveData<List<TransferHistoryWithWallets>> = transferHistoryRepository.getAllTransferHistoryWithWalletsLiveData()

}
