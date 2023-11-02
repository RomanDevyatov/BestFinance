package com.romandevyatov.bestfinance.utils.numberpad

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText

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
                setSelection(updatedText.length)
                addTextChangedListener(this)
            }
        }
    }
    addTextChangedListener(textWatcher)
}

private fun processInput(text: String): String {
    var updatedText = text.replace(",", ".").replace("-", "0")

    val decimalCount = updatedText.count { it == '.' }
    if (decimalCount > 1) {
        updatedText = updatedText.replaceFirst(".", "")
    }

    if (updatedText.length > 1) {
      if (updatedText.count { it == '0' } > 1) {
          return updatedText.replaceFirst("0", "")
      }
      if (updatedText[0] == '0') {
          val secondChar = updatedText[1]
          if (secondChar in '1'..'9') {
              return secondChar + updatedText.substring(2)
          }
      }
    }

    return updatedText
}
