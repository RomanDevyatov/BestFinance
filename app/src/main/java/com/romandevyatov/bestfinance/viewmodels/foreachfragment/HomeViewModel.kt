package com.romandevyatov.bestfinance.viewmodels.foreachfragment

import androidx.lifecycle.ViewModel
import com.romandevyatov.bestfinance.repositories.IncomeGroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val incomeGroupRepository: IncomeGroupRepository
) : ViewModel() {



}