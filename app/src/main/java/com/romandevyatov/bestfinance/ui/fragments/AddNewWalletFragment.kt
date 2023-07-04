package com.romandevyatov.bestfinance.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.romandevyatov.bestfinance.databinding.FragmentAddNewWalletBinding
import com.romandevyatov.bestfinance.db.entities.Wallet
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.WalletViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddNewWalletFragment : Fragment() {

    private var _binding: FragmentAddNewWalletBinding? = null
    private val binding get() = _binding!!

    private val walletViewModel: WalletViewModel by viewModels()

    private val args: AddNewWalletFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddNewWalletBinding.inflate(inflater, container, false)
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
            "add_income_history_fragment" -> {
                val action = AddNewWalletFragmentDirections.actionNavigationAddNewWalletToNavigationAddIncome()
                action.walletName = walletName
                findNavController().navigate(action)
            }
            "add_expense_history_fragment" -> {
                val action = AddNewWalletFragmentDirections.actionNavigationAddNewWalletToNavigationAddExpense()
                action.walletName = walletName
                findNavController().navigate(action)
            }
            else -> {
                AddNewWalletFragmentDirections.actionNavigationAddNewWalletToNavigationWallet()
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
