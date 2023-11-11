package com.romandevyatov.bestfinance.utils.numberpad

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText

private var currencySymbol = ""

fun TextInputEditText.addGenericTextWatcher() {

    val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(editable: Editable?) {
            val text = editable.toString()
            val updatedText = processInput(text)

            if (text != updatedText) {
                removeTextChangedListener(this)
                setText(updatedText)
                setSelection(updatedText.indexOf(" $currencySymbol"))
                addTextChangedListener(this)
            }
        }
    }

    addTextChangedListener(textWatcher)
}

fun TextInputEditText.setCurrencySymbol(symbol: String) {
    currencySymbol = symbol
}

fun processInput(text: String, currencySymbollocal: String = currencySymbol): String {
    var updatedText = text.replace(",", ".").replace("-", "0")

    updatedText = checkStartWithPoint(updatedText)

    updatedText = checkDoublePoints(updatedText)

    if (updatedText.length > 1) {
        if (updatedText.count { it == '.' } > 1) {
            updatedText.replaceFirst(".", "")
        }

        if (updatedText.startsWith("0")) {
            updatedText = removeBeginningZeros(updatedText)
        }
    }

    updatedText = updatedText.replace(" $currencySymbol", "")
    updatedText += " $currencySymbol"

    return updatedText
}

private fun checkStartWithPoint(text: String): String {
    if (text.startsWith('.')) {
        return "0$text"
    }
    return text
}

private fun checkDoublePoints(text: String): String {
    if (text.count { it == '.' } > 1) {
        return text.replaceFirst(".", "")
    }
    return text
}

private fun removeBeginningZeros(updatedText: String): String {
    var newStart = 0
    while (newStart < updatedText.length - 1 && updatedText[newStart] == '0' && updatedText[newStart + 1] != '.') {
        newStart++
    }

    return updatedText.substring(newStart)
}
