package com.romandevyatov.bestfinance.ui.validators

import android.text.TextUtils
import com.romandevyatov.bestfinance.ui.validators.base.BaseValidator
import com.romandevyatov.bestfinance.ui.validators.base.ValidateResult

class EmailValidator(val email: String) : BaseValidator() {

    override fun validate(): ValidateResult {
        val isValid = !TextUtils.isEmpty(email)
                && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val message = if (isValid) 0 else -1

        return ValidateResult(isValid, message)
    }

}