package com.romandevyatov.bestfinance.ui.fragments.update.wallet

import com.romandevyatov.bestfinance.utils.numberpad.addGenericTextWatcher
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.data.entities.ExpenseHistory
import com.romandevyatov.bestfinance.data.entities.IncomeHistory
import com.romandevyatov.bestfinance.data.entities.Wallet
import com.romandevyatov.bestfinance.databinding.DialogAlertBinding
import com.romandevyatov.bestfinance.databinding.FragmentUpdateWalletBinding
import com.romandevyatov.bestfinance.utils.BackStackLogger
import com.romandevyatov.bestfinance.utils.Constants
import com.romandevyatov.bestfinance.utils.TextFormatter.removeTrailingZeros
import com.romandevyatov.bestfinance.utils.WindowUtil
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.UpdateWalletViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import kotlin.math.absoluteValue

@AndroidEntryPoint
class UpdateWalletFragment : Fragment() {

    private var _binding: FragmentUpdateWalletBinding? = null

    private val binding get() = _binding!!

    private val updateWalletViewModel: UpdateWalletViewModel by viewModels()

    private val args: UpdateWalletFragmentArgs by navArgs()

    private var walletId: Long? = null

    private var walletGlobal: Wallet? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateWalletBinding.inflate(inflater, container, false)

        updateWalletViewModel.getWalletByNameLiveData(args.walletName.toString())
            .observe(viewLifecycleOwner) { wallet ->
                wallet?.let {
                    walletId = it.id
                    walletGlobal = it.copy()

                    binding.nameEditText.setText(it.name)
                    binding.balanceEditText.setText(removeTrailingZeros(it.balance.toString()))
                    binding.descriptionEditText.setText(it.description)
                }
            }

        BackStackLogger.logBackStack(findNavController())

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.balanceEditText.addGenericTextWatcher()

        binding.updateWalletButton.setOnClickListener {
            val walletNameBinding = binding.nameEditText.text.toString().trim()
            val walletBalanceBinding = binding.balanceEditText.text.toString().trim().toDouble()
            val walletDescriptionBinding = binding.descriptionEditText.text.toString().trim()

            updateWalletViewModel.getWalletByNameLiveData(walletNameBinding)
                .observe(viewLifecycleOwner) { wallet ->
                    if (walletNameBinding == args.walletName.toString() || wallet == null) {
                        walletGlobal?.let { walletGlobal ->
                            walletId?.let { id ->
                                val difference = walletBalanceBinding - walletGlobal.balance
                                if (difference != 0.0) {
                                    showChangingBalanceDialog(
                                        requireContext(),
                                        id,
                                        difference,
                                        getString(R.string.balance_is_changed_do_you_want_to_add_history_record)
                                    )
                                }
                            }
                        }

                        val updatedWallet = Wallet(
                            id = walletId,
                            name = walletNameBinding,
                            balance = walletBalanceBinding,
                            description = walletDescriptionBinding,
                            currencyCode = walletGlobal?.currencyCode ?: "USD"
                        )

                        updateWalletViewModel.updateNameAndDescriptionAndBalanceWalletById(updatedWallet)

                        performBackNavigation()
                    } else if (wallet.archivedDate == null) {
                        WindowUtil.showExistingDialog(
                            requireContext(),
                            getString(R.string.wallet_name_already_exists, walletNameBinding)
                        )
                    } else {
                        WindowUtil.showUnarchiveDialog(
                            requireContext(),
                            getString(R.string.wallet_name_is_archived_do_you_want_to_unarchive_and_proceed_updating, walletNameBinding, walletNameBinding)
                        ) {
                            updateWalletViewModel.unarchiveWallet(wallet)
                            performBackNavigation()
                        }
                    }
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showChangingBalanceDialog(
        context: Context,
        walletId: Long,
        difference: Double,
        message: String?
    ) {
        val binding = DialogAlertBinding.inflate(LayoutInflater.from(context))
        val dialog = Dialog(context)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.tvMessage.text = message

        binding.btnYes.setOnClickListener {
            dialog.dismiss()
            val incomeOrExpenseType = difference.compareTo(0.0)
            if (incomeOrExpenseType == 1) {
                updateWalletViewModel.addOnlyWalletIncomeHistoryRecord(
                    IncomeHistory(
                        incomeSubGroupId = null,
                        amount = difference,
                        comment = getString(R.string.changed_wallet_balance),
                        date = LocalDateTime.now(),
                        walletId = walletId,
                        amountBase = difference
                    )
                )
            } else if (incomeOrExpenseType == -1) {
                updateWalletViewModel.addOnlyWalletExpenseHistoryRecord(
                    ExpenseHistory(
                        expenseSubGroupId = null,
                        amount = difference.absoluteValue,
                        comment = getString(R.string.changed_wallet_balance),
                        date = LocalDateTime.now(),
                        walletId = walletId,
                        amountBase = difference.absoluteValue
                    )
                )
            }
        }

        binding.btnNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun performBackNavigation() {
        when (args.source.toString()) {
            Constants.MENU_WALLET_FRAGMENT -> {
                findNavController().popBackStack(R.id.wallet_fragment, false)
            }
            Constants.WALLETS_SETTINGS_FRAGMENT -> {
                findNavController().popBackStack(R.id.wallets_settings_fragment, false)
            }
        }
    }

    fun deleteRecord() {
        walletId?.let {
            WindowUtil.showDeleteDialog(
                context = requireContext(),
                viewModel = updateWalletViewModel,
                message = getString(R.string.delete_confirmation_warning_message, walletGlobal?.name),
                isCountdown = true,
                itemId = it,
                rootView = binding.root
            ) { performBackNavigation() }
        }
    }

//    val navController = findNavController()
//    if (navController.popBackStack()) {
//        val previousBackStackEntry = navController.previousBackStackEntry
//        // Now you can access information about the previous destination
//        val previousDestinationId = previousBackStackEntry?.destination?.id
//        // Do something with the previous destination ID
//        if (previousDestinationId != null) {
//            navController.navigate(previousDestinationId)
//        }
//    }
}
