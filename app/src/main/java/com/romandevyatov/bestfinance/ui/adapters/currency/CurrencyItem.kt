package com.romandevyatov.bestfinance.ui.adapters.currency

data class CurrencyItem (val code: String, val name: String) {
    override fun toString(): String {
        return name
    }
}