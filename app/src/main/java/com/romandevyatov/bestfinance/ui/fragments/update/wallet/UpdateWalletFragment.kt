package com.romandevyatov.bestfinance.ui.fragments.update.wallet

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
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.data.entities.ExpenseHistory
import com.romandevyatov.bestfinance.data.entities.IncomeHistory
import com.romandevyatov.bestfinance.data.entities.Wallet
import com.romandevyatov.bestfinance.databinding.FragmentUpdateWalletBinding
import com.romandevyatov.bestfinance.utils.Constants
import com.romandevyatov.bestfinance.utils.WindowUtil
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.UpdateWalletViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime

@AndroidEntryPoint
class UpdateWalletFragment : Fragment() {

    private var _binding: FragmentUpdateWalletBinding? = null

    private val binding get() = _binding!!

    private val updateWalletViewModel: UpdateWalletViewModel by viewModels()

    private val args: UpdateWalletFragmentArgs by navArgs()

    private var walletId: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateWalletBinding.inflate(inflater, container, false)

        updateWalletViewModel.getWalletByNameLiveData(args.walletName.toString())?.observe(viewLifecycleOwner) { wallet ->
            if (wallet != null) {
                walletId = wallet.id!!

                binding.nameEditText.setText(wallet.name)
                binding.balanceEditText.setText(wallet.balance.toString())
                binding.descriptionEditText.setText(wallet.description)
            }
        }

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.updateWalletButton.setOnClickListener {
            val walletNameBinding = binding.nameEditText.text.toString()
            val walletBalanceBinding = binding.balanceEditText.text.toString().toDouble()
            val walletDescriptionBinding = binding.descriptionEditText.text.toString()

            updateWalletViewModel.getWalletByNameLiveData(walletNameBinding)?.observe(viewLifecycleOwner) { wallet ->
                if (walletNameBinding == args.walletName.toString() || wallet == null) {
                    val updatedWallet = Wallet(
                        id = walletId,
                        name = walletNameBinding,
                        balance = walletBalanceBinding,
                        description = walletDescriptionBinding
                    )

                    val difference = walletBalanceBinding - wallet.balance
                    if (difference != 0.0) {
                        showChangingBalanceDialog(requireContext(), walletId!!, difference, getString(R.string.balance_is_changed_do_you_want_to_add_history_record))
                    }

                    updateWalletViewModel.updateNameAndDescriptionAndBalanceWalletById(updatedWallet)

                    performBackNavigation(args.source.toString())
                } else if (wallet.archivedDate == null) {
                    WindowUtil.showExistingDialog(
                        requireContext(),
                        getString(R.string.wallet_name_already_exists, walletNameBinding)
                    )
                } else {
                    showUnarchiveDialog(
                        requireContext(),
                        wallet,
                        getString(R.string.wallet_name_is_archived_do_you_want_to_unarchive_and_proceed_updating, walletNameBinding, walletNameBinding)
                    )
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
            val incomeOrExpenseType = difference.compareTo(0.0)
            if (incomeOrExpenseType == 1) {
                updateWalletViewModel.addOnlyWalletIncomeHistoryRecord(
                    IncomeHistory(
                        incomeSubGroupId = null,
                        amount = difference,
                        comment = getString(R.string.changed_wallet_balance),
                        date = LocalDateTime.now(),
                        walletId = walletId
                    )
                )
            } else if (incomeOrExpenseType == -1) {
                updateWalletViewModel.addOnlyWalletExpenseHistoryRecord(
                    ExpenseHistory(
                        expenseSubGroupId = null,
                        amount = difference,
                        comment = getString(R.string.changed_wallet_balance),
                        date = LocalDateTime.now(),
                        walletId = walletId
                    )
                )
            }
        }

        bntNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
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
            updateWalletViewModel.unarchiveWallet(wallet)
            performBackNavigation(args.source.toString())
        }

        bntNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun performBackNavigation(prevFragmentString: String?) {
        when (prevFragmentString) {
            Constants.MENU_WALLET_FRAGMENT -> {
                val action =
                    UpdateWalletFragmentDirections.actionNavigationUpdateWalletToNavigationWallet()
                findNavController().navigate(action)
            }
            Constants.WALLETS_SETTINGS_FRAGMENT -> {
                val action =
                    UpdateWalletFragmentDirections.actionNavigationUpdateWalletToWalletsSettings()
                findNavController().navigate(action)
            }
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
