package com.romandevyatov.bestfinance.utils

import java.math.BigDecimal
import java.math.RoundingMode

object TextFormatter {

    fun removeTrailingZeros(inputText: String): String {
        var input = inputText
        if (input.contains(".")) {
            // Remove trailing zeros after the decimal point
            input = input.replace("0*$".toRegex(), "")
            // Remove the decimal point if there are no digits after it
            input = input.replace("\\.$".toRegex(), "")
        }
        return input
    }

    fun roundDoubleToTwoDecimalPlaces(value: Double): Double {
        return BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toDouble()
    }
}
