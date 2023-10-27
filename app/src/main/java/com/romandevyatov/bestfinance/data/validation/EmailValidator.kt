package com.romandevyatov.bestfinance.data.validation

import android.text.TextUtils
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.data.validation.base.BaseValidator
import com.romandevyatov.bestfinance.data.validation.base.ValidateResult

class EmailValidator(private val email: String) : BaseValidator() {

    override fun validate(): ValidateResult {
        val isValid = !TextUtils.isEmpty(email)
                && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val message = if (isValid) R.string.text_validation_success else R.string.text_validation_error_wrong_email_field

        return ValidateResult(isValid, message)
    }
}
