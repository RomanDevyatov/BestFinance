package com.romandevyatov.bestfinance.utils.voiceassistance

import java.util.Locale

object NumberConverter {

    private fun replaceEnglishTextNumberToRealDigit(spokenText: String): MutableList<String> {
        val numberMap = mapOf(
            "zero" to "0",
            "one" to "1",
            "two" to "2",
            "three" to "3",
            "four" to "4",
            "five" to "5",
            "six" to "6",
            "seven" to "7",
            "eight" to "8",
            "nine" to "9",
            "ten" to "10",
            "eleven" to "11",
            "twelve" to "12",
            "thirteen" to "13",
            "fourteen" to "14",
            "fifteen" to "15",
            "sixteen" to "16",
            "seventeen" to "17",
            "eighteen" to "18",
            "nineteen" to "19",
            "twenty" to "20",
            "thirty" to "30",
            "forty" to "40",
            "fifty" to "50",
            "sixty" to "60",
            "seventy" to "70",
            "eighty" to "80",
            "ninety" to "90"
        )

        return spokenText.split(" ").map { word ->
            numberMap[word.lowercase(Locale.ROOT)] ?: word
        }.toMutableList()
    }

    fun convertSpokenTextToNumber(spokenText: String): Double? {
        return when (Locale.getDefault()) {
            Locale.US -> {
                handleEnglishNumbers(spokenText)
            }
            else -> {
                spokenText.toDoubleOrNull()
            }
        }
    }

    private fun handleEnglishNumbers(spokenText: String): Double? {
        var result = 0.0  // Initialize as a double for handling decimal parts
        var isDecimal = false
        var decimalMultiplier = 0.1  // Start with one decimal place
        var multiplier = 1.0  // Initialize as 1 for handling units like hundreds and thousands
        var nextMultiplier = 1.0

        val digitOrLevelList = replaceEnglishTextNumberToRealDigit(spokenText)

        val validWords = setOf(
            "point", "hundred", "thousand", "million", "billion"
        )

        for (digitOrLevel in digitOrLevelList) {
            if (isDouble(digitOrLevel) || (digitOrLevel.lowercase() in validWords)) {
                val number = if (digitOrLevel.isNotEmpty()) digitOrLevel.toDouble() else 0.0

                if (digitOrLevel.equals("point", ignoreCase = true)) {
                    isDecimal = true
                    continue  // Skip "point" in the final output
                }

                when {
                    digitOrLevel.equals("hundred", ignoreCase = true) -> {
                        nextMultiplier = 100.0
                    }
                    digitOrLevel.equals("thousand", ignoreCase = true) -> {
                        nextMultiplier = 1000.0
                    }
                    digitOrLevel.equals("million", ignoreCase = true) -> {
                        nextMultiplier = 1000000.0
                    }
                    digitOrLevel.equals("billion", ignoreCase = true) -> {
                        nextMultiplier = 1000000000.0
                    }
                    else -> {
                        if (isDecimal) {
                            // Accumulate the decimal - after point
                            result += number * decimalMultiplier
                            decimalMultiplier *= 0.1
                        } else {
                            if (nextMultiplier > 1) {
                                multiplier = nextMultiplier
                                nextMultiplier = 1.0
                            }
                            // Accumulate the integer part with appropriate multiplier - before point
                            result += number * multiplier
                        }
                    }
                }
            } else {
                return null
            }
        }

        return result
    }

    private fun isDouble(str: String): Boolean {
        return try {
            str.toDouble()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }
}