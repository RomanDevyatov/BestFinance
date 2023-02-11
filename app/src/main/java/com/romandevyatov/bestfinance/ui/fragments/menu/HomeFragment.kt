package com.romandevyatov.bestfinance.ui.fragments.menu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.FragmentHomeBinding
import com.romandevyatov.bestfinance.db.entities.Wallet
import com.romandevyatov.bestfinance.viewmodels.ExpenseHistoryViewModel
import com.romandevyatov.bestfinance.viewmodels.IncomeGroupViewModel
import com.romandevyatov.bestfinance.viewmodels.IncomeHistoryViewModel
import com.romandevyatov.bestfinance.viewmodels.WalletViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val walletViewModel: WalletViewModel by viewModels()
    private val incomeHistoryViewModel: IncomeHistoryViewModel by viewModels()
    private val expenseHistoryViewModel: ExpenseHistoryViewModel by viewModels()
    private val incomeGroupViewModel: IncomeGroupViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var passiveIncomeValue: Double? = null
//        binding.passiveIncomeValueTextView.text = passiveIncomeValue

        var totalIncomeValue: Double? = null
//        binding.totalIncomeValueTextView.text = totalIncomeValue

        var totalExpensesValue: Double? = null
//        binding.totalExpensesValueTextView.text = totalExpensesValue

        val moneyFlowValue: Double? = null
//        binding.moneyFlowValueTextView.text = moneyFlowValue


        binding.goToAddIncomeButton.setOnClickListener {

            findNavController().navigate(
                R.id.action_navigation_home_to_navigation_add_income
            )
        }

        binding.goToAddExpenseButton.setOnClickListener {

            findNavController().navigate(
                R.id.action_navigation_home_to_navigation_add_expense
            )
        }

        binding.goToHistoryButton.setOnClickListener {

            findNavController().navigate(
                R.id.action_navigation_home_to_navigation_history
            )

        }

        walletViewModel.walletsLiveData.observe(viewLifecycleOwner) { walletList ->
            binding.capitalTextView.text = walletList.sumOf { it.balance }.toString()
        }

        incomeHistoryViewModel.incomeHistoryLiveData.observe(viewLifecycleOwner) { history ->
            passiveIncomeValue = (history.filter { it.incomeGroupId == 3L}).sumOf { it.amount }
            binding.passiveIncomeValueTextView.text = passiveIncomeValue.toString()

            totalIncomeValue = history.sumOf { it.amount }
            binding.totalIncomeValueTextView.text = totalIncomeValue.toString()

            expenseHistoryViewModel.expenseHistoryLiveData.observe(viewLifecycleOwner) { expenseHistory ->
                totalExpensesValue = expenseHistory.sumOf { it.amount }
                binding.totalExpensesValueTextView.text = totalExpensesValue.toString()
                
                binding.moneyFlowValueTextView.text = ((totalIncomeValue!!.minus(totalExpensesValue!!) * 100.0).roundToInt() / 100.0).toString()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}