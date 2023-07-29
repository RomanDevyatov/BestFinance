package com.romandevyatov.bestfinance.data.validation

import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.data.validation.base.BaseValidator
import com.romandevyatov.bestfinance.data.validation.base.ValidateResult

class IsEqualValidator(private val input: String, private val another: String) : BaseValidator() {

    override fun validate(): ValidateResult {
        val isValid = input != another
        val message = if (isValid) R.string.text_validation_success else R.string.text_validation_same_spinner_values_field

        return ValidateResult(isValid, message)
    }
}