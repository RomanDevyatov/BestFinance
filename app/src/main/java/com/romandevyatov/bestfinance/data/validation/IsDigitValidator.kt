package com.romandevyatov.bestfinance.data.validation

import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.data.validation.base.BaseValidator
import com.romandevyatov.bestfinance.data.validation.base.ValidateResult

class IsDigitValidator(private val input: String) : BaseValidator() {

    override fun validate(): ValidateResult {
        val digitDecimalRegex = "\\d*\\.?\\d+".toRegex()

        val isValid = input.matches(digitDecimalRegex)

        val messageResId = if (isValid) R.string.text_validation_success else R.string.text_validation_error_not_digit_field

        return ValidateResult(isValid, messageResId)
    }
}
