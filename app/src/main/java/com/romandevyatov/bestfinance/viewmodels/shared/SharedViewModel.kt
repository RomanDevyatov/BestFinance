package com.romandevyatov.bestfinance.viewmodels.shared

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject


class SharedViewModel<T> @Inject constructor(): ViewModel() {

    private val _modelForm = MutableLiveData<T?>()
    val modelForm: MutableLiveData<T?> = _modelForm

    fun set(transferForm: T?) {
        _modelForm.value = transferForm
    }

    override fun onCleared() {
        _modelForm.value = null
    }

}
