package com.romandevyatov.bestfinance.utils.voiceassistance

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.widget.Toast
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.utils.Constants
import com.romandevyatov.bestfinance.utils.localization.LocaleUtil
import com.romandevyatov.bestfinance.utils.sharedpreferences.Storage

class CustomTextToSpeech(
    private val context: Context,
    private val speechRecognizer: CustomSpeechRecognizer,
    private val storage: Storage,
    private val customIntent: Intent? = null
) {

    private var textToSpeech: TextToSpeech? = null
    private var isTextToSpeechDone: Boolean = false

    init {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val locale = LocaleUtil.getLocaleFromPrefCode(storage.getPreferredLocale())
                val languageResult = textToSpeech?.setLanguage(locale)
                if (languageResult == TextToSpeech.LANG_MISSING_DATA
                    || languageResult == TextToSpeech.LANG_NOT_SUPPORTED
                ) {
                    Toast.makeText(
                        context,
                        R.string.language_is_not_supported,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                val speechRateResult = textToSpeech?.setSpeechRate(1.0.toFloat())
                if (speechRateResult == TextToSpeech.ERROR) {
                    Toast.makeText(
                        context,
                        "Error while setting speech rate",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {}

                    override fun onDone(utteranceId: String?) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            if (!isTextToSpeechDone) {
                                isTextToSpeechDone = true
                                speechRecognizer.startListening(customIntent)
                            }
                        }, Constants.DEFAULT_DELAY_AFTER_SPOKEN_TEXT)
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {}
                })
            }
        }
    }

    fun setIsTextToSpeechDone(isSpeechOnly: Boolean) {
        isTextToSpeechDone = isSpeechOnly
    }

    fun speak(text: String) {
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "uniqueUtteranceId")
    }

    fun finish() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }
}
