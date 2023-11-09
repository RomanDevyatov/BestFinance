package com.romandevyatov.bestfinance.ui.fragments.add.wallet

import com.romandevyatov.bestfinance.utils.numberpad.addGenericTextWatcher
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.data.entities.Wallet
import com.romandevyatov.bestfinance.data.validation.EmptyValidator
import com.romandevyatov.bestfinance.data.validation.IsDigitValidator
import com.romandevyatov.bestfinance.data.validation.TwoDigitsAfterPoint
import com.romandevyatov.bestfinance.data.validation.base.BaseValidator
import com.romandevyatov.bestfinance.databinding.FragmentAddWalletBinding
import com.romandevyatov.bestfinance.utils.BackStackLogger
import com.romandevyatov.bestfinance.utils.Constants
import com.romandevyatov.bestfinance.utils.Constants.ADD_EXPENSE_HISTORY_FRAGMENT
import com.romandevyatov.bestfinance.utils.Constants.ADD_INCOME_HISTORY_FRAGMENT
import com.romandevyatov.bestfinance.utils.Constants.ADD_TRANSFER_HISTORY_FRAGMENT
import com.romandevyatov.bestfinance.utils.Constants.UNCALLABLE_WORD
import com.romandevyatov.bestfinance.utils.Constants.WALLETS_FRAGMENT
import com.romandevyatov.bestfinance.utils.WindowUtil
import com.romandevyatov.bestfinance.utils.voiceassistance.InputState
import com.romandevyatov.bestfinance.utils.voiceassistance.NumberConverter
import com.romandevyatov.bestfinance.utils.voiceassistance.base.VoiceAssistanceBaseFragment
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.WalletViewModel
import com.romandevyatov.bestfinance.viewmodels.shared.SharedModifiedAddWalletFormViewModel
import com.romandevyatov.bestfinance.viewmodels.shared.SharedModifiedViewModel
import com.romandevyatov.bestfinance.viewmodels.shared.models.AddTransactionForm
import com.romandevyatov.bestfinance.viewmodels.shared.models.AddTransferForm
import com.romandevyatov.bestfinance.viewmodels.shared.models.AddWalletForm
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddWalletFragment : VoiceAssistanceBaseFragment() {

    private var _binding: FragmentAddWalletBinding? = null
    private val binding get() = _binding!!

    private val walletViewModel: WalletViewModel by viewModels()

    private val sharedModifiedAddWalletFormViewModel: SharedModifiedAddWalletFormViewModel by activityViewModels()
    private val sharedModifiedAddTransactionViewModel: SharedModifiedViewModel<AddTransactionForm> by activityViewModels()
    private val sharedModifiedAddTransferViewModel: SharedModifiedViewModel<AddTransferForm> by activityViewModels()

    private val args: AddWalletFragmentArgs by navArgs()

    private var isButtonClickable = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddWalletBinding.inflate(inflater, container, false)

        setUpSpeechRecognizer()

        setUpTextToSpeech()

        BackStackLogger.logBackStack(findNavController())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        restoreAmountDateCommentValues()

        binding.balanceEditText.addGenericTextWatcher()

        binding.currencyEditText.setOnClickListener {
            saveAddWalletForm()

            val action = AddWalletFragmentDirections.actionNavigationAddWalletToNavigationSelectCurrency()
            action.source = Constants.ADD_WALLET_FRAGMENT
            findNavController().navigate(action)
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                sharedModifiedAddWalletFormViewModel.set(null)
                performNavigation(args.source, null)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        binding.addButton.setOnClickListener {
            if (!isButtonClickable) return@setOnClickListener
            isButtonClickable = false
            view.isEnabled = false

            sendWallet()

            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                isButtonClickable = true
                view.isEnabled = true
            }, Constants.CLICK_DELAY_MS)
        }
    }

    private fun saveAddWalletForm() {
        val nameBinding = binding.nameEditText.text.toString().trim()
        val balanceBinding = binding.balanceEditText.text.toString().trim()
        val currencyBinding = binding.currencyEditText.text.toString().trim()
        val descriptionBinding = binding.descriptionEditText.text.toString().trim()

        val addWalletForm = AddWalletForm(
            name = nameBinding,
            balance = balanceBinding,
            currencyCode = currencyBinding,
            description = descriptionBinding
        )
        sharedModifiedAddWalletFormViewModel.set(addWalletForm)
    }

    private fun restoreAmountDateCommentValues() {
        val modifiedAddWalletForm = sharedModifiedAddWalletFormViewModel.modelForm

        if (modifiedAddWalletForm?.balance != null) {
            binding.balanceEditText.setText(modifiedAddWalletForm.balance)
        }

        if (modifiedAddWalletForm?.name != null) {
            binding.nameEditText.setText(modifiedAddWalletForm.name)
        }

        if (modifiedAddWalletForm?.description != null) {
            binding.descriptionEditText.setText(modifiedAddWalletForm.description)
        }
        val currencyCode = modifiedAddWalletForm?.currencyCode ?: walletViewModel.currentDefaultCurrencyCode
        binding.currencyEditText.text = currencyCode
    }

    private fun sendWallet() {
        val walletNameBinding = binding.nameEditText.text.toString().trim()
        val walletBalanceBinding = binding.balanceEditText.text.toString().trim()
        val walletDescriptionBinding = binding.descriptionEditText.text.toString().trim()
        val currencyCodeBinding = binding.currencyEditText.text.toString().trim()

        val walletNameValidation = EmptyValidator(walletNameBinding).validate()
        binding.nameLayout.error = if (!walletNameValidation.isSuccess) getString(walletNameValidation.message) else null

        val walletBalanceValidation = BaseValidator.validate(EmptyValidator(walletBalanceBinding), IsDigitValidator(walletBalanceBinding), TwoDigitsAfterPoint(walletBalanceBinding))
        binding.balanceLayout.error = if (!walletBalanceValidation.isSuccess) getString(walletBalanceValidation.message) else null

        if (walletNameValidation.isSuccess
            && walletBalanceValidation.isSuccess
        ) {
            walletViewModel.getWalletByNameLiveData(walletNameBinding)
                .observe(viewLifecycleOwner) { wallet ->
                    if (wallet == null) {
                        val newWallet = Wallet(
                            name = walletNameBinding,
                            balance = walletBalanceBinding.toDouble(),
                            description = walletDescriptionBinding,
                            currencyCode = currencyCodeBinding
                        )

                        sharedModifiedAddWalletFormViewModel.set(null)
                        walletViewModel.insertWallet(newWallet)
                        performNavigation(args.source, walletNameBinding)
                    } else if (wallet.archivedDate == null) {
                        WindowUtil.showExistingDialog(
                            requireContext(),
                            getString(R.string.wallet_is_already_existing_set_another_wallet_name, walletNameBinding)
                        )
                    } else {
                        WindowUtil.showUnarchiveDialog(
                            requireContext(),
                            getString(R.string.unarchive_wallet, wallet.name, wallet.name)
                        ) {
                            sharedModifiedAddWalletFormViewModel.set(null)
                            walletViewModel.unarchiveWallet(wallet)
                            performNavigation(args.source, wallet.name)
                        }
                    }
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun calculateSteps(): MutableList<InputState> {
        val steps: MutableList<InputState> = mutableListOf()

        if (binding.nameEditText.text.toString().isEmpty()) {
            steps.add(InputState.SET_NAME)
        }

        if (binding.balanceEditText.text.toString().isEmpty()) {
            steps.add(InputState.SET_WALLET_BALANCE)
        }

        if (binding.descriptionEditText.text.toString().isEmpty()) {
            steps.add(InputState.DESCRIPTION)
        }

        steps.add(InputState.CONFIRM)

        return steps
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun handleUserInput(handledSpokenValue: String, currentStage: InputState) {
        when (currentStage) {
            InputState.SET_NAME -> handleNameInput(handledSpokenValue)
            InputState.SET_WALLET_BALANCE -> handleWalletBalanceInput(handledSpokenValue)
            InputState.DESCRIPTION -> handleDescriptionInput(handledSpokenValue)
            InputState.CONFIRM -> handleConfirmInput(handledSpokenValue)
            else -> {}
        }
    }

    private fun handleNameInput(handledSpokenValue: String) {
        if (spokenValue == null) {
            walletViewModel.getWalletByNameLiveData(handledSpokenValue).observe(viewLifecycleOwner) { wallet ->
                wallet?.let {
                    val ask = getString(R.string.wallet_is_already_existing_set_another_wallet_name, handledSpokenValue)
                    startVoiceAssistance(ask)
                } ?: run {
                    binding.nameEditText.setText(handledSpokenValue)
                    nextStage()
                }
            }
        }
    }

    private fun handleWalletBalanceInput(handledSpokenValue: String) {
        if (spokenValue == null) {
            val textNumbers = handledSpokenValue.replace(",", "")

            val convertedNumber = NumberConverter.convertSpokenTextToNumber(textNumbers)

            if (convertedNumber != null) {
                binding.balanceEditText.setText(convertedNumber.toString())
                nextStage()
            } else {
                spokenValue = UNCALLABLE_WORD

                val askSpeechText = getString(R.string.incorrect_balance)
                speakTextAndRecognize(askSpeechText, false)
            }
        } else if (spokenValue.equals(UNCALLABLE_WORD)) {
            when (handledSpokenValue.lowercase()) {
                getString(R.string.yes) -> {
                    startVoiceAssistance()
                }
                getString(R.string.no) -> {
                    speakText(getString(R.string.exit))
                }
                else -> speakText(getString(R.string.you_said, handledSpokenValue))
            }
        }
    }

    private fun handleDescriptionInput(handledSpokenValue: String) {
        val speakText = if (handledSpokenValue.isNotEmpty()) {
            binding.descriptionEditText.setText(handledSpokenValue)
            getString(R.string.description_is_set)
        } else {
            getString(R.string.description_is_empty)
        }

        nextStage(speakTextBefore = speakText)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleConfirmInput(handledSpokenValue: String) {
        when (handledSpokenValue.lowercase()) {
            getString(R.string.yes) -> {
                sendWallet()
                speakText(getString(R.string.wallet_added, binding.nameEditText.text))
            }
            getString(R.string.no) -> {
                speakText(getString(R.string.exit))
            }
            else -> speakText(getString(R.string.you_said, handledSpokenValue))
        }
        spokenValue = null
    }

    private fun performNavigation(prevFragmentString: String?, walletName: String?) {
        when (prevFragmentString) {
            ADD_INCOME_HISTORY_FRAGMENT -> {
                saveWalletName(walletName)
                findNavController().popBackStack(R.id.add_income_fragment, false)
            }
            ADD_EXPENSE_HISTORY_FRAGMENT -> {
                saveWalletName(walletName)
                findNavController().popBackStack(R.id.add_expense_fragment, false)
            }
            ADD_TRANSFER_HISTORY_FRAGMENT -> {
                saveWalletNameForTransferForm(walletName)
                findNavController().popBackStack(R.id.add_transfer_fragment, false)
            }
            WALLETS_FRAGMENT -> {
                sharedModifiedAddWalletFormViewModel.set(null)
                findNavController().popBackStack(R.id.wallet_fragment, false)
            }
            else -> {
                findNavController().navigateUp()
            }
        }
    }

    private fun saveWalletName(name: String?) {
        if (name != null) {
            val updatedMod = sharedModifiedAddTransactionViewModel.modelForm?.copy(
                walletSpinnerValue = name
            )
            sharedModifiedAddTransactionViewModel.modelForm = updatedMod
        }
    }

    private fun saveWalletNameForTransferForm(name: String?) {
        if (name != null) {
            when(args.spinnerType) {
                Constants.SPINNER_FROM -> {
                    val updatedFromWalletMod = sharedModifiedAddTransferViewModel.modelForm?.copy(
                        fromWalletSpinnerValue = name
                    )
                    sharedModifiedAddTransferViewModel.modelForm = updatedFromWalletMod
                }
                Constants.SPINNER_TO -> {
                    val updatedToWalletMod = sharedModifiedAddTransferViewModel.modelForm?.copy(
                        toWalletSpinnerValue = name
                    )
                    sharedModifiedAddTransferViewModel.modelForm = updatedToWalletMod
                }
            }

        }
    }



}
