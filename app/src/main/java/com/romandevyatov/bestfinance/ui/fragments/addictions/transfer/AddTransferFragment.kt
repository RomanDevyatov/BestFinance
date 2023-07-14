package com.romandevyatov.bestfinance.ui.fragments.addictions.transfer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.romandevyatov.bestfinance.databinding.FragmentAddTransferBinding
import com.romandevyatov.bestfinance.db.entities.TransferHistory
import com.romandevyatov.bestfinance.db.entities.Wallet
import com.romandevyatov.bestfinance.ui.adapters.spinnerutils.SpinnerUtils
import com.romandevyatov.bestfinance.utils.Constants.WALLET_FROM
import com.romandevyatov.bestfinance.utils.Constants.WALLET_TO
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.TransferHistoryViewModel
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.WalletViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddTransferFragment : Fragment() {

    private var _binding: FragmentAddTransferBinding? = null
    private val binding get() = _binding!!

    private val walletViewModel: WalletViewModel by viewModels()
    private val transferHistoryViewModel: TransferHistoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTransferBinding.inflate(inflater, container, false)

        initWalletFromSpinner()
        initWalletToSpinner()

        binding.transferButton.setOnClickListener {
            walletViewModel.getWalletByNameNotArchivedLiveData(binding.walletNameFromSpinner.selectedItem.toString())
                .observe(viewLifecycleOwner) { walletFrom ->

                    val amount = binding.amount.text.toString().trim().toDouble()

                    val updatedWalletFromOutput = walletFrom.output.plus(amount)
                    val updatedWalletFromBalance = walletFrom.balance.minus(amount)

                    walletViewModel.updateWallet(
                        Wallet(
                            id = walletFrom.id,
                            name = walletFrom.name,
                            balance = updatedWalletFromBalance,
                            input = walletFrom.input,
                            output = updatedWalletFromOutput,
                            description = walletFrom.description,
                            archivedDate = walletFrom.archivedDate
                        )
                    )

                    walletViewModel.getWalletByNameNotArchivedLiveData(binding.walletNameToSpinner.selectedItem.toString())
                        .observe(viewLifecycleOwner) { walletTo ->

                            val updatedWalletToInput = walletTo.input.plus(amount)
                            val updatedWalletToBalance = walletTo.balance.plus(amount)

                            walletViewModel.updateWallet(
                                Wallet(
                                    id = walletTo.id,
                                    name = walletTo.name,
                                    balance = updatedWalletToBalance,
                                    input = updatedWalletToInput,
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

                            val action = AddTransferFragmentDirections.actionAddNewTransferFragmentToNavigationHome()
                            findNavController().navigate(action)
                    }
            }
        }

        return binding.root
    }

    private fun initWalletFromSpinner() {
        val spinnerAdapter = SpinnerUtils.getArraySpinner(requireContext())

        spinnerAdapter.add(WALLET_FROM)

        walletViewModel.notArchivedWalletsLiveData.observe(viewLifecycleOwner) { walletList ->
            walletList?.forEach { it ->
                spinnerAdapter.add(it.name)
            }
        }

        binding.walletNameFromSpinner.adapter = spinnerAdapter
    }

    private fun initWalletToSpinner() {
        val spinnerAdapter = SpinnerUtils.getArraySpinner(requireContext())

        spinnerAdapter.add(WALLET_TO)

        walletViewModel.notArchivedWalletsLiveData.observe(viewLifecycleOwner) { walletList ->
            walletList?.forEach { it ->
                spinnerAdapter.add(it.name)
            }
        }

        binding.walletNameToSpinner.adapter = spinnerAdapter
    }



}