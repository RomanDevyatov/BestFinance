package com.romandevyatov.bestfinance.utils.voiceassistance

import java.util.Locale

object NumberConverter {

    private const val NUMBER_MAP: String = "numberMap"
    private const val LARGE_NUMBER_MAP: String = "largeNumberMap"

    private fun convertRecognizedText(text: String): String {
        val numberPattern = "\\d+(\\.\\d{1,3})"
        val numberRegex = Regex(numberPattern)

        val formattedText = numberRegex.replace(text) { matchResult ->
            val originalNumber = matchResult.value
            val formattedNumber = originalNumber.replace(".", "")
            formattedNumber
        }

        return formattedText
    }

    fun convertSpokenTextToNumber(spokenText: String): Double? {
        when (val currentLocale = Locale.getDefault()) {
            Locale.US, Locale("RU") -> {
                if (containsOnlyDigitsAndPeriod(spokenText)) {
                    return convertRecognizedText(spokenText).toDoubleOrNull()
                }

                val wordToNumberMapping = wordToNumberMaps[currentLocale] ?: emptyMap()

                val englishWords = spokenText.split(" ")
                return convertWordsToNumber(englishWords, wordToNumberMapping)
            }
            else -> {
                return spokenText.toDoubleOrNull()
            }
        }
    }

    private fun containsOnlyDigitsAndPeriod(input: String): Boolean {
        val regex = Regex("^[0-9.]*$")
        return regex.matches(input)
    }

    private fun convertWordsToNumber(
        russianWords: List<String>,
        wordToNumberMap: Map<String, Map<String, Double>>
    ): Double {
        var result = 0.0
        var currentNumber = 0.0

        if (wordToNumberMap.isEmpty()) {
            throw RuntimeException("wordToNumberMapArray is empty! Set wordToNumberMapArray")
        }

        for (word in russianWords) {
            val lowercaseWord = word.trim().lowercase()
            val f1 = lowercaseWord.toDoubleOrNull()
            val number: Double = f1 ?: (wordToNumberMap[NUMBER_MAP]?.get(lowercaseWord) ?: wordToNumberMap[LARGE_NUMBER_MAP]?.get(lowercaseWord)) ?: 0.0

            if (number >= 100) {
                result += currentNumber * number
                currentNumber = 0.0
            } else {
                if (number >= 10) {
                    if (currentNumber > 0.0) {
                        result += currentNumber * number
                    } else {
                        currentNumber = number
                    }
                } else {
                    if (currentNumber > 0.0) {
                        result += currentNumber + number
                        currentNumber = 0.0
                    } else {
                        currentNumber = number
                    }
                }
            }
        }

        return result + currentNumber
    }

    private val russianNumberMap = mapOf(
        "ноль" to 0.0,
        "один" to 1.0,
        "два" to 2.0,
        "три" to 3.0,
        "четыре" to 4.0,
        "пять" to 5.0,
        "шесть" to 6.0,
        "семь" to 7.0,
        "восемь" to 8.0,
        "девять" to 9.0,
        "десять" to 10.0,
        "одиннадцать" to 11.0,
        "двенадцать" to 12.0,
        "тринадцать" to 13.0,
        "четырнадцать" to 14.0,
        "пятнадцать" to 15.0,
        "шестнадцать" to 16.0,
        "семнадцать" to 17.0,
        "восемнадцать" to 18.0,
        "девятнадцать" to 19.0,
        "двадцать" to 20.0,
        "тридцать" to 30.0,
        "сорок" to 40.0,
        "пятьдесят" to 50.0,
        "шестьдесят" to 60.0,
        "семьдесят" to 70.0,
        "восемьдесят" to 80.0,
        "девяносто" to 90.0
    )

    private val russianLargeNumberMap = mapOf(
        "сто" to 100.0,
        "двести" to 200.0,
        "триста" to 300.0,
        "четыреста" to 400.0,
        "пятьсот" to 500.0,
        "шестьсот" to 600.0,
        "семьсот" to 700.0,
        "восемьсот" to 800.0,
        "девятьсот" to 900.0,
        "тысяча" to 1000.0,
        "млн" to 1_000_000.0,
        "миллиард" to 1_000_000_000.0
    )

    private val englishNumberMap = mapOf(
        "zero" to 0.0,
        "one" to 1.0,
        "two" to 2.0,
        "three" to 3.0,
        "four" to 4.0,
        "five" to 5.0,
        "six" to 6.0,
        "seven" to 7.0,
        "eight" to 8.0,
        "nine" to 9.0,
        "ten" to 10.0,
        "eleven" to 11.0,
        "twelve" to 12.0,
        "thirteen" to 13.0,
        "fourteen" to 14.0,
        "fifteen" to 15.0,
        "sixteen" to 16.0,
        "seventeen" to 17.0,
        "eighteen" to 18.0,
        "nineteen" to 19.0,
        "twenty" to 20.0,
        "thirty" to 30.0,
        "forty" to 40.0,
        "fifty" to 50.0,
        "sixty" to 60.0,
        "seventy" to 70.0,
        "eighty" to 80.0,
        "ninety" to 90.0
    )

    private val englishLargeNumberMap = mapOf(
        "one hundred" to 100.0,
        "two hundred" to 200.0,
        "three hundred" to 300.0,
        "four hundred" to 400.0,
        "five hundred" to 500.0,
        "six hundred" to 600.0,
        "seven hundred" to 700.0,
        "eight hundred" to 800.0,
        "nine hundred" to 900.0,
        "thousand" to 1000.0,
        "million" to 1_000_000.0,
        "billion" to 1_000_000_000.0
    )

    private val wordToNumberMaps = mapOf(
        Locale.US to mapOf(
            NUMBER_MAP to englishNumberMap,
            LARGE_NUMBER_MAP to englishLargeNumberMap
        ),
        Locale("RU") to mapOf(
            NUMBER_MAP to russianNumberMap,
            LARGE_NUMBER_MAP to russianLargeNumberMap
        )
    )

}
