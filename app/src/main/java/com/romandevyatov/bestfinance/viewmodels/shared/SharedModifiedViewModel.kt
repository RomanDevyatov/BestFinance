package com.romandevyatov.bestfinance.viewmodels.shared

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class SharedModifiedViewModel<T> @Inject constructor(): ViewModel() {

    private val _modelForm: T? = null
    var modelForm: T? = _modelForm

    fun set(transferForm: T?) {
        modelForm = transferForm
    }

    override fun onCleared() {
        modelForm = null
    }

}