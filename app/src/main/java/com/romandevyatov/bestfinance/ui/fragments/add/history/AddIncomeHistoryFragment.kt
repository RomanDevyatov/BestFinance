package com.romandevyatov.bestfinance.ui.fragments.add.history

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
import com.romandevyatov.bestfinance.data.entities.IncomeGroup
import com.romandevyatov.bestfinance.data.entities.IncomeSubGroup
import com.romandevyatov.bestfinance.data.entities.Wallet
import com.romandevyatov.bestfinance.data.entities.relations.IncomeGroupWithIncomeSubGroups
import com.romandevyatov.bestfinance.data.roomdb.converters.LocalDateTimeRoomTypeConverter.Companion.dateFormat
import com.romandevyatov.bestfinance.data.roomdb.converters.LocalDateTimeRoomTypeConverter.Companion.dateTimeFormatter
import com.romandevyatov.bestfinance.data.roomdb.converters.LocalDateTimeRoomTypeConverter.Companion.timeFormat
import com.romandevyatov.bestfinance.data.validation.EmptyValidator
import com.romandevyatov.bestfinance.data.validation.IsDigitValidator
import com.romandevyatov.bestfinance.data.validation.base.BaseValidator
import com.romandevyatov.bestfinance.databinding.FragmentAddIncomeHistoryBinding
import com.romandevyatov.bestfinance.ui.adapters.spinner.GroupSpinnerAdapter
import com.romandevyatov.bestfinance.ui.adapters.spinner.SpinnerItem
import com.romandevyatov.bestfinance.utils.Constants
import com.romandevyatov.bestfinance.utils.Constants.ADD_INCOME_HISTORY_FRAGMENT
import com.romandevyatov.bestfinance.utils.Constants.ADD_NEW_INCOME_GROUP
import com.romandevyatov.bestfinance.utils.Constants.ADD_NEW_INCOME_SUB_GROUP
import com.romandevyatov.bestfinance.utils.Constants.ADD_NEW_WALLET
import com.romandevyatov.bestfinance.utils.SpinnerUtil
import com.romandevyatov.bestfinance.utils.voiceassistance.CustomSpeechRecognitionListener
import com.romandevyatov.bestfinance.utils.voiceassistance.InputState
import com.romandevyatov.bestfinance.utils.voiceassistance.NumberConverter
import com.romandevyatov.bestfinance.viewmodels.*
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.AddIncomeHistoryViewModel
import com.romandevyatov.bestfinance.viewmodels.shared.SharedModifiedViewModel
import com.romandevyatov.bestfinance.viewmodels.shared.models.AddTransactionForm
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_add_income_history.*
import kotlinx.android.synthetic.main.fragment_add_income_history.view.*
import kotlinx.android.synthetic.main.fragment_add_income_sub_group.view.*
import java.time.LocalDateTime
import java.util.*

@AndroidEntryPoint
class AddIncomeHistoryFragment : Fragment() {

    private var _binding: FragmentAddIncomeHistoryBinding? = null
    private val binding get() = _binding!!

    private val addHistoryViewModel: AddIncomeHistoryViewModel by viewModels()
    private val sharedModViewModel: SharedModifiedViewModel<AddTransactionForm> by activityViewModels()
    private val args: AddIncomeHistoryFragmentArgs by navArgs()

    private val groupSpinnerItemsGlobal: MutableList<SpinnerItem> = mutableListOf()
    private val subGroupSpinnerItemsGlobal: MutableList<SpinnerItem> = mutableListOf()
    private val walletItemsGlobal: MutableList<SpinnerItem> = mutableListOf()

    private var groupSpinnerValueGlobalBeforeAdd: String? = null
    private var subGroupSpinnerValueGlobalBeforeAdd: String? = null
    private var walletSpinnerValueGlobalBeforeAdd: String? = null

    private var isButtonClickable = true

    private var textToSpeech: TextToSpeech? = null
    private var spokenValue: String? = null

    private lateinit var intentGlob: Intent
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var handler: Handler
    private lateinit var currentStageName: InputState

    private var steps: MutableList<InputState> = mutableListOf()
    private var currentStageIndex: Int = -1

    private var isTextToSpeechDone = true

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddIncomeHistoryBinding.inflate(inflater, container, false)

        setUpSpeechRecognizer()

        setUpTextToSpeech()

        handler = Handler(Looper.getMainLooper())

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOnBackPressedCallback()

        setSpinners()

        setDateEditText()
        setTimeEditText()

        setButtonOnClickListener(view)

        restoreAmountDateCommentValues()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        textToSpeech?.stop()
        textToSpeech?.shutdown()

        speechRecognizer.destroy()

        _binding = null
    }

    fun setIntentGlob(intent: Intent) {
        intentGlob = intent
    }

    fun startAddingTransaction(textToSpeak: String) {
        if (steps.size == 0) {
            steps.addAll(getNotSetSteps())
            currentStageIndex = 0
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
                    Toast.makeText(requireContext(), R.string.language_is_not_supported, Toast.LENGTH_SHORT).show()
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

    private fun startVoiceAssistance(textBefore: String = "") {
        spokenValue = null

        currentStageName = steps[currentStageIndex]

        speakTextAndRecognize(textBefore + currentStageName.settingText, false)
    }

    private fun nextStage(speakTextBefore: String = "") {
        currentStageIndex++
        startVoiceAssistance(speakTextBefore)
    }

    private fun speakTextAndRecognize(textToSpeak: String, onlySpeechText: Boolean = true) {
        isTextToSpeechDone = onlySpeechText
        textToSpeech?.speak(
            textToSpeak,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "uniqueUtteranceId")
    }

    private fun speakText(text: String) {
        speakTextAndRecognize(text, true)
    }

    private fun getNotSetSteps(): MutableList<InputState> {
        val steps: MutableList<InputState> = mutableListOf()

        if (binding.groupSpinner.text.isEmpty()) {
            steps.add(InputState.GROUP)
        }

        if (binding.subGroupSpinner.text.isEmpty()) {
            steps.add(InputState.SUB_GROUP)
        }

        if (binding.walletSpinner.text.isEmpty()) {
            steps.add(InputState.WALLET)
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

    private fun handleGroupInput(currentSpokenText: String) {
        if (spokenValue == null) {
            val groupList = SpinnerUtil.getAllItemsFromAutoCompleteTextView(binding.groupSpinner)

            if (groupList.contains(currentSpokenText)) { // success
                binding.groupSpinner.setText(currentSpokenText, false)
                resetSubGroupSpinner()
                refreshSubGroupSpinnerByGroupSpinnerValue(currentSpokenText)
                nextStage(speakTextBefore = getString(R.string.group_is_set))
            } else {
                spokenValue = currentSpokenText

                val ask = getString(R.string.group_doesnt_exist, currentSpokenText)
                speakTextAndRecognize(ask, false)
            }
        } else if (!spokenValue.equals("-1")) {
            when (currentSpokenText.lowercase()) {
                "yes" -> { // create new
                    addHistoryViewModel.insertIncomeGroup(
                        IncomeGroup(
                            name = spokenValue!!,
                            isPassive = false
                        )
                    )
                    binding.groupSpinner.setText(spokenValue, false)
                    resetSubGroupSpinner()
                    refreshSubGroupSpinnerByGroupSpinnerValue(spokenValue!!)

                    spokenValue = null

                    nextStage(speakTextBefore = getString(R.string.created_group_is_set))
                }
                "no" -> { // then ask exit or start again?
                    spokenValue = "-1" // any
                    val ask = getString(R.string.call_group_one_more_time)
                    speakTextAndRecognize(ask, false)
                }
                else -> speakText(getString(R.string.you_said, currentSpokenText))
            }
        } else if (spokenValue.equals("-1")) {
            when (currentSpokenText.lowercase()) {
                "yes" -> { // start again
                    startVoiceAssistance()
                }
                "no" -> { // exit
                    speakText(getString(R.string.exit))
                }
                else -> speakText(getString(R.string.you_said, currentSpokenText))
            }

            spokenValue = null
        }
    }

    private fun handleSubGroupInput(currentSpokenText: String) {
        if (spokenValue == null) {
            val subGroupList = SpinnerUtil.getAllItemsFromAutoCompleteTextView(binding.subGroupSpinner)

            if (subGroupList.contains(currentSpokenText)) { // success
                binding.subGroupSpinner.setText(currentSpokenText, false)
                nextStage(speakTextBefore = getString(R.string.subgroup_is_set))
            } else {
                spokenValue = currentSpokenText

                val ask = getString(R.string.add_new_subgroup_prompt, currentSpokenText)
                speakTextAndRecognize(ask, false)
            }
        } else if (!spokenValue.equals("-1")) {
            when (currentSpokenText.lowercase()) {
                "yes" -> {
                    speakText(getString(R.string.adding_subgroup, spokenValue))

                    val groupId = groupSpinnerItemsGlobal.find { it.name == binding.groupSpinner.text.toString() }?.id!!

                    val newIncomeSubGroup = IncomeSubGroup(
                        name = spokenValue!!,
                        incomeGroupId = groupId
                    )
                    addHistoryViewModel.insertIncomeSubGroup(newIncomeSubGroup)
                    binding.subGroupSpinner.setText(spokenValue, false)

                    spokenValue = null

                    nextStage(speakTextBefore = getString(R.string.created_subgroup_is_set))
                }
                "no" -> {
                    spokenValue = "-1"
                    val ask = getString(R.string.continue_prompt)
                    speakTextAndRecognize(ask, false)
                }
                else -> speakText(getString(R.string.you_said, currentSpokenText))
            }
        } else if (spokenValue.equals("-1")) {
            when (currentSpokenText.lowercase()) {
                "yes" -> {
                    startVoiceAssistance()
                }
                "no" -> {
                    speakText(getString(R.string.terminate))
                }
                else -> {
                    speakText(getString(R.string.you_said, currentSpokenText))
                }
            }

            spokenValue = null
        }
    }

    private fun handleWalletInput(currentSpokenText: String) {
        if (spokenValue == null) {
            val wallets = SpinnerUtil.getAllItemsFromAutoCompleteTextView(binding.walletSpinner)

            if (wallets.contains(currentSpokenText)) { // success
                binding.walletSpinner.setText(currentSpokenText, false)
                nextStage() // move further
            } else {
                spokenValue = currentSpokenText

                val ask = getString(R.string.wallet_doesnt_exist, currentSpokenText)
                speakTextAndRecognize(ask, false)
            }
        } else if (!spokenValue.equals("-1")) {
            when (currentSpokenText.lowercase()) {
                "yes" -> {
                    currentStageName = InputState.SET_BALANCE

                    val message = getString(R.string.adding_wallet, spokenValue.toString())
                    speakTextAndRecognize(message, false) // move further
                }
                "no" -> { // then ask exit or start again?
                    spokenValue = "-1" // any

                    val ask = getString(R.string.continue_wallet_prompt)
                    speakTextAndRecognize(ask, false)
                }
                else -> speakText(getString(R.string.you_said, currentSpokenText))
            }
        } else if (spokenValue.equals("-1")) {
            when (currentSpokenText.lowercase()) {
                "yes" -> speakText(getString(R.string.start_again)) // start again
                "no" -> { // exit
                    speakText(getString(R.string.exit))
                }
                else -> speakText(getString(R.string.you_said, currentSpokenText))
            }
            spokenValue = null
        }
    }

    private fun handleWalletBalanceInput(spokenBalanceText: String) {
        val textNumbers = spokenBalanceText.replace(",", "")

        val convertedNumber = NumberConverter.convertSpokenTextToNumber(textNumbers)

        if (convertedNumber != null && spokenValue != null) {
            val newWallet = Wallet(
                name = spokenValue!!,
                balance = convertedNumber
            )
            addHistoryViewModel.insertWallet(newWallet)

            binding.walletSpinner.setText(spokenValue, false)

            spokenValue = null

            nextStage()
        } else if (!spokenValue.equals("-1") || convertedNumber == null) {
            spokenValue = "-1"

            val askSpeechText = getString(R.string.incorrect_balance)
            speakTextAndRecognize(askSpeechText, false)
        } else if (spokenValue.equals("-1")) {
            when (spokenBalanceText.lowercase()) {
                "yes" -> { // start again setting balance
                    val ask = getString(R.string.set_balance)
                    speakTextAndRecognize(ask, false)
                }
                "no" -> { // exit
                    speakText(getString(R.string.exit))
                }
                else -> speakText(getString(R.string.you_said, spokenBalanceText))
            }
            spokenValue = null
        }
    }

    private fun handleAmountInput(spokenAmountText: String) {
        if (spokenValue == null) {
            val textNumbers = spokenAmountText.replace(",", "")

            val convertedNumber = NumberConverter.convertSpokenTextToNumber(textNumbers)

            if (convertedNumber != null) {
                binding.amountEditText.setText(convertedNumber.toString())
                nextStage()
            } else {
                spokenValue = spokenAmountText

                val askSpeechText = getString(R.string.incorrect_number)
                speakTextAndRecognize(askSpeechText, false)
            }
        } else {
            when (spokenAmountText.lowercase()) {
                "yes" -> { // start again
                    startVoiceAssistance()
                }
                "no" -> { // exit
                    speakText(getString(R.string.exit))
                }
                else -> speakText(getString(R.string.you_said, spokenAmountText))
            }
        }
    }


    private fun handleCommentInput(spokenComment: String) {
        val speakText = if (spokenComment.isNotEmpty()) {
            binding.commentEditText.setText(spokenComment)
            getString(R.string.comment_is_set)
        } else {
            getString(R.string.comment_is_empty)
        }

        nextStage(speakTextBefore = speakText)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleConfirmInput(sentSpokenValue: String) {
        when (sentSpokenValue.lowercase()) {
            "yes" -> { // sent
                sendIncomeHistory()
                speakText(getString(R.string.history_added))
            }
            "no" -> { // no
                speakText(getString(R.string.exit))
            }
            else -> speakText(getString(R.string.you_said, sentSpokenValue))
        }
        spokenValue = null
    }

    private fun setOnBackPressedCallback() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                sharedModViewModel.set(null)
                findNavController().navigate(R.id.action_navigation_add_income_to_navigation_home)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun setSpinners() {
        setGroupAndSubGroupSpinnerAdapter()
        setGroupSpinnerOnClickListener()
        setSubGroupSpinnerOnClickListener()

        setWalletSpinnerAdapter()
        setWalletSpinnerOnItemClickListener()
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
        binding.addHistoryButton.setOnClickListener {
            if (!isButtonClickable) return@setOnClickListener
            isButtonClickable = false
            view.isEnabled = false

            sendIncomeHistory()

            handler.postDelayed({
                isButtonClickable = true
                view.isEnabled = true
            }, Constants.CLICK_DELAY_MS.toLong())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendIncomeHistory() {
        val subGroupNameBinding = binding.subGroupSpinner.text.toString()
        val amountBinding = binding.amountEditText.text.toString().trim()
        val commentBinding = binding.commentEditText.text.toString().trim()
        val walletNameBinding = binding.walletSpinner.text.toString()
        val dateBinding = binding.dateEditText.text.toString().trim()
        val timeBinding = binding.timeEditText.text.toString().trim()

        val subGroupNameBindingValidation = EmptyValidator(subGroupNameBinding).validate()
        binding.subGroupSpinnerLayout.error = if (!subGroupNameBindingValidation.isSuccess) getString(subGroupNameBindingValidation.message) else null

        val amountBindingValidation = BaseValidator.validate(EmptyValidator(amountBinding), IsDigitValidator(amountBinding))
        binding.amountLayout.error = if (!amountBindingValidation.isSuccess) getString(amountBindingValidation.message) else null

        val walletNameBindingValidation = EmptyValidator(walletNameBinding).validate()
        binding.walletSpinnerLayout.error = if (!walletNameBindingValidation.isSuccess) getString(walletNameBindingValidation.message) else null

        val dateBindingValidation = EmptyValidator(dateBinding).validate()
        binding.dateLayout.error = if (!dateBindingValidation.isSuccess) getString(dateBindingValidation.message) else null

        val timeBindingValidation = EmptyValidator(timeBinding).validate()
        binding.timeLayout.error = if (!timeBindingValidation.isSuccess) getString(timeBindingValidation.message) else null

        if (subGroupNameBindingValidation.isSuccess
            && amountBindingValidation.isSuccess
            && walletNameBindingValidation.isSuccess
            && dateBindingValidation.isSuccess
            && timeBindingValidation.isSuccess) {

            val fullDateTime = dateBinding.plus(" ").plus(timeBinding)
            val parsedLocalDateTime = LocalDateTime.from(dateTimeFormatter.parse(fullDateTime))

            val subGroupId = subGroupSpinnerItemsGlobal.find { it.name == subGroupNameBinding }?.id
            val walletId = walletItemsGlobal.find { it.name == walletNameBinding }?.id

            if (subGroupId != null && walletId != null) {
                addHistoryViewModel.addIncomeHistoryAndUpdateWallet(
                    subGroupId,
                    amountBinding.toDouble(),
                    commentBinding,
                    parsedLocalDateTime,
                    walletId
                )

                sharedModViewModel.set(null)
                navigateToHome()
            } else {
                Toast.makeText(requireContext(), "subGroupId or walletId is null!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToHome() {
        findNavController().navigate(R.id.action_navigation_add_income_to_navigation_home)
    }

    private fun setGroupAndSubGroupSpinnerAdapter() {
        addHistoryViewModel.getAllIncomeGroupNotArchived().observe(viewLifecycleOwner) { groups ->
            val spinnerGroupItems: MutableList<SpinnerItem> = mutableListOf()

            if (groups.isNotEmpty()) {
                spinnerGroupItems.addAll(getGroupItemsForSpinner(groups))
            }

            groupSpinnerItemsGlobal.clear()
            groupSpinnerItemsGlobal.addAll(spinnerGroupItems)

            spinnerGroupItems.add(SpinnerItem(null, ADD_NEW_INCOME_GROUP))

            val groupSpinnerAdapter = GroupSpinnerAdapter(
                requireContext(),
                R.layout.item_with_del,
                spinnerGroupItems,
                ADD_NEW_INCOME_GROUP,
                archiveGroupListener
            )

            binding.groupSpinner.setAdapter(groupSpinnerAdapter)

            if (groups.isNotEmpty()) {
                setIfAvailableGroupSpinnersValue(spinnerGroupItems)
            }

            setSubGroupSpinnerAdapter()
        }
    }

    private fun setSubGroupSpinnerAdapter() {
        val groupSpinnerBinding = binding.groupSpinner.text.toString()

        if (groupSpinnerBinding.isNotBlank()) {
            setSubGroupSpinnerAdapterByGroupName(groupSpinnerBinding, true)
        } else {
            setEmptySubGroupSpinnerAdapter()
        }
    }

    private fun setSubGroupSpinnerAdapterByGroupName(groupSpinnerBinding: String, isSetIfAvailable: Boolean) {
        // TODO: getIncomeGroupNotArchivedWithIncomeSubGroupsNotArchivedByIncomeGroupNameLiveData doesn't work
        addHistoryViewModel.getIncomeGroupNotArchivedWithIncomeSubGroupsNotArchivedByIncomeGroupNameLiveData(
            groupSpinnerBinding
        )?.observe(viewLifecycleOwner) { groupWithSubGroups ->
            val spinnerSubItems: MutableList<SpinnerItem> = mutableListOf()

            if (groupWithSubGroups != null) {
                spinnerSubItems.addAll(getSpinnerSubItemsNotArchived(groupWithSubGroups))
            }

            subGroupSpinnerItemsGlobal.clear()
            subGroupSpinnerItemsGlobal.addAll(spinnerSubItems)

            spinnerSubItems.add(SpinnerItem(null, ADD_NEW_INCOME_SUB_GROUP))

            val subGroupSpinnerAdapter = GroupSpinnerAdapter(
                    requireContext(),
                    R.layout.item_with_del,
                    spinnerSubItems,
                    ADD_NEW_INCOME_SUB_GROUP,
                    archiveSubGroupListener)

            binding.subGroupSpinner.setAdapter(subGroupSpinnerAdapter)

            if (isSetIfAvailable) {
                setIfAvailableSubGroupSpinnersValue(spinnerSubItems)
            }
        }
    }


    private fun setWalletSpinnerAdapter() {
        addHistoryViewModel.walletsNotArchivedLiveData.observe(viewLifecycleOwner) { wallets ->
            val spinnerWalletItems = getWalletItemsForSpinner(wallets)

            walletItemsGlobal.clear()
            walletItemsGlobal.addAll(spinnerWalletItems)

            spinnerWalletItems.add(SpinnerItem(null, ADD_NEW_WALLET))

            val walletSpinnerAdapter = GroupSpinnerAdapter(
                    requireContext(),
                    R.layout.item_with_del,
                    spinnerWalletItems,
                    ADD_NEW_WALLET,
                    archiveWalletListener)

            binding.walletSpinner.setAdapter(walletSpinnerAdapter)

            setIfAvailableWalletSpinnerValue(spinnerWalletItems)
        }
    }

    private fun setEmptySubGroupSpinnerAdapter() {
        val emptySubGroupSpinnerAdapter = getEmptySubGroupSpinnerAdapter()

        emptySubGroupSpinnerAdapter.add(SpinnerItem(null, ADD_NEW_INCOME_SUB_GROUP))

        binding.subGroupSpinner.setAdapter(emptySubGroupSpinnerAdapter)
    }

    private fun getEmptySubGroupSpinnerAdapter(): GroupSpinnerAdapter {
        val subGroupSpinnerItems: MutableList<SpinnerItem> = mutableListOf()

        return GroupSpinnerAdapter(
            requireContext(),
            R.layout.item_with_del,
            subGroupSpinnerItems,
            ADD_NEW_INCOME_SUB_GROUP,
            archiveSubGroupListener
        )
    }

    private fun setGroupSpinnerOnClickListener() {
        binding.groupSpinner.setOnItemClickListener {
                _, _, _, _ ->

            val selectedGroupName =
                binding.groupSpinner.text.toString()

            if (selectedGroupName != groupSpinnerValueGlobalBeforeAdd) {
                resetSubGroupSpinner()
            }

            if (selectedGroupName == ADD_NEW_INCOME_GROUP) {
                setPrevValue(groupSpinnerValueGlobalBeforeAdd, binding.groupSpinner)

                saveAddTransactionForm()

                val action =
                    AddIncomeHistoryFragmentDirections.actionNavigationAddIncomeToNavigationAddIncomeGroup()
                findNavController().navigate(action)
            } else {
                refreshSubGroupSpinnerByGroupSpinnerValue(selectedGroupName)
            }
        }
    }

    private fun refreshSubGroupSpinnerByGroupSpinnerValue(selectedGroupName: String) {
        setSubGroupSpinnerAdapterByGroupName(selectedGroupName, false)
        groupSpinnerValueGlobalBeforeAdd = selectedGroupName
    }

    private fun setPrevValue(value: String?, spinner: AutoCompleteTextView) {
        spinner.setText(value, false)
    }

    private fun setSubGroupSpinnerOnClickListener() {
        binding.subGroupSpinner.setOnItemClickListener {
                _, _, _, _ ->

            val selectedSubGroupName = binding.subGroupSpinner.text.toString()

            if (selectedSubGroupName == ADD_NEW_INCOME_SUB_GROUP) {
                setPrevValue(subGroupSpinnerValueGlobalBeforeAdd, binding.subGroupSpinner)

                saveAddTransactionForm()

                val action =
                    AddIncomeHistoryFragmentDirections.actionNavigationAddIncomeToNavigationAddSubIncomeGroup()
                action.incomeGroupName = binding.groupSpinner.text.toString()
                findNavController().navigate(action)
            } else {
                subGroupSpinnerValueGlobalBeforeAdd = selectedSubGroupName
            }
        }
    }

    private fun setWalletSpinnerOnItemClickListener() {
        binding.walletSpinner.setOnItemClickListener {
                _, _, _, _ ->

            val selectedWalletName = binding.walletSpinner.text.toString()

            if (selectedWalletName == ADD_NEW_WALLET) {
                setPrevValue(walletSpinnerValueGlobalBeforeAdd, binding.walletSpinner)

                saveAddTransactionForm()

                val action =
                    AddIncomeHistoryFragmentDirections.actionNavigationAddIncomeToNavigationAddWallet()
                action.source = ADD_INCOME_HISTORY_FRAGMENT
                action.spinnerType = null
                findNavController().navigate(action)
            } else {
                walletSpinnerValueGlobalBeforeAdd = selectedWalletName
            }
        }
    }

    private fun setIfAvailableGroupSpinnersValue(
        spinnerGroupItems: MutableList<SpinnerItem>
    ) {
        val savedGroupName = args.incomeGroupName ?: sharedModViewModel.modelForm?.groupSpinnerValue

        if (savedGroupName?.isNotBlank() == true) {
            resetSubGroupSpinner()

            if (spinnerGroupItems.find { it.name == savedGroupName } != null) {
                groupSpinnerValueGlobalBeforeAdd = savedGroupName

                binding.groupSpinner.setText(savedGroupName, false)
            }
        }
    }

    private fun setIfAvailableSubGroupSpinnersValue(spinnerSubItems: MutableList<SpinnerItem>) {
        val savedSubGroupName = args.incomeSubGroupName ?: sharedModViewModel.modelForm?.subGroupSpinnerValue

        if (savedSubGroupName?.isNotBlank() == true && spinnerSubItems.find { it.name == savedSubGroupName} != null) {
            subGroupSpinnerValueGlobalBeforeAdd = savedSubGroupName

            binding.subGroupSpinner.setText(savedSubGroupName, false)
        }
    }

    private fun setIfAvailableWalletSpinnerValue(spinnerWalletItems: MutableList<SpinnerItem>) {
        val savedWalletName = args.walletName ?: sharedModViewModel.modelForm?.walletSpinnerValue

        if (savedWalletName?.isNotBlank() == true && spinnerWalletItems.find { it.name == savedWalletName} != null) {
            walletSpinnerValueGlobalBeforeAdd = savedWalletName

            binding.walletSpinner.setText(savedWalletName, false)
        }
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

    private fun getGroupItemsForSpinner(groups: List<IncomeGroup>): MutableList<SpinnerItem> {
        return groups.map {
            SpinnerItem(it.id, it.name)
        }.toMutableList()
    }

    private fun getSpinnerSubItemsNotArchived(groupWithSubGroups: IncomeGroupWithIncomeSubGroups): MutableList<SpinnerItem> {
        return groupWithSubGroups.incomeSubGroups.filter {
            it.archivedDate == null
        }.map {
            SpinnerItem(it.id, it.name)
        }.toMutableList()
    }

    private fun getWalletItemsForSpinner(wallets: List<Wallet>): MutableList<SpinnerItem> {
        return wallets.map {
            SpinnerItem(it.id, it.name)
        }.toMutableList()

    }

    private fun dismissAndDropdownSpinner(spinner: AutoCompleteTextView) {
        spinner.dismissDropDown()
        spinner.postDelayed({
            spinner.showDropDown()
        }, Constants.SHOW_DROP_DOWN_DELAY_MS)
    }

    private fun saveAddTransactionForm() {
        val amountBinding = binding.amountEditText.text.toString().trim()
        val dateBinding = binding.dateEditText.text.toString().trim()
        val timeBinding = binding.timeEditText.text.toString().trim()
        val commentBinding = binding.commentEditText.text.toString().trim()

        val addTransactionForm = AddTransactionForm(
            groupSpinnerValue = groupSpinnerValueGlobalBeforeAdd,
            subGroupSpinnerValue = subGroupSpinnerValueGlobalBeforeAdd,
            walletSpinnerValue = walletSpinnerValueGlobalBeforeAdd,
            amount = amountBinding,
            date = dateBinding,
            time = timeBinding,
            comment = commentBinding
        )
        sharedModViewModel.set(addTransactionForm)
    }

    private fun resetSubGroupSpinner() {
        binding.subGroupSpinner.text = null
    }

    private val archiveGroupListener =
        object : GroupSpinnerAdapter.DeleteItemClickListener {

            @RequiresApi(Build.VERSION_CODES.O)
            override fun archive(spinnerItem: SpinnerItem) {
                spinnerItem.id?.let { addHistoryViewModel.archiveIncomeGroup(it) }
                if (binding.groupSpinner.text.toString() == spinnerItem.name) {
                    resetSubGroupSpinner()
                    binding.groupSpinner.text = null
                    groupSpinnerValueGlobalBeforeAdd = null
                }
                sharedModViewModel.set(null)

                dismissAndDropdownSpinner(binding.groupSpinner)
            }
        }

    private val archiveSubGroupListener =
        object : GroupSpinnerAdapter.DeleteItemClickListener {

            @RequiresApi(Build.VERSION_CODES.O)
            override fun archive(spinnerItem: SpinnerItem) {
                spinnerItem.id?.let { addHistoryViewModel.archiveIncomeSubGroup(it) }
                if (binding.subGroupSpinner.text.toString() == spinnerItem.name) {
                    binding.subGroupSpinner.text = null
                    subGroupSpinnerValueGlobalBeforeAdd = null
                }
                sharedModViewModel.set(null)

                dismissAndDropdownSpinner(binding.subGroupSpinner)
            }
        }

    private val archiveWalletListener =
        object : GroupSpinnerAdapter.DeleteItemClickListener {

            @RequiresApi(Build.VERSION_CODES.O)
            override fun archive(spinnerItem: SpinnerItem) {
                spinnerItem.id?.let { addHistoryViewModel.archiveWallet(it) }
                if (binding.walletSpinner.text.toString() == spinnerItem.name) {
                    binding.walletSpinner.text = null
                    walletSpinnerValueGlobalBeforeAdd = null
                }
                sharedModViewModel.set(null)

                dismissAndDropdownSpinner(binding.walletSpinner)
            }
        }
}
