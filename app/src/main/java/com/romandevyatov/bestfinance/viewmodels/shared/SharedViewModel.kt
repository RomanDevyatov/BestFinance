package com.romandevyatov.bestfinance.viewmodels.shared

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(): ViewModel() {

    private val _transferForm = MutableLiveData<TransferForm?>()

    val transferForm: MutableLiveData<TransferForm?> = _transferForm

    override fun onCleared() {
        _transferForm.value = null
    }

    fun setTransferFormValue(transferForm: TransferForm) {
        _transferForm.value = transferForm
    }

}
