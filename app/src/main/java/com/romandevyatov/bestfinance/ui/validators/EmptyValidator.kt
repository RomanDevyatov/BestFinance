package com.romandevyatov.bestfinance.ui.validators

import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.ui.validators.base.BaseValidator
import com.romandevyatov.bestfinance.ui.validators.base.ValidateResult

class EmptyValidator(val input: String) : BaseValidator() {

    override fun validate(): ValidateResult {
        val isValid = input.isNotEmpty()
        val message = if (isValid) R.string.text_validation_success else R.string.text_validation_error_empty_field
        return ValidateResult(
            isValid,
            message
        )
    }
}
