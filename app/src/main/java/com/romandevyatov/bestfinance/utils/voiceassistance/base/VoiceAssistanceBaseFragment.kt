package com.romandevyatov.bestfinance.utils.voiceassistance.base

import android.os.Build
import android.os.Bundle
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.romandevyatov.bestfinance.ui.activity.MainActivity
import com.romandevyatov.bestfinance.utils.localization.LocaleUtil
import com.romandevyatov.bestfinance.utils.voiceassistance.CustomSpeechRecognitionListener
import com.romandevyatov.bestfinance.utils.voiceassistance.CustomSpeechRecognizer
import com.romandevyatov.bestfinance.utils.voiceassistance.CustomTextToSpeech
import com.romandevyatov.bestfinance.utils.voiceassistance.InputState

abstract class VoiceAssistanceBaseFragment : Fragment() {

    protected var spokenValue: String? = null
    protected var textToSpeech: CustomTextToSpeech? = null
    protected var speechRecognizer: CustomSpeechRecognizer? = null
    protected lateinit var currentStageName: InputState
    protected var currentStageIndex: Int = -1
    protected var steps: MutableList<InputState> = mutableListOf()
    protected var voicedWalletName: String? = null

    protected abstract fun calculateSteps(): MutableList<InputState>
    protected abstract fun handleUserInput(handledSpokenValue: String, currentStage: InputState)

    override fun onDestroy() {
        super.onDestroy()

        textToSpeech?.finish()

        speechRecognizer?.destroy()
    }

    open fun startAddingTransaction(textToSpeak: String) {
        val isNull = steps.size == 0

        steps.clear()
        steps.addAll(calculateSteps())
        currentStageIndex = 0

        if (isNull) {
            startVoiceAssistance(textToSpeak)
        } else {
            startVoiceAssistance()
        }
    }

    protected open fun setUpSpeechRecognizerListener() {
        val recognitionListener = object : CustomSpeechRecognitionListener(requireContext()) {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResults(results: Bundle?) {
                val recognizedStrings = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

                if (recognizedStrings != null && recognizedStrings.isNotEmpty()) {
                    val currentSpokenText = recognizedStrings[0]
                    val handledSpokenValue = handleRecognizedText(currentSpokenText)

                    handleUserInput(handledSpokenValue, currentStageName)
                }
            }
        }
        speechRecognizer?.setRecognitionListener(recognitionListener)
    }

    protected fun startVoiceAssistance(textBefore: String? = "") {
        spokenValue = null

        currentStageName = steps[currentStageIndex]

        speakTextAndRecognize(textBefore + getString(currentStageName.settingTextResId), false)
    }

    protected fun nextStage(speakTextBefore: String = "") {
        currentStageIndex++
        startVoiceAssistance(speakTextBefore)
    }

    protected fun speakTextAndRecognize(textToSpeak: String, onlySpeechText: Boolean = true) {
        textToSpeech?.setIsTextToSpeechDone(onlySpeechText)
        textToSpeech?.speak(textToSpeak)
    }

    protected fun speakText(text: String) {
        speakTextAndRecognize(text, true)
    }

    protected fun setUpTextToSpeech() {
        textToSpeech = speechRecognizer?.let {
            CustomTextToSpeech(
                requireContext(),
                it,
                (requireActivity() as MainActivity).getProtectedStorage(),
                null
            )
        }
    }

    protected open fun setUpSpeechRecognizer() {
        val storage = (requireActivity() as MainActivity).getProtectedStorage()
        speechRecognizer = CustomSpeechRecognizer(
            requireContext(),
            LocaleUtil.getLocaleFromPrefCode(storage.getPreferredLocale())
        )
        setUpSpeechRecognizerListener()
    }
}
