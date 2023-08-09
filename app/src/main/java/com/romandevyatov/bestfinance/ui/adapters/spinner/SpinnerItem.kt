package com.romandevyatov.bestfinance.ui.adapters.spinner

data class SpinnerItem(val id: Long?, val name: String) {
    override fun toString(): String {
        return name
    }
}
