package com.romandevyatov.bestfinance.viewmodels.shared

import androidx.lifecycle.ViewModel
import com.romandevyatov.bestfinance.viewmodels.shared.models.AddWalletForm
import javax.inject.Inject

class SharedModifiedAddWalletFormViewModel @Inject constructor(): ViewModel() {

    private val _modelForm: AddWalletForm? = null
    var modelForm: AddWalletForm? = _modelForm

    fun set(transferForm: AddWalletForm?) {
        modelForm = transferForm
    }

    override fun onCleared() {
        modelForm = null
    }
}