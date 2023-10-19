package com.romandevyatov.bestfinance.ui.fragments.add.wallet

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.FragmentAddWalletBinding
import com.romandevyatov.bestfinance.data.entities.Wallet
import com.romandevyatov.bestfinance.data.validation.EmptyValidator
import com.romandevyatov.bestfinance.data.validation.IsDigitValidator
import com.romandevyatov.bestfinance.data.validation.base.BaseValidator
import com.romandevyatov.bestfinance.databinding.DialogAlertBinding
import com.romandevyatov.bestfinance.utils.Constants
import com.romandevyatov.bestfinance.utils.Constants.ADD_EXPENSE_HISTORY_FRAGMENT
import com.romandevyatov.bestfinance.utils.Constants.ADD_INCOME_HISTORY_FRAGMENT
import com.romandevyatov.bestfinance.utils.Constants.ADD_TRANSFER_HISTORY_FRAGMENT
import com.romandevyatov.bestfinance.utils.Constants.WALLETS_FRAGMENT
import com.romandevyatov.bestfinance.utils.WindowUtil
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.WalletViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddWalletFragment : Fragment() {

    private var _binding: FragmentAddWalletBinding? = null
    private val binding get() = _binding!!

    private val walletViewModel: WalletViewModel by viewModels()

    private val args: AddWalletFragmentArgs by navArgs()

    private var isButtonClickable = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddWalletBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                performNavigation(args.source, null)
            }
        })

        binding.addButton.setOnClickListener {
            if (!isButtonClickable) return@setOnClickListener
            isButtonClickable = false
            view.isEnabled = false

            val walletNameBinding = binding.nameEditText.text.toString().trim()
            val walletBalanceBinding = binding.balanceEditText.text.toString().trim()
            val walletDescriptionBinding = binding.descriptionEditText.text.toString().trim()

            val walletNameValidation = EmptyValidator(walletNameBinding).validate()
            binding.nameLayout.error = if (!walletNameValidation.isSuccess) getString(walletNameValidation.message) else null

            val walletBalanceValidation = BaseValidator.validate(EmptyValidator(walletBalanceBinding), IsDigitValidator(walletBalanceBinding))
            binding.balanceLayout.error = if (!walletBalanceValidation.isSuccess) getString(walletBalanceValidation.message) else null

            if (walletNameValidation.isSuccess
                && walletBalanceValidation.isSuccess
            ) {
                walletViewModel.getWalletByNameLiveData(walletNameBinding)?.observe(viewLifecycleOwner) { wallet ->
                    if (wallet == null) {
                        val newWallet = Wallet(
                            name = walletNameBinding,
                            balance = walletBalanceBinding.toDouble(),
                            description = walletDescriptionBinding
                        )

                        walletViewModel.insertWallet(newWallet)
                        performNavigation(args.source, walletNameBinding)
                    } else if (wallet.archivedDate == null) {
                        WindowUtil.showExistingDialog(
                            requireContext(),
                            getString(R.string.wallet_is_already_existing, walletNameBinding)
                        )
                    } else {
                        showUnarchiveDialog(
                            requireContext(),
                            wallet,
                            getString(R.string.unarchive_wallet, wallet.name, wallet.name)
                        )
                    }
                }
            }

            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                isButtonClickable = true
                view.isEnabled = true
            }, Constants.CLICK_DELAY_MS.toLong())
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun performNavigation(prevFragmentString: String?, walletName: String?) {
        when (prevFragmentString) {
            ADD_INCOME_HISTORY_FRAGMENT -> {
                val action =
                    AddWalletFragmentDirections.actionNavigationAddWalletToNavigationAddIncome()
                action.walletName = walletName
                action.incomeGroupName = null
                action.incomeSubGroupName = null
                findNavController().navigate(action)
            }
            ADD_EXPENSE_HISTORY_FRAGMENT -> {
                val action =
                    AddWalletFragmentDirections.actionNavigationAddWalletToNavigationAddExpense()
                action.walletName = walletName
                action.expenseGroupName = null
                action.expenseSubGroupName = null
                findNavController().navigate(action)
            }
            ADD_TRANSFER_HISTORY_FRAGMENT -> {
                val action = AddWalletFragmentDirections.actionNavigationAddWalletToNavigationAddTransfer()
                action.walletName = walletName
                action.spinnerType = args.spinnerType
                findNavController().navigate(action)
            }
            WALLETS_FRAGMENT -> {
                val action = AddWalletFragmentDirections.actionNavigationAddWalletToNavigationWallet()
                findNavController().navigate(action)
            }
        }
    }

    private fun showUnarchiveDialog(context: Context, wallet: Wallet, message: String?) {
        val binding = DialogAlertBinding.inflate(LayoutInflater.from(context))
        val dialog = Dialog(context)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.tvMessage.text = message

        binding.btnYes.setOnClickListener {
            dialog.dismiss()
            walletViewModel.unarchiveWallet(wallet)
            performNavigation(args.source, wallet.name)
        }

        binding.btnNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

}
