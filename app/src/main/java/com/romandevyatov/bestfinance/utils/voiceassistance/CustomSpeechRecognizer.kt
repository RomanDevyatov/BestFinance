package com.romandevyatov.bestfinance.utils.voiceassistance

import android.content.Context
import android.content.Intent
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import java.util.Locale

class CustomSpeechRecognizer(context: Context, locale: Locale = Locale.getDefault()) {

    private val speechRecognizer: SpeechRecognizer? = SpeechRecognizer.createSpeechRecognizer(context)
    private val defaultIntent: Intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

    init {
        defaultIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        defaultIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, locale.toLanguageTag())
    }

    fun setRecognitionListener(listener: RecognitionListener) {
        speechRecognizer!!.setRecognitionListener(listener)
    }

    fun startListening(customIntent: Intent?) {
        val intent = customIntent ?: defaultIntent

        speechRecognizer!!.startListening(intent)
    }

    fun stopListening() {
        speechRecognizer!!.stopListening()
    }

    fun destroy() {
        speechRecognizer?.destroy()
    }
}
