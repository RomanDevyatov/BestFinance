package com.romandevyatov.bestfinance.utils.voiceassistance

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast
import java.util.Locale

open class CustomSpeechRecognitionListener(private val context: Context): RecognitionListener {
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
            SpeechRecognizer.ERROR_NO_MATCH -> {
                Toast.makeText(context, "No match found. Please try again.", Toast.LENGTH_SHORT).show()
            }
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
