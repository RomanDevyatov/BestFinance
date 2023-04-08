package com.romandevyatov.bestfinance.ui.validator

import com.romandevyatov.bestfinance.ui.validator.base.BaseValidator
import com.romandevyatov.bestfinance.ui.validator.base.ValidateResult

class EmptyValidator(val input: String) : BaseValidator() {

    override fun validate(): ValidateResult {
        val isValid = input.isNotEmpty()
        val message = if (isValid) 0 else -1
        return ValidateResult(
            isValid,
            message
        )
    }
}