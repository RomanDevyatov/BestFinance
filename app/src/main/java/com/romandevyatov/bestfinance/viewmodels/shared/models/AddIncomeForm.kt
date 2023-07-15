package com.romandevyatov.bestfinance.viewmodels.shared.models

data class AddIncomeForm(
    val incomeGroupSpinnerPosition: Int = 0,
    val incomeSubGroupSpinnerPosition: Int = 0,
    val walletSpinnerPosition: Int = 0,
    val amount: String? = null,
    val date: String? = null,
    val comment: String? = null
)
