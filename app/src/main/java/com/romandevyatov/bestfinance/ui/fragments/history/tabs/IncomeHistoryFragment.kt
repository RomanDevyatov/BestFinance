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
import com.romandevyatov.bestfinance.data.entities.IncomeGroupEntity
import com.romandevyatov.bestfinance.data.entities.relations.IncomeHistoryWithIncomeSubGroupAndWallet
import com.romandevyatov.bestfinance.databinding.FragmentIncomeHistoryBinding
import com.romandevyatov.bestfinance.ui.adapters.history.bydate.transactions.HistoryTransactionByDateAdapter
import com.romandevyatov.bestfinance.ui.adapters.history.bydate.transactions.TransactionAdapter
import com.romandevyatov.bestfinance.ui.adapters.history.bydate.transactions.model.TransactionHistoryItem
import com.romandevyatov.bestfinance.ui.adapters.history.bydate.transactions.model.TransactionItem
import com.romandevyatov.bestfinance.ui.fragments.history.HistoryFragmentDirections
import com.romandevyatov.bestfinance.utils.BackStackLogger
import com.romandevyatov.bestfinance.utils.TextFormatter.removeTrailingZeros
import com.romandevyatov.bestfinance.utils.TextFormatter.roundDoubleToTwoDecimalPlaces
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.IncomeGroupViewModel
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.IncomeHistoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.format.DateTimeFormatter
import java.util.*

@AndroidEntryPoint
class IncomeHistoryFragment : Fragment() {

    private var _binding: FragmentIncomeHistoryBinding? = null
    private val binding get() = _binding!!

    private val incomeHistoryViewModel: IncomeHistoryViewModel by viewModels()
    private val groupViewModel: IncomeGroupViewModel by viewModels()
    private var incomeHistoryAdapter: HistoryTransactionByDateAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIncomeHistoryBinding.inflate(inflater, container, false)

        initRecyclerView()

        BackStackLogger.logBackStack(findNavController())

        return binding.root
    }

    private fun initRecyclerView() {
        val listener = object : TransactionAdapter.OnHistoryItemListener {

            override fun navigateToUpdateTransaction(id: Long) {
                val action =
                    HistoryFragmentDirections.actionHistoryFragmentToUpdateIncomeHistoryFragment()
                action.incomeHistoryId = id
                findNavController().navigate(action)
            }
        }

        incomeHistoryAdapter = HistoryTransactionByDateAdapter(R.drawable.ic_arrow_downward, listener)
        binding.incomeHistoryRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.incomeHistoryRecyclerView.adapter = incomeHistoryAdapter
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        groupViewModel.allEntityIncomeGroupsLiveData.observe(viewLifecycleOwner) { groups ->
            val incomeGroupEntityMap: Map<Long?, IncomeGroupEntity> = groups.associateBy { it.id }
            incomeHistoryViewModel.allIncomeHistoryWithIncomeSubGroupAndWalletLiveData.observe(
                viewLifecycleOwner
            ) { allIncomeHistoryWithIncomeGroupAndWallet ->
                val transactionItems =
                    convertHistoryToIncomeHistoryItemList(allIncomeHistoryWithIncomeGroupAndWallet, incomeGroupEntityMap)
                val sortedTransactionItems = transactionItems.sortedByDescending { it.date }
                val groupTransactionsByDate = groupTransactionsByDate(sortedTransactionItems)
                incomeHistoryAdapter?.submitList(groupTransactionsByDate)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun groupTransactionsByDate(transactionItemList: List<TransactionItem>): MutableList<TransactionHistoryItem> {
        val groupedTransactions = transactionItemList.groupBy {
            val transactionDate = it.date?.toLocalDate()
            transactionDate?.format(DateTimeFormatter.ofPattern("d MMMM uuuu", Locale.getDefault())).toString()
        }

        val transactionHistoryItemList = mutableListOf<TransactionHistoryItem>()

        for ((date, transactions) in groupedTransactions) {
            transactionHistoryItemList.add(TransactionHistoryItem(date, transactions))
        }

        return transactionHistoryItemList
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun convertHistoryToIncomeHistoryItemList(
        allIncomeHistoryWithIncomeGroupAndWallet: List<IncomeHistoryWithIncomeSubGroupAndWallet>,
        incomeGroupEntityMap: Map<Long?, IncomeGroupEntity>
    ): MutableList<TransactionItem> {
        val transactionItemList = mutableListOf<TransactionItem>()

        for (incomeHistoryWithIncomeSubGroupAndWallet in allIncomeHistoryWithIncomeGroupAndWallet) {
            val incomeHistory = incomeHistoryWithIncomeSubGroupAndWallet.incomeHistoryEntity
            val incomeSubGroup = incomeHistoryWithIncomeSubGroupAndWallet.incomeSubGroup
            val wallet = incomeHistoryWithIncomeSubGroupAndWallet.walletEntity

            if (wallet != null) {
                val formattedAmountText = "+".plus(removeTrailingZeros(roundDoubleToTwoDecimalPlaces(incomeHistory.amount).toString()))
                    .plus(wallet.currencyCode)
                val formattedAmountBaseText = "+"
                    .plus(removeTrailingZeros(roundDoubleToTwoDecimalPlaces(incomeHistory.amountBase).toString()))
                    .plus(incomeHistoryViewModel.getDefaultCurrencyCode())
                    .plus("(${getString(R.string.base)})")

                val transactionItem = TransactionItem(
                    id = incomeHistory.id,
                    groupName = incomeGroupEntityMap[incomeSubGroup?.incomeGroupId]?.name ?: "",
                    subGroupGroupName = incomeSubGroup?.name ?: getString(R.string.changed_balance),
                    amount = formattedAmountText,
                    amountBase = formattedAmountBaseText,
                    comment = incomeHistory.comment ?: "",
                    date = incomeHistory.date,
                    walletName = wallet.name
                )
                transactionItemList.add(transactionItem)
            }
        }

        return transactionItemList
    }
}
