package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.Currency
import com.romandevyatov.bestfinance.data.entities.IncomeGroup
import com.romandevyatov.bestfinance.data.repositories.CurrencyRepository
import com.romandevyatov.bestfinance.data.repositories.IncomeGroupRepository
import com.romandevyatov.bestfinance.utils.localization.Storage
import com.romandevyatov.bestfinance.viewmodels.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val storage: Storage,
    private val incomeGroupRepository: IncomeGroupRepository,
    private val currencyRepository: CurrencyRepository
) : BaseViewModel(storage) {

    val incomeGroupsLiveData: LiveData<List<IncomeGroup>> = incomeGroupRepository.getAllLiveData()

    val currentCurrencySymbol: String = getCurrencySymbol()

    private val _resultLiveData = MutableLiveData<IncomeGroup>()
    val resultLiveData: LiveData<IncomeGroup> = _resultLiveData

    fun get(id: Long) {
        viewModelScope.launch {
            val result = incomeGroupRepository.getIncomeGroupByIdNotArchived(id)
            _resultLiveData.postValue(result)
        }
    }

    fun setIsFirstLaunch(isFirstLaunch: Boolean) {
        storage.setIsFirstLaunch(isFirstLaunch)
    }

    fun getIsFirstLaunch(): Boolean {
        return storage.getIsFirstLaunch()
    }

    fun initializeCurrencyData() {
        val currencies = listOf(
            Currency("USD", "United States Dollar"),
            Currency("RUB", "Russian Rouble")
        )

        viewModelScope.launch {
            if (currencyRepository.getAllCurrencies().isEmpty()) {
                currencyRepository.insertAllCurrencies(currencies)
            }
        }
    }
}
