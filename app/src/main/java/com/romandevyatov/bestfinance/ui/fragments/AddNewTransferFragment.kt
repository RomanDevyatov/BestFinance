package com.romandevyatov.bestfinance.ui.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.romandevyatov.bestfinance.databinding.FragmentTransferBinding
import com.romandevyatov.bestfinance.db.entities.TransferHistory
import com.romandevyatov.bestfinance.db.entities.Wallet
import com.romandevyatov.bestfinance.ui.adapters.spinnerutils.SpinnerUtils
import com.romandevyatov.bestfinance.viewmodels.TransferHistoryViewModel
import com.romandevyatov.bestfinance.viewmodels.WalletViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddNewTransferFragment : Fragment() {

    private var _binding: FragmentTransferBinding? = null
    private val binding get() = _binding!!

    private val walletViewModel: WalletViewModel by viewModels()
    private val transferHistoryViewModel: TransferHistoryViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransferBinding.inflate(inflater, container, false)

        initWalletFromSpinner()
        initWalletToSpinner()

        binding.transferButton.setOnClickListener {
            walletViewModel.getNotArchivedWalletByNameLiveData(binding.walletNameFromSpinner.selectedItem.toString())
                .observe(viewLifecycleOwner) { walletFrom ->

                    val amount = binding.amount.text.toString().trim().toDouble()

                    val updatedOutput = walletFrom.output.plus(amount)

                    walletViewModel.updateWallet(
                        Wallet(
                            id = walletFrom.id,
                            name = walletFrom.name,
                            balance = walletFrom.balance,
                            input = walletFrom.input,
                            output = updatedOutput,
                            description = walletFrom.description,
                            archivedDate = walletFrom.archivedDate
                        )
                    )

                    walletViewModel.getNotArchivedWalletByNameLiveData(binding.walletNameToSpinner.selectedItem.toString())
                        .observe(viewLifecycleOwner) { walletTo ->

                            val updatedInput = walletTo.input.plus(amount)

                            walletViewModel.updateWallet(
                                Wallet(
                                    id = walletTo.id,
                                    name = walletTo.name,
                                    balance = walletTo.balance,
                                    input = updatedInput,
                                    output = walletTo.output,
                                    description = walletTo.description,
                                    archivedDate = walletTo.archivedDate
                                )
                            )

                            transferHistoryViewModel.insertTransferHistory(
                                TransferHistory(
                                    balance = amount,
                                    fromWalletId = walletFrom.id!!,
                                    toWalletId = walletTo.id!!
                                )
                            )

                            val action =
                                AddNewTransferFragmentDirections.actionAddNewTransferFragmentToNavigationHome()
                            findNavController().navigate(action)
                    }
            }
        }

        return binding.root
    }

    private fun initWalletFromSpinner() {
        val spinnerAdapter = SpinnerUtils.getArraySpinner(requireContext())

        spinnerAdapter.add("Wallet from")

        walletViewModel.notArchivedWalletsLiveData.observe(viewLifecycleOwner) { walletList ->
            walletList?.forEach { it ->
                spinnerAdapter.add(it.name)
            }
        }

        binding.walletNameFromSpinner.adapter = spinnerAdapter
    }

    private fun initWalletToSpinner() {
        val spinnerAdapter = SpinnerUtils.getArraySpinner(requireContext())

        spinnerAdapter.add("Wallet to")

        walletViewModel.notArchivedWalletsLiveData.observe(viewLifecycleOwner) { walletList ->
            walletList?.forEach { it ->
                spinnerAdapter.add(it.name)
            }
        }

        binding.walletNameToSpinner.adapter = spinnerAdapter
    }



}
