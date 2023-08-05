package com.romandevyatov.bestfinance.ui.fragments.update

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.FragmentUpdateWalletBinding
import com.romandevyatov.bestfinance.data.entities.Wallet
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.WalletViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateWalletFragment : Fragment() {

    private var _binding: FragmentUpdateWalletBinding? = null

    private val binding get() = _binding!!

    private val walletViewModel: WalletViewModel by viewModels()

    private val args: UpdateWalletFragmentArgs by navArgs()

    private var walletId: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateWalletBinding.inflate(inflater, container, false)

        walletViewModel.getWalletByNameNotArchivedLiveData(args.walletName.toString()).observe(viewLifecycleOwner) { wallet ->
            binding.nameEditText.setText(wallet.name)
            binding.balanceEditText.setText(wallet.balance.toString())
            binding.descriptionEditText.setText(wallet.description)
            walletId = wallet.id
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.updateWalletButton.setOnClickListener {
            val walletNameBinding = binding.nameEditText.text.toString()
            val walletBalanceBinding = binding.balanceEditText.text.toString().toDouble()
            val walletDescriptionBinding = binding.descriptionEditText.text.toString()

            if (walletNameBinding != args.walletName.toString()) {
                walletViewModel.getWalletByNameLiveData(walletNameBinding)?.observe(viewLifecycleOwner) { wallet ->
                    if (wallet == null) {
                        val updatedWallet = Wallet(
                            id = walletId,
                            name = walletNameBinding,
                            balance = walletBalanceBinding,
                            description = walletDescriptionBinding
                        )

                        walletViewModel.updateWalletById(updatedWallet)

                        val action =
                            UpdateWalletFragmentDirections.actionNavigationUpdateWalletToNavigationWallet()
                        findNavController().navigate(action)
                    } else if (wallet.archivedDate == null){
                        showExistingDialog(
                            requireContext(),
                            "The wallet with this name `$walletNameBinding` is already existing."
                        )
                    } else {
                        showUnarchiveDialog(
                            requireContext(),
                            wallet,
                            "The wallet with this name is archived. Do you want to unarchive `$walletNameBinding` wallet and proceed updating?")
                    }
                }
            } else {
                val updatedWallet = Wallet(
                    id = walletId,
                    name = walletNameBinding,
                    balance = walletBalanceBinding,
                    description = walletDescriptionBinding
                )

                walletViewModel.updateWalletById(updatedWallet)

                val action =
                    UpdateWalletFragmentDirections.actionNavigationUpdateWalletToNavigationWallet()
                findNavController().navigate(action)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showUnarchiveDialog(context: Context, wallet: Wallet, message: String?) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_alert)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val tvMessage: TextView = dialog.findViewById(R.id.tvMessage)
        val btnYes: Button = dialog.findViewById(R.id.btnYes)
        val bntNo: Button = dialog.findViewById(R.id.btnNo)

        tvMessage.text = message

        btnYes.setOnClickListener {
            dialog.dismiss()
            walletViewModel.unarchiveWallet(wallet)
            val action =
                UpdateWalletFragmentDirections.actionNavigationUpdateWalletToNavigationWallet()
            findNavController().navigate(action)
        }

        bntNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showExistingDialog(context: Context, message: String?) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_info)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val tvMessage: TextView = dialog.findViewById(R.id.tvMessage)
        val btnOk: Button = dialog.findViewById(R.id.btnOk)

        tvMessage.text = message

        btnOk.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
