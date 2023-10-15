package com.romandevyatov.bestfinance.ui.fragments.add.transfer

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.data.entities.TransferHistory
import com.romandevyatov.bestfinance.data.entities.Wallet
import com.romandevyatov.bestfinance.data.roomdb.converters.LocalDateTimeRoomTypeConverter.Companion.dateFormat
import com.romandevyatov.bestfinance.data.roomdb.converters.LocalDateTimeRoomTypeConverter.Companion.dateTimeFormatter
import com.romandevyatov.bestfinance.data.roomdb.converters.LocalDateTimeRoomTypeConverter.Companion.timeFormat
import com.romandevyatov.bestfinance.data.validation.EmptyValidator
import com.romandevyatov.bestfinance.data.validation.IsDigitValidator
import com.romandevyatov.bestfinance.data.validation.IsEqualValidator
import com.romandevyatov.bestfinance.data.validation.base.BaseValidator
import com.romandevyatov.bestfinance.databinding.FragmentAddTransferBinding
import com.romandevyatov.bestfinance.ui.adapters.spinner.SpinnerAdapter
import com.romandevyatov.bestfinance.utils.Constants
import com.romandevyatov.bestfinance.utils.Constants.ADD_NEW_WALLET
import com.romandevyatov.bestfinance.utils.Constants.CLICK_DELAY_MS
import com.romandevyatov.bestfinance.utils.Constants.SHOW_DROP_DOWN_DELAY_MS
import com.romandevyatov.bestfinance.utils.Constants.SPINNER_FROM
import com.romandevyatov.bestfinance.utils.Constants.SPINNER_TO
import com.romandevyatov.bestfinance.utils.SpinnerUtil
import com.romandevyatov.bestfinance.utils.voiceassistance.CustomSpeechRecognitionListener
import com.romandevyatov.bestfinance.utils.voiceassistance.InputState
import com.romandevyatov.bestfinance.utils.voiceassistance.NumberConverter
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.AddTransferViewModel
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.TransferHistoryViewModel
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.WalletViewModel
import com.romandevyatov.bestfinance.viewmodels.shared.SharedModifiedViewModel
import com.romandevyatov.bestfinance.viewmodels.shared.models.AddTransferForm
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.util.*

// TODO: validate if the same wallets are chosen

@AndroidEntryPoint
class AddTransferFragment : Fragment() {

    private var _binding: FragmentAddTransferBinding? = null
    private val binding get() = _binding!!

    private val walletViewModel: WalletViewModel by viewModels()
    private val transferHistoryViewModel: TransferHistoryViewModel by viewModels()
    private val addTransferViewModel: AddTransferViewModel by viewModels()
    private val sharedModViewModel: SharedModifiedViewModel<AddTransferForm> by activityViewModels()
    private val args: AddTransferFragmentArgs by navArgs()

    private var fromSpinnerValueGlobalBeforeAdd: String? = null
    private var toSpinnerValueGlobalBeforeAdd: String? = null

    private var isButtonClickable = true

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var intentGlob: Intent
    private var textToSpeech: TextToSpeech? = null
    private lateinit var handler: Handler

    private var spokenValue: String? = null
    private var isTextToSpeechDone = true

    private var steps: MutableList<InputState> = mutableListOf()
    private var currentStageIndex: Int = -1
    private lateinit var currentStageName: InputState

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTransferBinding.inflate(inflater, container, false)

        setUpSpeechRecognizer()

        setUpTextToSpeech()

        handler = Handler(Looper.getMainLooper())

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                sharedModViewModel.set(null)
                findNavController().navigate(R.id.action_add_transfer_fragment_to_navigation_home)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        setSpinners()

        setDateEditText()
        setTimeEditText()

        setButtonOnClickListener(view)

        restoreAmountDateCommentValues()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        walletViewModel.allWalletsNotArchivedLiveData.removeObservers(viewLifecycleOwner)

        textToSpeech?.stop()
        textToSpeech?.shutdown()

        speechRecognizer.destroy()

        _binding = null
    }

    fun setIntentGlob(intent: Intent) {
        intentGlob = intent
    }

    fun startAddingTransaction(textToSpeak: String) {
        val isNull = steps.size == 0

        steps.clear()
        steps.addAll(getSteps())
        currentStageIndex = 0

        if (isNull) {
            startVoiceAssistance(textToSpeak)
        } else {
            startVoiceAssistance()
        }
    }

    private fun setUpTextToSpeech() {
        textToSpeech = TextToSpeech(requireContext()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val languageResult = textToSpeech?.setLanguage(Locale.getDefault())
                if (languageResult == TextToSpeech.LANG_MISSING_DATA
                    || languageResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(requireContext(), "language is not supported", Toast.LENGTH_SHORT).show()
                }

                val speechRateResult = textToSpeech?.setSpeechRate(1.0.toFloat())
                if (speechRateResult == TextToSpeech.ERROR) {
                    Toast.makeText(requireContext(), "Error while setting speech rate", Toast.LENGTH_SHORT).show()
                }

                textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) { }

                    override fun onDone(utteranceId: String?) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            if (!isTextToSpeechDone) {
                                isTextToSpeechDone = true
                                speechRecognizer.startListening(intentGlob)
                            }
                        }, Constants.DEFAULT_DELAY_AFTER_SPOKEN_TEXT)
                    }

                    override fun onError(utteranceId: String?) { }
                })
            }
        }
    }

    private fun setUpSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())

        val recognitionListener = object : CustomSpeechRecognitionListener() {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResults(results: Bundle?) {
                val recognizedStrings = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (recognizedStrings != null && recognizedStrings.isNotEmpty()) {
                    val currentSpokenText = recognizedStrings[0]
                    val handledSpokenValue = handleRecognizedText(currentSpokenText)

                    when (currentStageName) {
                        InputState.WALLET_FROM -> handleWalletInput(handledSpokenValue, binding.fromWalletNameSpinner)
                        InputState.WALLET_TO -> handleWalletInput(handledSpokenValue, binding.toWalletNameSpinner)
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

    private fun getSteps(): MutableList<InputState> {
        val steps: MutableList<InputState> = mutableListOf()

        if (binding.fromWalletNameSpinner .text.isEmpty()) {
            steps.add(InputState.WALLET_FROM)
        }

        if (binding.toWalletNameSpinner.text.isEmpty()) {
            steps.add(InputState.WALLET_TO)
        }

        if (binding.amountEditText.text.toString().isEmpty()) {
            steps.add(InputState.AMOUNT)
        }

        if (binding.commentEditText.text.toString().isEmpty()) {
            steps.add(InputState.COMMENT)
        }

        steps.add(InputState.CONFIRM)

        return steps
    }

    private fun startVoiceAssistance(textBefore: String = "") {
        spokenValue = null

        currentStageName = steps[currentStageIndex]

        speakTextAndRecognize(textBefore + currentStageName.settingText, false)
    }

    private fun speakTextAndRecognize(textToSpeak: String, speakTextOnly: Boolean = true) {
        isTextToSpeechDone = speakTextOnly

        textToSpeech?.speak(
            textToSpeak,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "uniqueUtteranceId")
    }

    private fun speakText(text: String) {
        speakTextAndRecognize(text, true)
    }

    private fun nextStage(speakTextBefore: String = "") {
        currentStageIndex++
        startVoiceAssistance(speakTextBefore)
    }

    private fun handleWalletInput(currentSpokenText: String, bindingWalletSpinner: AutoCompleteTextView) { // wallet
        if (spokenValue == null) { // WALLET_FROM, WALLET_TO
            val wallets = SpinnerUtil.getAllItemsFromAutoCompleteTextView(bindingWalletSpinner)

            if (wallets.contains(currentSpokenText)) { // success
                bindingWalletSpinner.setText(currentSpokenText, false)

                val isReset: Boolean = when (currentStageName) {
                    InputState.WALLET_FROM -> checkWalletFromSpinnersEqual(isResetFrom = false)
                    InputState.WALLET_TO -> checkWalletFromSpinnersEqual(isResetFrom = true)
                    else -> false
                }

                if (isReset) {
                    when (currentStageName) {
                        InputState.WALLET_FROM -> {
                            val idTo = steps.indexOf(InputState.WALLET_TO)
                            if (idTo == -1) {
                                steps.add(1, InputState.WALLET_TO)
                            }
                            currentStageIndex = 1
                        }
                        InputState.WALLET_TO -> {
                            val idFrom = steps.indexOf(InputState.WALLET_FROM)
                            if (idFrom == -1) {
                                steps.add(0, InputState.WALLET_FROM)
                            } else {
                                steps.removeAt(1)
                            }
                            currentStageIndex = 0
                        }
                        else -> {}
                    }
                    startVoiceAssistance()
                } else {
                    nextStage()
                }
            } else {
                spokenValue = currentSpokenText

                speakTextAndRecognize("Wallet '$currentSpokenText' doesn't exist. Do you want to create a new wallet with this name? (Yes/No)", false)
            }
        } else if (!spokenValue.equals("-1")) {
            when (currentSpokenText.lowercase()) {
                "yes" -> {
                    currentStageName = InputState.SET_BALANCE

                    speakTextAndRecognize("Adding $spokenValue wallet, set wallet balance", false) // move further
                }
                "no" -> { // then ask exit or start again?
                    spokenValue = "-1" // any

                    speakTextAndRecognize("Do you want to continue and call wallet name one more time? (Yes/No)", false)
                }
                else -> speakText("You sad $currentSpokenText. Exiting")
            }
        } else if (spokenValue.equals("-1")) {
            when (currentSpokenText.lowercase()) {
                "yes" -> startVoiceAssistance() // start again
                "no" -> { // exit
                    speakText("Exiting")
                }
                else -> speakText("You sad $currentSpokenText. Exiting")
            }
            spokenValue = null
        }
    }

    private fun handleWalletBalanceInput(spokenBalanceText: String) {
        val textNumbers = spokenBalanceText.replace(",", "")

        val convertedNumber = NumberConverter.convertSpokenTextToNumber(textNumbers)

        if (convertedNumber != null && spokenValue != null) {
            val newWallet = Wallet(
                name = spokenValue.toString(),
                balance = convertedNumber
            )
            addTransferViewModel.insertWallet(newWallet)

            currentStageName = steps[currentStageIndex]
            if (currentStageName == InputState.WALLET_FROM) {
                binding.fromWalletNameSpinner.setText(spokenValue, false)
            } else if (currentStageName == InputState.WALLET_TO) {
                binding.toWalletNameSpinner.setText(spokenValue, false)
            }

            spokenValue = null

            nextStage()
        } else if (!spokenValue.equals("-1") || convertedNumber == null) {
            spokenValue = "-1"

            speakTextAndRecognize("Incorrect wallet balance. Do you want to continue and call wallet balance one more time? (Yes/No)" , false)
        } else if (spokenValue.equals("-1")) {
            when (spokenBalanceText.lowercase()) {
                "yes" -> { // start again setting balance
                    val ask = "Set wallet balance."
                    speakTextAndRecognize(ask , false)
                }
                "no" -> { // exit
                    speakText("Exiting")
                }
                else -> speakText("You sad $spokenBalanceText. Exiting")
            }
            spokenValue = null
        }
    }

    private fun handleAmountInput(spokenAmountText: String) { // amount
        if (spokenValue == null) {
            val textNumbers = spokenAmountText.replace(",", "")

            val convertedNumber = NumberConverter.convertSpokenTextToNumber(textNumbers)

            if (convertedNumber != null) {
                binding.amountEditText.setText(convertedNumber.toString())
                nextStage()
            } else {
                spokenValue = spokenAmountText

                speakTextAndRecognize("Incorrect number. Do you want to continue and call amount one more time? (Yes/No)" , false)
            }
        } else {
            when (spokenAmountText.lowercase()) {
                "yes" -> { // start again
                    startVoiceAssistance()
                }
                "no" -> { // exit
                    speakText("Terminated")
                }
                else -> speakText("You sad $spokenAmountText. Exiting")
            }
        }
    }

    private fun handleCommentInput(spokenComment: String) { // comment
        var speakText = "Comment is set."
        if (spokenComment.isNotEmpty()) {
            binding.commentEditText.setText(spokenComment)
        } else speakText = "Comment is empty."

        nextStage(speakTextBefore = speakText)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleConfirmInput(sentSpokenValue: String) {
        when (sentSpokenValue.lowercase()) {
            "yes" -> { // sent
                sendTransferHistory()
                speakText("History record is added")
            }
            "no" -> { // no
                speakText("Exiting")
            }
            else -> speakText("You sad $sentSpokenValue. Exiting")
        }
        spokenValue = null
    }

    private val archiveFromWalletListener =
        object : SpinnerAdapter.DeleteItemClickListener {

            @RequiresApi(Build.VERSION_CODES.O)
            override fun archive(name: String) {
                archiveWallet(name)

                sharedModViewModel.set(null)

                dismissAndDropdownSpinner(binding.fromWalletNameSpinner)
            }
        }

    private val archiveToWalletListener =
        object : SpinnerAdapter.DeleteItemClickListener {

            @RequiresApi(Build.VERSION_CODES.O)
            override fun archive(name: String) {
                archiveWallet(name)

                sharedModViewModel.set(null)

                dismissAndDropdownSpinner(binding.toWalletNameSpinner)
            }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun archiveWallet(name: String) {
        addTransferViewModel.archiveWallet(name)
        if (binding.toWalletNameSpinner.text.toString() == name) {
            binding.toWalletNameSpinner.text = null
            toSpinnerValueGlobalBeforeAdd = null
        }
        if (binding.fromWalletNameSpinner.text.toString() == name) {
            binding.fromWalletNameSpinner.text = null
            fromSpinnerValueGlobalBeforeAdd = null
        }
    }

    private fun setSpinners() {
        setFromWalletSpinnerAdapter()
        setFromSpinnerListener()

        setToWalletSpinnerAdapter()
        setToSpinnerListener()
    }

    private fun setFromWalletSpinnerAdapter() {
        walletViewModel.allWalletsNotArchivedLiveData.observe(viewLifecycleOwner) { wallets ->

            val spinnerItems = getWalletItemsSpinner(wallets)

            val walletSpinnerAdapter =
                SpinnerAdapter(requireContext(), R.layout.item_with_del, spinnerItems, ADD_NEW_WALLET, archiveFromWalletListener)

            binding.fromWalletNameSpinner.setAdapter(walletSpinnerAdapter)

            setIfAvailableFromWalletSpinnersValue(walletSpinnerAdapter)
        }
    }

    private fun setToWalletSpinnerAdapter() {
        walletViewModel.allWalletsNotArchivedLiveData.observe(viewLifecycleOwner) { wallets ->

            val spinnerItems = getWalletItemsSpinner(wallets)

            val walletSpinnerAdapter = SpinnerAdapter(requireContext(), R.layout.item_with_del, spinnerItems, ADD_NEW_WALLET, archiveToWalletListener)

            binding.toWalletNameSpinner.setAdapter(walletSpinnerAdapter)

            setIfAvailableToWalletSpinnersValue(walletSpinnerAdapter)
        }
    }

    private fun getWalletItemsSpinner(walletList: List<Wallet>?): ArrayList<String> {
        val spinnerItems = ArrayList<String>()

        walletList?.forEach { it ->
            spinnerItems.add(it.name)
        }
        spinnerItems.add(ADD_NEW_WALLET)

        return spinnerItems
    }

    private fun setFromSpinnerListener() {
        binding.fromWalletNameSpinner.setOnItemClickListener {
                _, _, _, _ ->

            checkWalletFromSpinnersEqual(isResetFrom = false)

            val selectedWalletNameFrom =
                binding.fromWalletNameSpinner.text.toString()

            if (selectedWalletNameFrom == ADD_NEW_WALLET) {
                binding.fromWalletNameSpinner.setText(fromSpinnerValueGlobalBeforeAdd, false)

                saveAddTransfer()

                navigateAddNewWallet(SPINNER_FROM)
            } else {
                fromSpinnerValueGlobalBeforeAdd = selectedWalletNameFrom
            }
        }
    }

    private fun setToSpinnerListener() {
        binding.toWalletNameSpinner.setOnItemClickListener {
                _, _, _, _ ->

            checkWalletFromSpinnersEqual(isResetFrom = true)

            val selectedWalletNameTo =
                binding.toWalletNameSpinner.text.toString()

            if (selectedWalletNameTo == ADD_NEW_WALLET) {
                binding.fromWalletNameSpinner.setText(toSpinnerValueGlobalBeforeAdd, false)

                saveAddTransfer()

                navigateAddNewWallet(SPINNER_TO)
            } else {
                toSpinnerValueGlobalBeforeAdd = selectedWalletNameTo
            }
        }
    }

    private fun checkWalletFromSpinnersEqual(isResetFrom: Boolean): Boolean {
        val from = binding.fromWalletNameSpinner.text.toString()
        val to = binding.toWalletNameSpinner.text.toString()

        if (from == to) {
            return if (isResetFrom) {
                binding.fromWalletNameSpinner.text = null
                true
            } else {
                binding.toWalletNameSpinner.text = null
                true
            }
        }

        return false
    }

    private fun setIfAvailableFromWalletSpinnersValue(walletSpinnerAdapter: SpinnerAdapter) {
        val savedWalletName = args.walletName ?: sharedModViewModel.modelForm?.fromWalletSpinnerValue
        val spinnerTypeArg = args.spinnerType

        if (savedWalletName?.isNotBlank() == true && spinnerTypeArg == SPINNER_FROM && isNameInAdapter(walletSpinnerAdapter, savedWalletName)) {
            fromSpinnerValueGlobalBeforeAdd = savedWalletName

            binding.fromWalletNameSpinner.setText(savedWalletName, false)
        }
    }

    private fun setIfAvailableToWalletSpinnersValue(walletSpinnerAdapter: SpinnerAdapter) {
        val savedWalletName = args.walletName ?: sharedModViewModel.modelForm?.fromWalletSpinnerValue
        val spinnerTypeArg = args.spinnerType

        if (savedWalletName?.isNotBlank() == true && spinnerTypeArg == SPINNER_TO && isNameInAdapter(walletSpinnerAdapter, savedWalletName)) {
            toSpinnerValueGlobalBeforeAdd = savedWalletName

            binding.toWalletNameSpinner.setText(savedWalletName, false)
        }
    }

    private fun isNameInAdapter(subGroupSpinnerAdapter: SpinnerAdapter, savedSubGroupName: String?): Boolean {
        return subGroupSpinnerAdapter.getPosition(savedSubGroupName) > -1
    }

    private fun restoreAmountDateCommentValues() {
        val mod = sharedModViewModel.modelForm

        if (mod?.amount != null) {
            binding.amountEditText.setText(mod.amount)
        }

        if (mod?.date != null) {
            binding.dateEditText.setText(mod.date)
        }

        if (mod?.time != null) {
            binding.timeEditText.setText(mod.time)
        }

        if (mod?.comment != null) {
            binding.commentEditText.setText(mod.comment)
        }
    }

    private fun navigateAddNewWallet(spinnerType: String) {
        val action = AddTransferFragmentDirections.actionAddTransferFragmentToNavigationAddWallet()
        action.source = Constants.ADD_TRANSFER_HISTORY_FRAGMENT
        action.spinnerType = spinnerType
        findNavController().navigate(action)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setDateEditText() {
        val selectedDate = Calendar.getInstance()
        val datePickerListener = DatePickerDialog.OnDateSetListener() {
                _, year, month, dayOfMonth ->
            selectedDate.set(Calendar.YEAR, year)
            selectedDate.set(Calendar.MONTH, month)
            selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            binding.dateEditText.setText(dateFormat.format(selectedDate.time))
        }

        binding.dateEditText.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                datePickerListener,
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.dateEditText.setText(dateFormat.format(selectedDate.time))
    }

    private fun setTimeEditText() {
        val selectedTime = Calendar.getInstance()

        val timePickerListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
            selectedTime.set(Calendar.MINUTE, minute)
            binding.timeEditText.setText(timeFormat.format(selectedTime.time))
        }

        binding.timeEditText.setOnClickListener {
            TimePickerDialog(
                requireContext(),
                timePickerListener,
                selectedTime.get(Calendar.HOUR_OF_DAY),
                selectedTime.get(Calendar.MINUTE),
                false
            ).show()
        }

        binding.timeEditText.setText(timeFormat.format(selectedTime.time))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setButtonOnClickListener(view: View) {
        binding.transferButton.setOnClickListener {
            if (!isButtonClickable) return@setOnClickListener
            isButtonClickable = false
            view.isEnabled = false

            sendTransferHistory()

            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                isButtonClickable = true
                view.isEnabled = true
            }, CLICK_DELAY_MS.toLong())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendTransferHistory() {
        walletViewModel.allWalletsNotArchivedLiveData.observe(viewLifecycleOwner) { wallets ->
            val amountBinding = binding.amountEditText.text.toString().trim()
            val dateBinding = binding.dateEditText.text.toString().trim()
            val timeBinding = binding.timeEditText.text.toString().trim()
            val comment = binding.commentEditText.text.toString().trim()

            val walletFromNameBinding = binding.fromWalletNameSpinner.text.toString()
            val walletToNameBinding = binding.toWalletNameSpinner.text.toString()

            val isEqualSpinnerNamesValidation = IsEqualValidator(walletFromNameBinding, walletToNameBinding).validate()
            binding.fromWalletNameSpinnerLayout.error = if (!isEqualSpinnerNamesValidation.isSuccess) getString(isEqualSpinnerNamesValidation.message) else null
            binding.toWalletNameSpinnerLayout.error = if (!isEqualSpinnerNamesValidation.isSuccess) getString(isEqualSpinnerNamesValidation.message) else null

            val amountValidation = BaseValidator.validate(EmptyValidator(amountBinding), IsDigitValidator(amountBinding))
            binding.amountEditText.error = if (!amountValidation.isSuccess) getString(amountValidation.message) else null

            val dateBindingValidation = EmptyValidator(dateBinding).validate()
            binding.dateLayout.error = if (!dateBindingValidation.isSuccess) getString(dateBindingValidation.message) else null

            val timeBindingValidation = EmptyValidator(timeBinding).validate()
            binding.timeLayout.error = if (!timeBindingValidation.isSuccess) getString(timeBindingValidation.message) else null

            if (isEqualSpinnerNamesValidation.isSuccess
                && amountValidation.isSuccess
                && dateBindingValidation.isSuccess
                && timeBindingValidation.isSuccess
            ) {
                val walletFrom = wallets.find { it.name == walletFromNameBinding }
                updateWalletFrom(walletFrom!!, amountBinding.toDouble())

                val walletTo = wallets.find { it.name == walletToNameBinding }
                updateWalletTo(walletTo!!, amountBinding.toDouble())

                val fullDateTime = dateBinding.plus(" ").plus(timeBinding)
                val parsedLocalDateTime = LocalDateTime.from(dateTimeFormatter.parse(fullDateTime))

                insertTransferHistoryRecord(comment, walletFrom, walletTo, amountBinding.toDouble(), parsedLocalDateTime)

                sharedModViewModel.set(null)
                val action = AddTransferFragmentDirections.actionAddTransferFragmentToNavigationHome()
                findNavController().navigate(action)
            }
        }
    }

    private fun saveAddTransfer() {
        val amountBinding = binding.amountEditText.text.toString().trim()
        val dateBinding = binding.dateEditText.text.toString().trim()
        val timeBinding = binding.timeEditText.text.toString().trim()
        val commentBinding = binding.commentEditText.text.toString().trim()

        var isSend = true
        if (amountBinding.isNotBlank()) {
            val amountBindingValidation = IsDigitValidator(amountBinding).validate()
            binding.amountEditTextLayout.error =
                if (!amountBindingValidation.isSuccess) getString(amountBindingValidation.message) else null

            if (!amountBindingValidation.isSuccess) {
                isSend = false
            }
        }

        if (isSend) {
            val addTransactionForm = AddTransferForm(
                fromWalletSpinnerValue = fromSpinnerValueGlobalBeforeAdd,
                toWalletSpinnerValue = toSpinnerValueGlobalBeforeAdd,
                amount = amountBinding,
                comment = commentBinding,
                date = dateBinding,
                time = timeBinding
            )
            sharedModViewModel.set(addTransactionForm)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun insertTransferHistoryRecord(comment: String,
                                            walletFrom: Wallet,
                                            walletTo: Wallet,
                                            amount: Double,
                                            parsedLocalDateTime: LocalDateTime) {
        val transferHistory = TransferHistory(
            amount = amount,
            fromWalletId = walletFrom.id!!,
            toWalletId = walletTo.id!!,
            date = parsedLocalDateTime,
            comment = comment,
            createdDate = LocalDateTime.now()
        )
        transferHistoryViewModel.insertTransferHistory(transferHistory)
    }

    private fun updateWalletTo(walletTo: Wallet, amount: Double) {
        val updatedWalletToInput = walletTo.input.plus(amount)
        val updatedWalletToBalance = walletTo.balance.plus(amount)

        val updatedWalletTo = walletTo.copy(
            balance = updatedWalletToBalance,
            input = updatedWalletToInput
        )
        walletViewModel.updateWallet(updatedWalletTo)
    }

    private fun updateWalletFrom(walletFrom: Wallet, amount: Double) {
        val updatedWalletFromOutput = walletFrom.output.plus(amount)
        val updatedWalletFromBalance = walletFrom.balance.minus(amount)

        val updatedWalletFrom = walletFrom.copy(
            balance = updatedWalletFromBalance,
            output = updatedWalletFromOutput
        )

        walletViewModel.updateWallet(updatedWalletFrom)
    }

    private fun dismissAndDropdownSpinner(spinner: AutoCompleteTextView) {
        spinner.dismissDropDown()
        spinner.postDelayed({
            spinner.showDropDown()
        }, SHOW_DROP_DOWN_DELAY_MS)
    }

}
