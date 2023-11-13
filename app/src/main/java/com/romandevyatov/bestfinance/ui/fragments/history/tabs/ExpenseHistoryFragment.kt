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
import com.romandevyatov.bestfinance.data.entities.ExpenseGroupEntity
import com.romandevyatov.bestfinance.data.entities.relations.ExpenseHistoryWithExpenseSubGroupAndWallet
import com.romandevyatov.bestfinance.databinding.FragmentExpenseHistoryBinding
import com.romandevyatov.bestfinance.ui.adapters.history.bydate.transactions.HistoryTransactionByDateAdapter
import com.romandevyatov.bestfinance.ui.adapters.history.bydate.transactions.TransactionAdapter
import com.romandevyatov.bestfinance.ui.adapters.history.bydate.transactions.model.TransactionHistoryItem
import com.romandevyatov.bestfinance.ui.adapters.history.bydate.transactions.model.TransactionItem
import com.romandevyatov.bestfinance.ui.fragments.history.HistoryFragmentDirections
import com.romandevyatov.bestfinance.utils.BackStackLogger
import com.romandevyatov.bestfinance.utils.TextFormatter.removeTrailingZeros
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.ExpenseGroupViewModel
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.ExpenseHistoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.format.DateTimeFormatter
import java.util.Locale

@AndroidEntryPoint
class ExpenseHistoryFragment : Fragment() {

    private var _binding: FragmentExpenseHistoryBinding? = null
    private val binding get() = _binding!!

    private val expenseHistoryViewModel: ExpenseHistoryViewModel by viewModels()
    private val groupViewModel: ExpenseGroupViewModel by viewModels()
    private var expenseHistoryAdapter: HistoryTransactionByDateAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpenseHistoryBinding.inflate(inflater, container, false)

        initRecyclerView()

        BackStackLogger.logBackStack(findNavController())

        return binding.root
    }

    private fun initRecyclerView() {
        val listener = object : TransactionAdapter.OnHistoryItemListener {

            override fun navigateToUpdateTransaction(id: Long) {
                val action =
                    HistoryFragmentDirections.actionHistoryFragmentToUpdateExpenseHistoryFragment()
                action.expenseHistoryId = id
                findNavController().navigate(action)
            }
        }

        expenseHistoryAdapter = HistoryTransactionByDateAdapter(R.drawable.ic_arrow_upward, listener)
        binding.expenseHistoryRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.expenseHistoryRecyclerView.adapter = expenseHistoryAdapter
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        groupViewModel.allExpenseGroupEntityLiveData.observe(viewLifecycleOwner) { groups ->
            val expenseGroupEntityMap: Map<Long?, ExpenseGroupEntity> = groups.associateBy { it.id }
            expenseHistoryViewModel.allExpenseHistoryWithExpenseGroupAndWalletLiveData.observe(
                viewLifecycleOwner
            ) { allExpenseHistoryWithExpenseGroupAndWallet ->
                val transactionItems =
                    convertHistoryToExpenseHistoryItemList(allExpenseHistoryWithExpenseGroupAndWallet, expenseGroupEntityMap)
                val sortedTransactionItems = transactionItems.sortedByDescending { it.date }
                val groupTransactionsByDate = groupTransactionsByDate(sortedTransactionItems)
                expenseHistoryAdapter?.submitList(groupTransactionsByDate)
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
    private fun convertHistoryToExpenseHistoryItemList(
        allExpenseHistoryWithExpenseGroupAndWallet: List<ExpenseHistoryWithExpenseSubGroupAndWallet>,
        expenseGroupEntityMap: Map<Long?, ExpenseGroupEntity>
    ): MutableList<TransactionItem> {
        val transactionItemList = mutableListOf<TransactionItem>()

        for (incomeHistoryWithIncomeSubGroupAndWallet in allExpenseHistoryWithExpenseGroupAndWallet) {
            val expenseHistory = incomeHistoryWithIncomeSubGroupAndWallet.expenseHistory
            val expenseSubGroup = incomeHistoryWithIncomeSubGroupAndWallet.expenseSubGroup
            val wallet = incomeHistoryWithIncomeSubGroupAndWallet.wallet

            if (wallet != null) {
                val formattedAmountText = "-".plus(removeTrailingZeros(expenseHistory.amount.toString()))
                    .plus(expenseHistoryViewModel.getCurrencySymbolByCode(wallet.currencyCode))
                val formattedAmountBaseText = "-".plus(removeTrailingZeros(expenseHistory.amountBase.toString()))
                    .plus(expenseHistoryViewModel.currentDefaultCurrencySymbol)
                    .plus("(${getString(R.string.base)})")

                val transactionItem = TransactionItem(
                    id = expenseHistory.id,
                    groupName = expenseGroupEntityMap[expenseSubGroup?.expenseGroupId]?.name ?: "",
                    subGroupGroupName = expenseSubGroup?.name ?: getString(R.string.changed_balance),
                    amount = formattedAmountText,
                    comment = expenseHistory.comment ?: "",
                    date = expenseHistory.date,
                    walletName = wallet.name,
                    amountBase = formattedAmountBaseText
                )
                transactionItemList.add(transactionItem)
            }
        }

        return transactionItemList
    }

}
