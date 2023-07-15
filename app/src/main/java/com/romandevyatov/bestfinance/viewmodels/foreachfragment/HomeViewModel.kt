package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.db.entities.IncomeGroup
import com.romandevyatov.bestfinance.repositories.IncomeGroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val incomeGroupRepository: IncomeGroupRepository
) : ViewModel() {

    val incomeGroupsLiveData: LiveData<List<IncomeGroup>> = incomeGroupRepository.getAllLiveData()

    private val _resultLiveData = MutableLiveData<IncomeGroup>()
    val resultLiveData: LiveData<IncomeGroup> = _resultLiveData

    fun get(id: Long) {
        viewModelScope.launch {
            val result = incomeGroupRepository.getIncomeGroupByIdNotArchived(id)
            _resultLiveData.postValue(result)
        }
    }

}