package com.romandevyatov.bestfinance.ui.adapters.spinner.models

data class SpinnerItem(val id: Long?, val name: String) {
    override fun toString(): String {
        return name
    }
}
