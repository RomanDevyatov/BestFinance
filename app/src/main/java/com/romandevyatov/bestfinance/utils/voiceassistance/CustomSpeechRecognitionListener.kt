package com.romandevyatov.bestfinance.utils.voiceassistance

import android.content.ContentValues
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.util.Log
import java.util.*

open class CustomSpeechRecognitionListener: RecognitionListener {
    override fun onReadyForSpeech(params: Bundle?) { }

    override fun onBeginningOfSpeech() { }

    override fun onRmsChanged(rmsdB: Float) { }

    override fun onBufferReceived(buffer: ByteArray?) { }

    override fun onEndOfSpeech() { }

    override fun onError(error: Int) {
        val errorMessage = when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio error"
            SpeechRecognizer.ERROR_CLIENT -> "Client error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
            SpeechRecognizer.ERROR_NETWORK -> "Network error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
            SpeechRecognizer.ERROR_NO_MATCH -> "No match found"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer is busy"
            SpeechRecognizer.ERROR_SERVER -> "Server error"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Speech timeout"
            else -> "Unknown error"
        }

        Log.e(ContentValues.TAG, "Speech recognition error: $errorMessage")
    }

    override fun onResults(results: Bundle?) { }

    override fun onPartialResults(partialResults: Bundle?) { }

    override fun onEvent(eventType: Int, params: Bundle?) { }

    companion object {
        fun handleRecognizedText(recognizedText: String): String {
            return recognizedText.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            }
        }
    }
}