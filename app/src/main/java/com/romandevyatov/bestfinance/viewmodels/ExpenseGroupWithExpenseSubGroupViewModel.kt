package com.romandevyatov.bestfinance.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romandevyatov.bestfinance.db.entities.relations.ExpenseGroupWithExpenseSubGroup
import com.romandevyatov.bestfinance.repositories.ExpenseGroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseGroupWithExpenseSubGroupViewModel @Inject constructor(
    private val expenseGroupRepository: ExpenseGroupRepository
) : ViewModel() {

    val expenseGroupsLiveData: LiveData<List<ExpenseGroupWithExpenseSubGroup>>
                    = expenseGroupRepository.getAllExpenseGroupWithExpenseSubGroup()

    fun getExpenseGroupWithExpenseSubGroup() = viewModelScope.launch(Dispatchers.IO) {
        expenseGroupRepository.getAllExpenseGroupWithExpenseSubGroup()
    }

}
