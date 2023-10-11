package com.romandevyatov.bestfinance.ui.fragments.add.history

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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
import com.romandevyatov.bestfinance.ui.adapters.spinner.SpinnerAdapter
import com.romandevyatov.bestfinance.ui.adapters.spinner.SpinnerItem
import com.romandevyatov.bestfinance.utils.Constants
import com.romandevyatov.bestfinance.utils.Constants.ADD_INCOME_HISTORY_FRAGMENT
import com.romandevyatov.bestfinance.utils.Constants.ADD_NEW_INCOME_GROUP
import com.romandevyatov.bestfinance.utils.Constants.ADD_NEW_INCOME_SUB_GROUP
import com.romandevyatov.bestfinance.utils.Constants.ADD_NEW_WALLET
import com.romandevyatov.bestfinance.viewmodels.*
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.AddIncomeHistoryViewModel
import com.romandevyatov.bestfinance.viewmodels.shared.SharedModifiedViewModel
import com.romandevyatov.bestfinance.viewmodels.shared.models.AddTransactionForm
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.util.*

@AndroidEntryPoint
class AddIncomeHistoryFragment : Fragment() {

    private var _binding: FragmentAddIncomeHistoryBinding? = null
    private val binding get() = _binding!!

    private val addHistoryViewModel: AddIncomeHistoryViewModel by viewModels()
    private val sharedModViewModel: SharedModifiedViewModel<AddTransactionForm> by activityViewModels()
    private val args: AddIncomeHistoryFragmentArgs by navArgs()

    private val spinnerItemsGlobal: MutableList<SpinnerItem> = mutableListOf()
    private var groupSpinnerValueGlobalBeforeAdd: String? = null
    private var subGroupSpinnerValueGlobalBeforeAdd: String? = null
    private var walletSpinnerValueGlobalBeforeAdd: String? = null

    private var isButtonClickable = true

    private var textToSpeech: TextToSpeech? = null
    private var spokenValue: String? = null

    private lateinit var intentGlob: Intent
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var handler: Handler
    private lateinit var inputType: InputState

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

    fun startVoiceAssistance(currentInputState: InputState = InputState.GROUP, textToSpeak: String) {
        spokenValue = null

        inputType = currentInputState
        speakTextAndRecognize(textToSpeak, false)
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

            addHistoryViewModel.addIncomeHistoryAndUpdateWallet(
                subGroupNameBinding,
                amountBinding.toDouble(),
                commentBinding,
                parsedLocalDateTime,
                walletNameBinding
            )

            sharedModViewModel.set(null)
            navigateToHome()
        }
    }

    private fun navigateToHome() {
        findNavController().navigate(R.id.action_navigation_add_income_to_navigation_home)
    }

    private fun setGroupAndSubGroupSpinnerAdapter() {
        addHistoryViewModel.getAllIncomeGroupNotArchived()?.observe(viewLifecycleOwner) { groups ->
            val spinnerGroupItems = getGroupItemsForSpinner(groups)

            val groupSpinnerAdapter = SpinnerAdapter(
                requireContext(),
                R.layout.item_with_del,
                spinnerGroupItems,
                ADD_NEW_INCOME_GROUP,
                archiveGroupListener)

            binding.groupSpinner.setAdapter(groupSpinnerAdapter)

            setIfAvailableGroupSpinnersValue(groupSpinnerAdapter)

            setSubGroupSpinnerAdapter()
        }
    }

    private fun setSubGroupSpinnerAdapter() {
        val groupSpinnerBinding = binding.groupSpinner.text.toString()

        if (groupSpinnerBinding.isNotBlank()) {
            setSubGroupSpinnerAdapterByGroupName(groupSpinnerBinding)
        } else {
            setEmptySubGroupSpinnerAdapter()
        }
    }

    private fun setSubGroupSpinnerAdapterByGroupName(groupSpinnerBinding: String) {
        addHistoryViewModel.getIncomeGroupNotArchivedWithIncomeSubGroupsNotArchivedByIncomeGroupNameLiveData(
            groupSpinnerBinding
        ).observe(viewLifecycleOwner) { groupWithSubGroups ->
            val spinnerSubItems =
                getSpinnerSubItemsNotArchived(groupWithSubGroups)

            val subGroupSpinnerAdapter =
                SpinnerAdapter(requireContext(), R.layout.item_with_del, spinnerSubItems, ADD_NEW_INCOME_SUB_GROUP, archiveSubGroupListener)

            binding.subGroupSpinner.setAdapter(subGroupSpinnerAdapter)

            setIfAvailableSubGroupSpinnersValue(subGroupSpinnerAdapter)
        }
    }

    private fun setWalletSpinnerAdapter() {
        addHistoryViewModel.walletsNotArchivedLiveData.observe(viewLifecycleOwner) { wallets ->

            val spinnerWalletItems = getWalletItemsForSpinner(wallets)

            val walletSpinnerAdapter =
                SpinnerAdapter(requireContext(), R.layout.item_with_del, spinnerWalletItems, ADD_NEW_WALLET, archiveWalletListener)

            binding.walletSpinner.setAdapter(walletSpinnerAdapter)

            setIfAvailableWalletSpinnerValue(walletSpinnerAdapter)
        }
    }

    private fun setEmptySubGroupSpinnerAdapter() {
        val emptySubGroupSpinnerAdapter = getEmptySubGroupSpinnerAdapter()

        binding.subGroupSpinner.setAdapter(emptySubGroupSpinnerAdapter)
    }

    private fun getEmptySubGroupSpinnerAdapter(): SpinnerAdapter {
        val subGroupSpinnerItems = ArrayList<String>()

        subGroupSpinnerItems.add(ADD_NEW_INCOME_SUB_GROUP)

        return SpinnerAdapter(
            requireContext(),
            R.layout.item_with_del,
            subGroupSpinnerItems,
            ADD_NEW_INCOME_GROUP,
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
                updateSubGroupSpinnerByGroupSpinnerValue(selectedGroupName)
            }
        }
    }

    private fun updateSubGroupSpinnerByGroupSpinnerValue(selectedGroupName: String) {
        // TODO: getIncomeGroupNotArchivedWithIncomeSubGroupsNotArchivedByIncomeGroupNameLiveData doesn't work
        addHistoryViewModel.getIncomeGroupNotArchivedWithIncomeSubGroupsNotArchivedByIncomeGroupNameLiveData(
            selectedGroupName
        ).observe(viewLifecycleOwner) { groupWithSubGroups ->
            val spinnerSubItems = getSpinnerSubItemsNotArchived(groupWithSubGroups)
            val subGroupSpinnerAdapter =
                SpinnerAdapter(requireContext(), R.layout.item_with_del, spinnerSubItems, ADD_NEW_INCOME_SUB_GROUP, archiveSubGroupListener)

            binding.subGroupSpinner.setAdapter(subGroupSpinnerAdapter)
        }
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

    private fun setIfAvailableGroupSpinnersValue(groupSpinnerAdapter: SpinnerAdapter) {
        val savedGroupName = args.incomeGroupName ?: sharedModViewModel.modelForm?.groupSpinnerValue

        if (savedGroupName?.isNotBlank() == true) {
            resetSubGroupSpinner()

            if (isNameInAdapter(groupSpinnerAdapter, savedGroupName)) {
                groupSpinnerValueGlobalBeforeAdd = savedGroupName

                binding.groupSpinner.setText(savedGroupName, false)
            }
        }
    }

    private fun setIfAvailableSubGroupSpinnersValue(subGroupSpinnerAdapter: SpinnerAdapter) {
        val savedSubGroupName = args.incomeSubGroupName ?: sharedModViewModel.modelForm?.subGroupSpinnerValue

        if (savedSubGroupName?.isNotBlank() == true && isNameInAdapter(subGroupSpinnerAdapter, savedSubGroupName)) {
            subGroupSpinnerValueGlobalBeforeAdd = savedSubGroupName

            binding.subGroupSpinner.setText(savedSubGroupName, false)
        }
    }

    private fun setIfAvailableWalletSpinnerValue(walletSpinnerAdapter: SpinnerAdapter) {
        val savedWalletName = args.walletName ?: sharedModViewModel.modelForm?.walletSpinnerValue

        if (savedWalletName?.isNotBlank() == true && isNameInAdapter(walletSpinnerAdapter, savedWalletName)) {
            walletSpinnerValueGlobalBeforeAdd = savedWalletName

            binding.walletSpinner.setText(savedWalletName, false)
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

    private fun getGroupItemsForSpinner(groups: List<IncomeGroup>?): ArrayList<String> {
        val spinnerItems = ArrayList<String>()

        spinnerItemsGlobal.clear()
        groups?.forEach { it ->
            spinnerItems.add(it.name)
            spinnerItemsGlobal.add(SpinnerItem(it.id, it.name))
        }
        spinnerItems.add(ADD_NEW_INCOME_GROUP)
        spinnerItemsGlobal.add(SpinnerItem(-1, ADD_NEW_INCOME_GROUP))

        return spinnerItems
    }

    private fun getSpinnerSubItemsNotArchived(groupWithSubGroups: IncomeGroupWithIncomeSubGroups?): ArrayList<String> {
        val spinnerSubItems = ArrayList<String>()

        groupWithSubGroups?.incomeSubGroups?.forEach {
            if (it.archivedDate == null) {
                spinnerSubItems.add(it.name)
            }
        }

        spinnerSubItems.add(ADD_NEW_INCOME_SUB_GROUP)

        return spinnerSubItems
    }

    private fun getWalletItemsForSpinner(walletList: List<Wallet>?): ArrayList<String> {
        val spinnerItems = ArrayList<String>()

        walletList?.forEach { it ->
            spinnerItems.add(it.name)
        }
        spinnerItems.add(ADD_NEW_WALLET)

        return spinnerItems
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

    enum class InputState {
        GROUP, SUB_GROUP, WALLET, AMOUNT, COMMENT, SET_BALANCE, CONFIRM
    }

    private var isTextToSpeechDone = true

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

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) { }
                })
            }
        }
    }

    private fun setUpSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())

        val recognitionListener = getSpeechRecognitionListener()
        speechRecognizer.setRecognitionListener(recognitionListener)
    }

    private fun getSpeechRecognitionListener(): RecognitionListener {
        return object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                // Called when the speech recognizer is ready for speech input
            }

            override fun onBeginningOfSpeech() {
            }

            override fun onRmsChanged(rmsdB: Float) {
                // Called when the RMS dB (sound level) changes during speech input
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                // Called when the audio buffer is received
            }

            override fun onEndOfSpeech() {
                // Called when the user stops speaking
            }

            override fun onError(error: Int) {
                // Called if there is an error during speech recognition
                // Handle errors appropriately (e.g., display an error message)
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResults(results: Bundle?) {
                val recognizedStrings = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (recognizedStrings != null && recognizedStrings.isNotEmpty()) {
                    val currentSpokenText = recognizedStrings[0]
                    val handledSpokenValue = handleRecognizedText(currentSpokenText)

                    when (inputType) {
                        InputState.GROUP -> handleGroupInput(handledSpokenValue)
                        InputState.SUB_GROUP -> handleSubGroupInput(handledSpokenValue)
                        InputState.WALLET -> handleWalletInput(handledSpokenValue)
                        InputState.SET_BALANCE -> handleWalletBalanceInput(handledSpokenValue)
                        InputState.AMOUNT -> handleAmountInput(handledSpokenValue)
                        InputState.COMMENT -> handleCommentInput(handledSpokenValue)
                        InputState.CONFIRM -> handleConfirmInput(handledSpokenValue)
                    }
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                // Called when partial recognition results are available
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                // Called for various speech recognition events
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleConfirmInput(sentSpokenValue: String) {
        when (sentSpokenValue.lowercase()) {
            "yes" -> { // sent
                sendIncomeHistory()
            }
            "no" -> { // no
                speakText("Terminated")
            }
            else -> speakText("You sad $sentSpokenValue. Exiting")
        }
        spokenValue = null
        inputType = InputState.GROUP
    }

    private fun handleGroupInput(currentSpokenText: String) { // income group name
        if (spokenValue == null) {
            val groupList = getAllItemsFromAutoCompleteTextView(binding.groupSpinner)

            if (groupList.contains(currentSpokenText)) { // success
                binding.groupSpinner.setText(currentSpokenText, false)
                speakText("Group is set")
                updateSubGroupSpinnerByGroupSpinnerValue(currentSpokenText)
                startVoiceAssistance(InputState.SUB_GROUP, "Set subgroup")
            } else {
                spokenValue = currentSpokenText

                val ask = "Group name '$currentSpokenText' doesn't exist. Do you want to create a new group with this name? (Yes/No)"
                speakTextAndRecognize(ask, false)
            }
        } else if (!spokenValue.equals("-1")) {
            when (currentSpokenText.lowercase()) {
                "yes" -> { // create new
                    handleCreateNewGroup()
                    speakText("Group is set")
                    startVoiceAssistance(InputState.SUB_GROUP, "Set subgroup")
                }
                "no" -> { // then ask exit or start again?
                    spokenValue = "-1" // any
                    val ask = "Do you want to continue and call group name one more time? (Yes/No)"
                    speakTextAndRecognize(ask, false)
                }
                else -> speakText("You sad $currentSpokenText. Exiting")
            }
        } else if (spokenValue.equals("-1")) {
            when (currentSpokenText.lowercase()) {
                "yes" -> { // start again
                    startVoiceAssistance(InputState.GROUP, "Set group")
                }
                "no" -> { // exit
                    speakText("Terminated")
                }
                else -> speakText("You sad $currentSpokenText. Exiting")
            }

            spokenValue = null
        }
    }

    private fun handleCreateNewGroup() {
        speakText("Adding $spokenValue group")
        addHistoryViewModel.insertIncomeGroup(
            IncomeGroup(
                name = spokenValue!!.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
                isPassive = false
            )
        )
        binding.groupSpinner.setText(spokenValue, false)
        speakText("Created group is set")
        updateSubGroupSpinnerByGroupSpinnerValue(spokenValue!!)

        spokenValue = null
    }

    private fun handleSubGroupInput(currentSpokenText: String) { // income sub group
        if (spokenValue == null) {
            val subGroupList = getAllItemsFromAutoCompleteTextView(binding.subGroupSpinner)

            if (subGroupList.contains(currentSpokenText)) { // success
                binding.subGroupSpinner.setText(currentSpokenText, false)
                speakText("Subgroup is set")
                startVoiceAssistance(InputState.WALLET, "Set wallet") // move further
            } else {
                spokenValue = currentSpokenText

                val ask = "Subgroup name '$currentSpokenText' doesn't exist. Do you want to create a new subgroup with this name? (Yes/No)"
                speakTextAndRecognize(ask, false)
            }
        } else if (!spokenValue.equals("-1")) {
            when (currentSpokenText.lowercase()) {
                "yes" -> {
                    handleCreateNewSubGroup()
                    startVoiceAssistance(InputState.WALLET, "Set wallet") // move further
                }
                "no" -> { // then ask exit or start again?
                    spokenValue = "-1" // any
                    val ask = "Do you want to continue and call subgroup one more time? (Yes/No)"
                    speakTextAndRecognize(ask, false)
                }
                else -> speakText("You sad $currentSpokenText. Exiting")
            }
        } else if (spokenValue.equals("-1")) {
            when (currentSpokenText.lowercase()) {
                "yes" -> { // start again
                    startVoiceAssistance(InputState.SUB_GROUP, "Set subgroup")
                }
                "no" -> { // exit
                    speakText("Terminated")
                }
                else -> {
                    speakText("You sad $currentSpokenText. Exiting")
                }
            }

            spokenValue = null
        }
    }

    private fun handleCreateNewSubGroup() { // create new subgroup
        speakText("Adding $spokenValue subgroup")

        val groupId = spinnerItemsGlobal.find { it.name == binding.groupSpinner.text.toString() }?.id!!

        val newIncomeSubGroup = IncomeSubGroup(
            name = spokenValue!!,
            incomeGroupId = groupId
        )
        addHistoryViewModel.insertIncomeSubGroup(
            newIncomeSubGroup
        )
        binding.subGroupSpinner.setText(spokenValue, false)
        speakText("Created subgroup is set")

        spokenValue = null
    }

    private fun handleWalletInput(currentSpokenText: String) { // wallet
        if (spokenValue == null) {
            val wallets = getAllItemsFromAutoCompleteTextView(binding.walletSpinner)

            if (wallets.contains(currentSpokenText)) { // success
                binding.walletSpinner.setText(currentSpokenText, false)
                startVoiceAssistance(InputState.AMOUNT, "Set amount") // move further
            } else {
                spokenValue = currentSpokenText

                val ask = "Wallet '$currentSpokenText' doesn't exist. Do you want to create a new wallet with this name? (Yes/No)"
                speakTextAndRecognize(ask, false)
            }
        } else if (!spokenValue.equals("-1")) {
            when (currentSpokenText.lowercase()) {
                "yes" -> {
                    inputType = InputState.SET_BALANCE
                    val ask = "Adding $spokenValue wallet, set wallet balance"
                    speakTextAndRecognize(ask, false) // move further
                }
                "no" -> { // then ask exit or start again?
                    spokenValue = "-1" // any
                    val ask = "Do you want to continue and call wallet name one more time? (Yes/No)"
                    speakTextAndRecognize(ask, false)
                }
                else -> speakText("You sad $currentSpokenText. Exiting")
            }
        } else if (spokenValue.equals("-1")) {
            when (currentSpokenText.lowercase()) {
                "yes" -> startVoiceAssistance(InputState.WALLET, "Set wallet") // start again
                "no" -> { // exit
                    speakText("Terminated")
                }
                else -> speakText("You sad $currentSpokenText. Exiting")
            }
            spokenValue = null
        }
    }

    private fun handleWalletBalanceInput(spokenBalanceText: String) {
        val numbers = spokenBalanceText.replace(",", "")
            .split(" ")
            .filter { it.matches(Regex("-?\\d+(\\.\\d+)?")) }
            .joinToString("")

        // Display extracted numbers
        if (numbers.isNotEmpty()) {
            val newWallet = Wallet(
                name = spokenValue!!,
                balance = numbers.toDouble()
            )
            addHistoryViewModel.insertWallet(newWallet)

            binding.walletSpinner.setText(spokenValue, false)

            spokenValue = null

            startVoiceAssistance(InputState.AMOUNT, "Set amount")
        } else if (!spokenValue.equals("-1")) {
            spokenValue = "-1"
            val ask = "Incorrect wallet balance. Do you want to continue and call wallet balance one more time? (Yes/No)"
            speakTextAndRecognize(ask , false)
        } else if (spokenValue.equals("-1")) {
            when (spokenBalanceText.lowercase()) {
                "yes" -> { // start again setting balance
                    val ask = "Set wallet balance"
                    speakTextAndRecognize(ask , false)
                }
                "no" -> { // exit
                    speakText("Terminated")
                }
                else -> speakText("You sad $spokenBalanceText. Exiting")
            }
            spokenValue = null
        }
    }

    private fun handleAmountInput(spokenAmountText: String) { // amount
        if (spokenValue == null) {
            val numbers = spokenAmountText.replace(",", "")
                .split(" ")
                .filter { it.matches(Regex("-?\\d+(\\.\\d+)?")) }
                .joinToString("")

            // Display extracted numbers
            if (numbers.isNotEmpty()) {
                binding.amountEditText.setText(numbers)
                startVoiceAssistance(InputState.COMMENT, "Set comment")
            } else {
                spokenValue = spokenAmountText

                val ask = "Incorrect number. Do you want to continue and call amount one more time? (Yes/No)"
                speakTextAndRecognize(ask , false)
            }
        } else {
            when (spokenAmountText.lowercase()) {
                "yes" -> { // start again
                    startVoiceAssistance(InputState.AMOUNT, "Set amount") // move further
                }
                "no" -> { // exit
                    spokenValue = null

                    speakText("Terminated")
                }
                else -> speakText("You sad $spokenAmountText. Exiting")
            }
        }
    }

    private fun handleCommentInput(spokenComment: String) { // comment
        if (spokenComment.isNotEmpty()) {
            binding.commentEditText.setText(spokenComment)
            speakText("Comment is set")
        }
        startVoiceAssistance(InputState.CONFIRM, "Confirm transaction (Yes/No)")
    }

    private fun handleRecognizedText(recognizedText: String): String {
        return recognizedText.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        }
    }

    private fun getAllItemsFromAutoCompleteTextView(autoCompleteTextView: AutoCompleteTextView): List<String> {
        val adapter = autoCompleteTextView.adapter
        val allItems = mutableListOf<String>()

        if (adapter is ArrayAdapter<*>) {
            for (i in 0 until adapter.count) {
                allItems.add(adapter.getItem(i).toString())
            }
        }

        return allItems
    }

    private val archiveGroupListener =
        object : SpinnerAdapter.DeleteItemClickListener {

            @RequiresApi(Build.VERSION_CODES.O)
            override fun archive(name: String) {
                addHistoryViewModel.archiveIncomeGroup(name)
                if (binding.groupSpinner.text.toString() == name) {
                    resetSubGroupSpinner()
                    binding.groupSpinner.text = null
                    groupSpinnerValueGlobalBeforeAdd = null
                }
                sharedModViewModel.set(null)

                dismissAndDropdownSpinner(binding.groupSpinner)
            }
        }

    private val archiveSubGroupListener =
        object : SpinnerAdapter.DeleteItemClickListener {

            @RequiresApi(Build.VERSION_CODES.O)
            override fun archive(name: String) {
                addHistoryViewModel.archiveIncomeSubGroup(name)
                if (binding.subGroupSpinner.text.toString() == name) {
                    binding.subGroupSpinner.text = null
                    subGroupSpinnerValueGlobalBeforeAdd = null
                }
                sharedModViewModel.set(null)

                dismissAndDropdownSpinner(binding.subGroupSpinner)
            }
        }

    private val archiveWalletListener =
        object : SpinnerAdapter.DeleteItemClickListener {

            @RequiresApi(Build.VERSION_CODES.O)
            override fun archive(name: String) {
                addHistoryViewModel.archiveWallet(name)
                if (binding.walletSpinner.text.toString() == name) {
                    binding.walletSpinner.text = null
                    walletSpinnerValueGlobalBeforeAdd = null
                }
                sharedModViewModel.set(null)

                dismissAndDropdownSpinner(binding.walletSpinner)
            }
        }
}
