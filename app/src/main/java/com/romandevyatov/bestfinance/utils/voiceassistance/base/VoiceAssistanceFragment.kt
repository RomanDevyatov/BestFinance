package com.romandevyatov.bestfinance.utils.voiceassistance.base

import android.os.Build
import android.os.Bundle
import android.speech.SpeechRecognizer
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.romandevyatov.bestfinance.ui.activity.MainActivity
import com.romandevyatov.bestfinance.utils.localization.LocaleUtil
import com.romandevyatov.bestfinance.utils.voiceassistance.CustomSpeechRecognitionListener
import com.romandevyatov.bestfinance.utils.voiceassistance.CustomSpeechRecognizer
import com.romandevyatov.bestfinance.utils.voiceassistance.CustomTextToSpeech
import com.romandevyatov.bestfinance.utils.voiceassistance.InputState

abstract class VoiceAssistanceFragment : Fragment() {

    protected var spokenValue: String? = null
    protected var textToSpeech: CustomTextToSpeech? = null
    protected lateinit var speechRecognizer: CustomSpeechRecognizer
    protected lateinit var currentStageName: InputState
    protected var currentStageIndex: Int = -1
    protected var steps: MutableList<InputState> = mutableListOf()
    protected var voicedWalletName: String? = null

    protected abstract fun calculateSteps(): MutableList<InputState>
    abstract fun handleGroupInput(handledSpokenValue: String)
    abstract fun handleSubGroupInput(handledSpokenValue: String)
    abstract fun handleWalletInput(handledSpokenValue: String)
    abstract fun handleWalletBalanceInput(handledSpokenValue: String)
    abstract fun handleAmountInput(handledSpokenValue: String)
    abstract fun handleCommentInput(handledSpokenValue: String)
    abstract fun handleConfirmInput(handledSpokenValue: String)

    override fun onDestroy() {
        super.onDestroy()

        textToSpeech?.finish()

        speechRecognizer.destroy()
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
        textToSpeech = CustomTextToSpeech(
            requireContext(),
            speechRecognizer,
            (requireActivity() as MainActivity).getProtectedStorage(),
            null
        )
    }

    protected open fun setUpSpeechRecognizerListener() {
        val recognitionListener = object : CustomSpeechRecognitionListener(requireContext()) {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResults(results: Bundle?) {
                val recognizedStrings = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

                if (recognizedStrings != null && recognizedStrings.isNotEmpty()) {
                    val currentSpokenText = recognizedStrings[0]
                    val handledSpokenValue = handleRecognizedText(currentSpokenText)

                    when (currentStageName) {
                        InputState.GROUP -> handleGroupInput(handledSpokenValue)
                        InputState.SUB_GROUP -> handleSubGroupInput(handledSpokenValue)
                        InputState.WALLET -> handleWalletInput(handledSpokenValue)
                        InputState.SET_BALANCE -> handleWalletBalanceInput(handledSpokenValue)
                        InputState.AMOUNT -> handleAmountInput(handledSpokenValue)
                        InputState.COMMENT -> handleCommentInput(handledSpokenValue)
                        InputState.CONFIRM -> handleConfirmInput(handledSpokenValue)
                        else -> {}
                    }
                }
            }
        }
        speechRecognizer.setRecognitionListener(recognitionListener)
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
