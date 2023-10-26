package com.romandevyatov.bestfinance.ui.fragments.history

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
import com.romandevyatov.bestfinance.data.entities.relations.IncomeHistoryWithIncomeSubGroupAndWallet
import com.romandevyatov.bestfinance.databinding.FragmentIncomeHistoryBinding
import com.romandevyatov.bestfinance.ui.adapters.history.bydate.HistoryByDateAdapter
import com.romandevyatov.bestfinance.ui.adapters.history.bydate.TransactionAdapter
import com.romandevyatov.bestfinance.ui.adapters.history.bydate.model.IncomeHistoryItem
import com.romandevyatov.bestfinance.ui.adapters.history.bydate.model.TransactionItem
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
//    private var incomeHistoryAdapter: IncomeHistoryAdapter? = null
    private var incomeHistoryAdapter: HistoryByDateAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIncomeHistoryBinding.inflate(inflater, container, false)

        initRecyclerView()

        return binding.root
    }

    private fun initRecyclerView() {
        groupViewModel.allIncomeGroupsLiveData.observe(viewLifecycleOwner) { groups ->
            groups.associateBy { it.id }

            val listener = object : TransactionAdapter.OnHistoryItemListener {

                override fun navigateToUpdateTransaction(id: Long) {
                    val action = HistoryFragmentDirections.actionHistoryFragmentToUpdateIncomeHistoryFragment()
                    action.incomeHistoryId = id
                    findNavController().navigate(action)
                }
            }

            incomeHistoryAdapter = HistoryByDateAdapter(listener)
            binding.incomeHistoryRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.incomeHistoryRecyclerView.adapter = incomeHistoryAdapter
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        incomeHistoryViewModel.allIncomeHistoryWithIncomeSubGroupAndWalletLiveData.observe(viewLifecycleOwner) { allIncomeHistoryWithIncomeGroupAndWallet ->
                val transactionItems = convertHistoryToIncomeHistoryItemList(allIncomeHistoryWithIncomeGroupAndWallet)
                val groupTransactionsByDate = groupTransactionsByDate(transactionItems)
                incomeHistoryAdapter?.submitList(groupTransactionsByDate)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun groupTransactionsByDate(transactionItemList: List<TransactionItem>): MutableList<IncomeHistoryItem> {
        val groupedTransactions = transactionItemList.groupBy {
            val transactionDate = it.date?.toLocalDate()
            transactionDate?.format(DateTimeFormatter.ofPattern("d MMMM uuuu", Locale.getDefault())).toString()
        }

        val incomeHistoryItemList = mutableListOf<IncomeHistoryItem>()

        for ((date, transactions) in groupedTransactions) {
            incomeHistoryItemList.add(IncomeHistoryItem(date, transactions))
        }

        return incomeHistoryItemList
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun convertHistoryToIncomeHistoryItemList(allIncomeHistoryWithIncomeGroupAndWallet: List<IncomeHistoryWithIncomeSubGroupAndWallet>): MutableList<TransactionItem> {
        val transactionItemList = mutableListOf<TransactionItem>()

        for (incomeHistoryWithIncomeSubGroupAndWallet in allIncomeHistoryWithIncomeGroupAndWallet) {
            val incomeHistory = incomeHistoryWithIncomeSubGroupAndWallet.incomeHistory
            val incomeSubGroup = incomeHistoryWithIncomeSubGroupAndWallet.incomeSubGroup
            val wallet = incomeHistoryWithIncomeSubGroupAndWallet.wallet

            if (incomeSubGroup != null && wallet != null) {
                val transactionItem = TransactionItem(
                    id = incomeHistory.id,
                    groupName = incomeSubGroup.name,
                    subGroupGroupName = incomeSubGroup.name,
                    amount = incomeHistory.amount,
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
