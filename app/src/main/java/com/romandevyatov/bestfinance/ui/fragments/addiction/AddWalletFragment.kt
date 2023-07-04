package com.romandevyatov.bestfinance.ui.fragments.addiction

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
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addNewWalletButton.setOnClickListener {
            val walletNameBinding = binding.newWalletNameEditText.text.toString()
            val walletBalanceBinding = binding.newWalletInitialBalance.text.toString().toDouble()
            val walletDescriptionBinding = binding.newWalletDescriptionEditText.text.toString()
            walletViewModel.insertWallet(
                Wallet(
                    name = walletNameBinding,
                    balance = walletBalanceBinding,
                    description = walletDescriptionBinding
                )
            )

            performAction(args.source, walletNameBinding)
        }
    }

    fun performAction(prevFragmentString: String?, walletName: String) {
        when (prevFragmentString) {
            Constants.ADD_INCOME_HISTORY_FRAGMENT -> {
                val action = AddWalletFragmentDirections.actionNavigationAddWalletToNavigationAddIncome()
                action.walletName = walletName
                findNavController().navigate(action)
            }
            Constants.ADD_EXPENSE_HISTORY_FRAGMENT -> {
                val action = AddWalletFragmentDirections.actionNavigationAddWalletToNavigationAddExpense()
                action.walletName = walletName
                findNavController().navigate(action)
            }
            else -> {
                AddWalletFragmentDirections.actionNavigationAddWalletToNavigationWallet()
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}