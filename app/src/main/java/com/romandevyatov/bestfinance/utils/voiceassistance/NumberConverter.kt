package com.romandevyatov.bestfinance.utils.voiceassistance

import android.util.Log
import java.util.Locale

object NumberConverter {

    private const val NUMBER_MAP: String = "numberMap"
    private const val LARGE_NUMBER_MAP: String = "largeNumberMap"

    fun convertSpokenTextToNumber(spokenText: String): Double? {
        when (val currentLocale = Locale.getDefault()) {
            Locale.US, Locale("RU") -> {
                if (containsOnlyDigitsAndPeriod(spokenText)) {
                    return postProcessRecognizedNumber(spokenText).toDouble()
                }

                val wordToNumberMapping = wordToNumberMaps[currentLocale] ?: emptyMap()

                val englishWords = spokenText.split(" ")
                return convertWordsToNumber(englishWords, wordToNumberMapping).toDouble()
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
        wordToNumberMap: Map<String, Map<String, Int>>
    ): Int {
        var result = 0
        var currentNumber = 0
        var previousNumber = 0

        if (wordToNumberMap.isEmpty()) {
            throw RuntimeException("wordToNumberMapArray is empty! Set wordToNumberMapArray")
        }

        for (word in russianWords) {
            val lowercaseWord = word.lowercase()
            val number = wordToNumberMap[NUMBER_MAP]?.get(lowercaseWord) ?: wordToNumberMap[LARGE_NUMBER_MAP]?.get(lowercaseWord)

            if (number != null) {
                if (number >= 100) {
                    result += currentNumber * number
                    previousNumber = number
                    currentNumber = 0
                } else {
                    if (number >= 10) {
                        if (currentNumber > 0) {
                            result += currentNumber * number
                        } else {
                            currentNumber = number
                        }
                    } else {
                        if (currentNumber > 0) {
                            result += currentNumber + number
                            currentNumber = 0
                        } else {
                            currentNumber = number
                        }
                    }
                }
            } else {
                Log.d("NumberConverter", "number == null, no map for $word")
            }
        }

        return result + currentNumber + previousNumber
    }

    private fun postProcessRecognizedNumber(text: String): String {
        val segments = text.split(" ")

        val result = StringBuilder()
        var buffer = ""

        for (segment in segments) {
            if (segment.matches(Regex("^\\d+(\\.\\d+)?$")) || segment == "0.0") {
                if (buffer.isNotEmpty()) {
                    result.append(buffer)
                    buffer = ""
                }
                result.append(segment)
            } else {
                if (buffer.isNotEmpty()) {
                    result.append(buffer)
                    buffer = ""
                }
                result.append(segment)
            }
        }

        if (buffer.isNotEmpty()) {
            result.append(buffer)
        }

        return result.toString()
    }

    private val russianNumberMap = mapOf(
        "ноль" to 0,
        "один" to 1,
        "два" to 2,
        "три" to 3,
        "четыре" to 4,
        "пять" to 5,
        "шесть" to 6,
        "семь" to 7,
        "восемь" to 8,
        "девять" to 9,
        "десять" to 10,
        "одиннадцать" to 11,
        "двенадцать" to 12,
        "тринадцать" to 13,
        "четырнадцать" to 14,
        "пятнадцать" to 15,
        "шестнадцать" to 16,
        "семнадцать" to 17,
        "восемнадцать" to 18,
        "девятнадцать" to 19,
        "двадцать" to 20,
        "тридцать" to 30,
        "сорок" to 40,
        "пятьдесят" to 50,
        "шестьдесят" to 60,
        "семьдесят" to 70,
        "восемьдесят" to 80,
        "девяносто" to 90
    )

    private val russianLargeNumberMap = mapOf(
        "сто" to 100,
        "двести" to 200,
        "триста" to 300,
        "четыреста" to 400,
        "пятьсот" to 500,
        "шестьсот" to 600,
        "семьсот" to 700,
        "восемьсот" to 800,
        "девятьсот" to 900,
        "тысяча" to 1000,
        "миллион" to 1_000_000,
        "миллиард" to 1_000_000_000
    )

    private val englishNumberMap = mapOf(
        "zero" to 0,
        "one" to 1,
        "two" to 2,
        "three" to 3,
        "four" to 4,
        "five" to 5,
        "six" to 6,
        "seven" to 7,
        "eight" to 8,
        "nine" to 9,
        "ten" to 10,
        "eleven" to 11,
        "twelve" to 12,
        "thirteen" to 13,
        "fourteen" to 14,
        "fifteen" to 15,
        "sixteen" to 16,
        "seventeen" to 17,
        "eighteen" to 18,
        "nineteen" to 19,
        "twenty" to 20,
        "thirty" to 30,
        "forty" to 40,
        "fifty" to 50,
        "sixty" to 60,
        "seventy" to 70,
        "eighty" to 80,
        "ninety" to 90
    )

    private val englishLargeNumberMap = mapOf(
        "one hundred" to 100,
        "two hundred" to 200,
        "three hundred" to 300,
        "four hundred" to 400,
        "five hundred" to 500,
        "six hundred" to 600,
        "seven hundred" to 700,
        "eight hundred" to 800,
        "nine hundred" to 900,
        "thousand" to 1000,
        "million" to 1_000_000,
        "billion" to 1_000_000_000
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
