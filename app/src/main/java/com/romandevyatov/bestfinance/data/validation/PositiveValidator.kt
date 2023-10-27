package com.romandevyatov.bestfinance.data.validation

import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.data.validation.base.BaseValidator
import com.romandevyatov.bestfinance.data.validation.base.ValidateResult

class PositiveValidator(private val input: Number) : BaseValidator() {

    override fun validate(): ValidateResult {
        val isValid = input.toDouble() > 0.0
        val message = if (isValid) R.string.text_validation_success else R.string.text_validation_error_empty_field
        return ValidateResult(isValid, message)
    }
}
