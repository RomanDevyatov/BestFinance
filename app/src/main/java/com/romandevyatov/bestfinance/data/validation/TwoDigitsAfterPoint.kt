package com.romandevyatov.bestfinance.data.validation

import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.data.validation.base.BaseValidator
import com.romandevyatov.bestfinance.data.validation.base.ValidateResult

class TwoDigitsAfterPoint(private val input: String) : BaseValidator() {

    override fun validate(): ValidateResult {
        val isValid =
            if (input.contains(".")) {
            val indexOfDecimal = input.indexOf(".")
            input.length - indexOfDecimal <= 3
        } else {
            true
        }

        val message = if (isValid) R.string.text_validation_success else R.string.text_validation_error_more_then_two_digits_after_point
        return ValidateResult(isValid, message)
    }
}