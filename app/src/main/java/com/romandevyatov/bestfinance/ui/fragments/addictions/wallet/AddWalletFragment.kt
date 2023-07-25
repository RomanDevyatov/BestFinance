package com.romandevyatov.bestfinance.ui.fragments.addictions.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.romandevyatov.bestfinance.databinding.FragmentAddWalletBinding
import com.romandevyatov.bestfinance.db.entities.Wallet
import com.romandevyatov.bestfinance.ui.validators.EmptyValidator
import com.romandevyatov.bestfinance.ui.validators.IsDigitValidator
import com.romandevyatov.bestfinance.ui.validators.PositiveValidator
import com.romandevyatov.bestfinance.ui.validators.base.BaseValidator
import com.romandevyatov.bestfinance.utils.Constants
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.WalletViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddWalletFragment : Fragment() {

    private var _binding: FragmentAddWalletBinding? = null
    private val binding get() = _binding!!

    private val walletViewModel: WalletViewModel by viewModels()

    private val args: AddWalletFragmentArgs by navArgs()

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

        binding.addWalletButton.setOnClickListener {
            val walletNameBinding = binding.walletNameEditText.text.toString().trim()
            val walletBalanceBinding = binding.balanceEditText.text.toString().trim()
            val walletDescriptionBinding = binding.walletDescriptionEditText.text.toString().trim()

            val walletNameValidation = EmptyValidator(walletNameBinding).validate()
            binding.walletNameLayout.error = if (!walletNameValidation.isSuccess) getString(walletNameValidation.message) else null

            val walletBalanceValidation = BaseValidator.validate(EmptyValidator(walletBalanceBinding), IsDigitValidator(walletBalanceBinding))
            binding.walletBalanceLayout.error = if (!walletBalanceValidation.isSuccess) getString(walletBalanceValidation.message) else null

            if (walletNameValidation.isSuccess
                && walletBalanceValidation.isSuccess
            ) {
                walletViewModel.getWalletByNameLiveData(walletNameBinding).observe(viewLifecycleOwner) { wallet ->
                    if (wallet != null) {
                        if (wallet.archivedDate != null) {
                            val flag = true // Wallet with this name is archived, do you want to unarchive it?
                            if (flag) { // unarchive
                                walletViewModel.unarchiveWallet(wallet)
                            }
                        } else {
                            // this wallet is already exists
                        }
                    } else {
                        val newWallet = Wallet(
                            name = walletNameBinding,
                            balance = walletBalanceBinding.toDouble(),
                            description = walletDescriptionBinding
                        )

                        walletViewModel.insertWallet(newWallet)
                    }

                    performNavigation(args.source, walletNameBinding)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun performNavigation(prevFragmentString: String?, walletName: String) {
        when (prevFragmentString) {
            Constants.ADD_INCOME_HISTORY_FRAGMENT -> {
                val action =
                    AddWalletFragmentDirections.actionNavigationAddWalletToNavigationAddIncome()
                action.walletName = walletName
                findNavController().navigate(action)
            }
            Constants.ADD_EXPENSE_HISTORY_FRAGMENT -> {
                val action =
                    AddWalletFragmentDirections.actionNavigationAddWalletToNavigationAddExpense()
                action.walletName = walletName
                findNavController().navigate(action)
            }
            Constants.ADD_TRANSFER_HISTORY_FRAGMENT -> {
                val action = AddWalletFragmentDirections.actionNavigationAddWalletToNavigationAddTransfer()
                action.walletName = walletName
                action.spinnerType = args.spinnerType
                findNavController().navigate(action)
            }
            Constants.WALLETS_FRAGMENT -> {
                val action = AddWalletFragmentDirections.actionNavigationAddWalletToNavigationWallet()
                findNavController().navigate(action)
            }
        }
    }

}
