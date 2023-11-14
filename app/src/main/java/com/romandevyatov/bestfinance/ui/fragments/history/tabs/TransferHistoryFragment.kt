package com.romandevyatov.bestfinance.ui.fragments.history.tabs

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.data.entities.relations.TransferHistoryWithWallets
import com.romandevyatov.bestfinance.databinding.FragmentTransferHistoryBinding
import com.romandevyatov.bestfinance.ui.adapters.history.bydate.transfers.HistoryTransferByDateAdapter
import com.romandevyatov.bestfinance.ui.adapters.history.bydate.transfers.TransferAdapter
import com.romandevyatov.bestfinance.ui.adapters.history.bydate.transfers.models.TransferHistoryItem
import com.romandevyatov.bestfinance.ui.adapters.history.bydate.transfers.models.TransferItem
import com.romandevyatov.bestfinance.ui.fragments.history.HistoryFragmentDirections
import com.romandevyatov.bestfinance.utils.BackStackLogger
import com.romandevyatov.bestfinance.utils.TextFormatter.removeTrailingZeros
import com.romandevyatov.bestfinance.utils.TextFormatter.roundDoubleToTwoDecimalPlaces
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.TransferHistoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.format.DateTimeFormatter
import java.util.*

@AndroidEntryPoint
class TransferHistoryFragment : Fragment() {

    private var _binding: FragmentTransferHistoryBinding? = null
    private val binding get() = _binding!!

    private val transferHistoryViewModel: TransferHistoryViewModel by viewModels()

    private val listener = object : TransferAdapter.OnHistoryItemListener {

        override fun navigateToUpdateTransfer(id: Long) {
            val action =
                HistoryFragmentDirections.actionHistoryFragmentToUpdateTransferHistoryFragment()
            action.transferHistoryId = id
            findNavController().navigate(action)
        }
    }

    private val transferHistoryAdapter: HistoryTransferByDateAdapter = HistoryTransferByDateAdapter(listener)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransferHistoryBinding.inflate(inflater, container, false)

        initRecyclerView()

        BackStackLogger.logBackStack(findNavController())

        return binding.root
    }

    private fun initRecyclerView() {
        binding.transferHistoryRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.transferHistoryRecyclerView.adapter = transferHistoryAdapter
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        transferHistoryViewModel.allTransferHistoryWithWalletsLiveData.observe(viewLifecycleOwner) { transfers ->
            val transferItems =
                convertTransfersToTransferHistoryItemList(transfers)
            val sortedTransferItems = transferItems.sortedByDescending { it.date }
            val groupTransfersByDate = groupTransfersByDate(sortedTransferItems)
            transferHistoryAdapter.submitList(groupTransfersByDate)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun groupTransfersByDate(transactionItemList: List<TransferItem>): MutableList<TransferHistoryItem> {
        val groupedTransfers = transactionItemList.groupBy {
            val transferDate = it.date?.toLocalDate()
            transferDate?.format(DateTimeFormatter.ofPattern("d MMMM uuuu", Locale.getDefault())).toString()
        }

        val transactionHistoryItemList = mutableListOf<TransferHistoryItem>()

        for ((date, transfers) in groupedTransfers) {
            transactionHistoryItemList.add(TransferHistoryItem(date, transfers))
        }

        return transactionHistoryItemList
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun convertTransfersToTransferHistoryItemList(
        transfers: List<TransferHistoryWithWallets>
    ): MutableList<TransferItem> {
        val transferItems = mutableListOf<TransferItem>()

        for (transfer in transfers) {
            val transferHistory = transfer.transferHistory
            val walletFrom = transfer.walletFrom
            val walletTo = transfer.walletTo

            val formattedAmountText = removeTrailingZeros(roundDoubleToTwoDecimalPlaces(transferHistory.amount).toString()) + walletFrom.currencyCode
            val formattedAmountBaseText = removeTrailingZeros(roundDoubleToTwoDecimalPlaces(transferHistory.amountBase).toString()) + transferHistoryViewModel.getDefaultCurrencyCode()

            val formattedAmountBaseTextString = "${formattedAmountBaseText}(${getString(R.string.base)})"

            val transactionItem = TransferItem(
                id = transferHistory.id,
                fromName = walletFrom.name,
                toName = walletTo.name,
                amount = formattedAmountText,
                amountBase = formattedAmountBaseTextString,
                comment = transferHistory.comment ?: "",
                date = transferHistory.date
            )
            transferItems.add(transactionItem)
        }

        return transferItems
    }
}
