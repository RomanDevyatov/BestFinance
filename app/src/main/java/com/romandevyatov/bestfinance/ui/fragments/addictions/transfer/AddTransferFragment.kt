package com.romandevyatov.bestfinance.ui.fragments.addictions.transfer

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.romandevyatov.bestfinance.databinding.FragmentAddTransferBinding
import com.romandevyatov.bestfinance.db.entities.TransferHistory
import com.romandevyatov.bestfinance.db.entities.Wallet
import com.romandevyatov.bestfinance.ui.adapters.spinnerutils.CustomSpinnerAdapter
import com.romandevyatov.bestfinance.utils.Constants
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.TransferHistoryViewModel
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.WalletViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

// TODO: validate if the same wallets are chosen

@AndroidEntryPoint
class AddTransferFragment : Fragment() {

    private var _binding: FragmentAddTransferBinding? = null
    private val binding get() = _binding!!

    private val walletViewModel: WalletViewModel by viewModels()
    private val transferHistoryViewModel: TransferHistoryViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTransferBinding.inflate(inflater, container, false)

        setSpinners()
        setDateEditText()
        setTransferButtonListener()

        return binding.root
    }

    private fun setSpinners() {
        binding.walletNameFromSpinner.adapter = getAdapterForWalletSpinnerFrom()
        binding.walletNameToSpinner.adapter = getAdapterForWalletSpinnerTo()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setDateEditText() {
        val myCalendar = Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener() {
                view, year, month, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH,month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDate(myCalendar)
        }

        val dateET = binding.dateEditText
        dateET.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                datePicker,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        updateDate(myCalendar)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setTransferButtonListener() {
        binding.transferButton.setOnClickListener {
            walletViewModel.allWalletsNotArchivedLiveData.observe(viewLifecycleOwner) { wallets ->
                val amountBinding = binding.transferAmountEditText.text.toString().trim().toDouble()

                val walletFromNameBinding = binding.walletNameFromSpinner.selectedItem.toString()
                val walletToNameBinding = binding.walletNameToSpinner.selectedItem.toString()

                if (walletFromNameBinding != walletToNameBinding) {
                    val walletFrom = wallets.find { it.name == walletFromNameBinding }
                    updateWalletFrom(walletFrom!!, amountBinding)

                    val walletTo = wallets.find { it.name == walletToNameBinding }
                    updateWalletTo(walletTo!!, amountBinding)

                    val comment = binding.commentEditText.text.toString().trim()
                    insertTransferHistoryRecord(comment, walletFrom, walletTo, amountBinding)
                }
            }

            val action = AddTransferFragmentDirections.actionAddNewTransferFragmentToNavigationHome()
            findNavController().navigate(action)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun insertTransferHistoryRecord(
        comment: String,
        walletFrom: Wallet,
        walletTo: Wallet,
        amount: Double
    ) {
        val transferHistory = TransferHistory(
            amount = amount,
            fromWalletId = walletFrom.id!!,
            toWalletId = walletTo.id!!,
            comment = comment,
            createdDate = OffsetDateTime.now()
        )
        transferHistoryViewModel.insertTransferHistory(transferHistory)
    }

    private fun updateWalletTo(walletTo: Wallet, amount: Double) {
        val updatedWalletToInput = walletTo.input.plus(amount)
        val updatedWalletToBalance = walletTo.balance.plus(amount)

        val updatedWalletTo = Wallet(
            id = walletTo.id,
            name = walletTo.name,
            balance = updatedWalletToBalance,
            input = updatedWalletToInput,
            output = walletTo.output,
            description = walletTo.description,
            archivedDate = walletTo.archivedDate
        )
        walletViewModel.updateWallet(updatedWalletTo)
    }

    private fun updateWalletFrom(
        walletFrom: Wallet,
        amount: Double) {
        val updatedWalletFromOutput = walletFrom.output.plus(amount)
        val updatedWalletFromBalance = walletFrom.balance.minus(amount)

        val updatedWalletFrom = Wallet(
            id = walletFrom.id,
            name = walletFrom.name,
            balance = updatedWalletFromBalance,
            input = walletFrom.input,
            output = updatedWalletFromOutput,
            description = walletFrom.description,
            archivedDate = walletFrom.archivedDate
        )

        walletViewModel.updateWallet(updatedWalletFrom)
    }

    private fun getAdapterForWalletSpinnerFrom(): CustomSpinnerAdapter {
        return getAdapterForWalletSpinner(Constants.WALLET_FROM)
    }

    private fun getAdapterForWalletSpinnerTo(): CustomSpinnerAdapter {
        return getAdapterForWalletSpinner(Constants.WALLET_TO)
    }

    private fun getAdapterForWalletSpinner(firstLine: String): CustomSpinnerAdapter {
        val archiveListener =
            object : CustomSpinnerAdapter.DeleteItemClickListener {

                @RequiresApi(Build.VERSION_CODES.O)
                override fun archive(name: String) {

                }
            }

        val spinnerItems = ArrayList<String>()
        spinnerItems.add(firstLine)
        var walletSpinnerAdapter = CustomSpinnerAdapter(requireContext(), spinnerItems, archiveListener)

        walletViewModel.allWalletsNotArchivedLiveData.observe(viewLifecycleOwner) { walletList ->

            walletList?.forEach { it ->
                spinnerItems.add(it.name)
            }

            walletSpinnerAdapter = CustomSpinnerAdapter(requireContext(), spinnerItems, archiveListener)
        }

        return walletSpinnerAdapter
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateDate(calendar: Calendar) {
//        val dateFormat =  //"yyyy-MM-dd HH:mm:ss"
//        val sdf = SimpleDateFormat(dateFormat, Locale.US)
//        binding.dateEditText.setText(sdf.format(calendar.time))
        val iso8601DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        binding.dateEditText.setText(OffsetDateTime.now().format(iso8601DateTimeFormatter))
    }

    override fun onDestroyView() {
        super.onDestroyView()

        walletViewModel.allWalletsNotArchivedLiveData.removeObservers(viewLifecycleOwner)
    }

}
