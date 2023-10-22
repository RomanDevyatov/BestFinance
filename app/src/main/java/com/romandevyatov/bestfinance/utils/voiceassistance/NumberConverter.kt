package com.romandevyatov.bestfinance.utils.voiceassistance

import java.util.Locale

object NumberConverter {

    fun convertSpokenTextToNumber(spokenText: String): Double? {
        val currentLocale = Locale.getDefault()
        val validWords = validWordsMap[currentLocale] ?: emptyMap()
        return when (currentLocale) {
            Locale.US -> {
                val digitOrLevelList = replaceTextNumberToRealDigit(spokenText, englishNumberMap)
                handleNumbers(digitOrLevelList, validWords)
            }
            Locale("RU") -> {
                val digitOrLevelList = replaceTextNumberToRealDigit(spokenText, russianNumberMap)
                handleNumbers(digitOrLevelList, validWords)
            }
            else -> {
                spokenText.toDoubleOrNull()
            }
        }
    }

    private val englishNumberMap = mapOf(
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

    private val russianNumberMap = mapOf(
        "ноль" to "0",
        "один" to "1",
        "два" to "2",
        "три" to "3",
        "четыре" to "4",
        "пять" to "5",
        "шесть" to "6",
        "семь" to "7",
        "восемь" to "8",
        "девять" to "9",
        "десять" to "10",
        "одиннадцать" to "11",
        "двенадцать" to "12",
        "тринадцать" to "13",
        "четырнадцать" to "14",
        "пятнадцать" to "15",
        "шестнадцать" to "16",
        "семнадцать" to "17",
        "восемнадцать" to "18",
        "девятнадцать" to "19",
        "двадцать" to "20",
        "тридцать" to "30",
        "сорок" to "40",
        "пятьдесят" to "50",
        "шестьдесят" to "60",
        "семьдесят" to "70",
        "восемьдесят" to "80",
        "девяносто" to "90"
    )

    private val validWordsMap = mapOf(
        Locale.US to mapOf(
            "." to arrayOf("point"),
            "100" to arrayOf("hundred"),
            "1000" to arrayOf("thousand"),
            "1000000" to arrayOf("million"),
            "1000000000" to arrayOf("billion")
        ),
        Locale("RU") to mapOf(
            "." to arrayOf("точка"),
            "100" to arrayOf("сотня", "сотен", "сто"),
            "1000" to arrayOf("тысяча", "тысяч", "тысячи"),
            "1000000" to arrayOf("миллион", "миллионов", "миллиона"),
            "1000000000" to arrayOf("миллиард", "миллиарда", "миллиардов")
        )
    )

    private fun handleNumbers(digitOrLevelList: MutableList<String>, validWordMap: Map<String, Array<String>>): Double? {
        var result = 0.0  // Initialize as a double for handling decimal parts
        var isDecimal = false
        var decimalMultiplier = 0.1  // Start with one decimal place
        var multiplier = 1.0  // handling units like hundreds and thousands
        var nextMultiplier = 1.0

        val validWords: List<String> = validWordMap.values.flatMap { it.asList() }

        for (digitOrLevel in digitOrLevelList) {
            if (isDouble(digitOrLevel) || validWords.contains(digitOrLevel.lowercase())) {
                if (validWordMap["."]?.contains(digitOrLevel.lowercase()) == false) {
                    nextMultiplier = when {
                        validWordMap["100"]?.contains(digitOrLevel.lowercase()) == true -> 100.0
                        validWordMap["1000"]?.contains(digitOrLevel.lowercase()) == true -> 1000.0
                        validWordMap["1000000"]?.contains(digitOrLevel.lowercase()) == true -> 1000000.0
                        validWordMap["1000000000"]?.contains(digitOrLevel.lowercase()) == true -> 1000000000.0
                        else -> {
                            val number = digitOrLevel.toDoubleOrNull() ?: 0.0
                            if (isDecimal) {
                                result += number * decimalMultiplier
                                decimalMultiplier *= 0.1
                            } else {
                                if (nextMultiplier > 1) {
                                    multiplier = nextMultiplier
                                    nextMultiplier = 1.0
                                }
                                result += number * multiplier
                            }
                            nextMultiplier
                        }
                    }
                } else {
                    isDecimal = true
                }
            } else {
                return null
            }
        }

        return result
    }

    private fun replaceTextNumberToRealDigit(spokenText: String, numberMap: Map<String, String>): MutableList<String> {
        return spokenText.split(" ").map { word ->
            numberMap[word.lowercase(Locale.ROOT)] ?: word
        }.toMutableList()
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
