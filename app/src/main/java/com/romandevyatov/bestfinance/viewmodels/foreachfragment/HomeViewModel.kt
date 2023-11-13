package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.data.entities.BaseCurrencyRate
import com.romandevyatov.bestfinance.data.entities.IncomeGroup
import com.romandevyatov.bestfinance.data.repositories.BaseCurrencyRatesRepository
import com.romandevyatov.bestfinance.data.repositories.CurrencyRepository
import com.romandevyatov.bestfinance.data.repositories.IncomeGroupRepository
import com.romandevyatov.bestfinance.utils.Constants.supportedCurrencies
import com.romandevyatov.bestfinance.utils.sharedpreferences.Storage
import com.romandevyatov.bestfinance.viewmodels.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val storage: Storage,
    private val incomeGroupRepository: IncomeGroupRepository,
    private val currencyRepository: CurrencyRepository,
    private val baseCurrencyRatesRepository: BaseCurrencyRatesRepository
) : BaseViewModel(storage) {

    val incomeGroupsLiveData: LiveData<List<IncomeGroup>> = incomeGroupRepository.getAllIncomeGroupsLiveData()

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

    fun initializeCurrencyData() = viewModelScope.launch(Dispatchers.IO) {
        val allCurrenciesList = currencyRepository.getAllCurrencies()
        if (allCurrenciesList.isEmpty()) {
            currencyRepository.insertAllCurrencies(supportedCurrencies)
        }

    }

    fun getBaseCurrencyRatesLiveData(pairName: String): LiveData<BaseCurrencyRate?> {
        return baseCurrencyRatesRepository.getBaseCurrencyRateByPairNameLiveData(pairName)
    }

    fun getBaseCurrencyRatesByPairName(pairName: String): BaseCurrencyRate? {
        return runBlocking {
            baseCurrencyRatesRepository.getBaseCurrencyRateByPairName(pairName)
        }
    }
}
