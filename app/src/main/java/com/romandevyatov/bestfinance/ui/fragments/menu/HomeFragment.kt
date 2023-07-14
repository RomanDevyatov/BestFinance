package com.romandevyatov.bestfinance.ui.fragments.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.romandevyatov.bestfinance.databinding.FragmentMenuHomeBinding
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.HomeViewModel
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.ExpenseHistoryViewModel
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.IncomeGroupViewModel
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.IncomeHistoryViewModel
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.WalletViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentMenuHomeBinding? = null
    private val binding get() = _binding!!

    private val walletViewModel: WalletViewModel by viewModels()
    private val incomeHistoryViewModel: IncomeHistoryViewModel by viewModels()
    private val incomeGroupViewModel: IncomeGroupViewModel by viewModels()
    private val expenseHistoryViewModel: ExpenseHistoryViewModel by viewModels()

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuHomeBinding.inflate(inflater, container, false)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var passiveIncomeValue: Double?
        var totalIncomeValue: Double?
        var totalExpensesValue: Double?
        var moneyFlowValue: Double?

        setButtonListeners()

        walletViewModel.notArchivedWalletsLiveData.observe(viewLifecycleOwner) { walletList ->
            val balanceValue = walletList.sumOf { it.balance }
            binding.capitalTextView.text = balanceValue.toString()
        }

        incomeHistoryViewModel.allIncomeHistoryWithIncomeGroupAndWalletLiveData.observe(viewLifecycleOwner) { history ->

            passiveIncomeValue = history
                .filter { incomeGroupViewModel.getIncomeGroupByIdNotArchived(it.incomeSubGroup.incomeGroupId).isPassive }
                .sumOf { it.incomeHistory.amount }

            binding.passiveIncomeValueTextView.text = passiveIncomeValue.toString()

            totalIncomeValue = history.sumOf { it.incomeHistory.amount }
            binding.totalIncomeValueTextView.text = totalIncomeValue.toString()

            expenseHistoryViewModel.expenseHistoryLiveData.observe(viewLifecycleOwner) { expenseHistory ->
                totalExpensesValue = expenseHistory.sumOf { it.amount }
                binding.totalExpensesValueTextView.text = totalExpensesValue.toString()

                moneyFlowValue = totalIncomeValue!!.minus(totalExpensesValue!!)
                binding.moneyFlowValueTextView.text = moneyFlowValue.toString()
            }
        }
    }

    private fun setButtonListeners() {
        binding.goToAddIncomeButton.setOnClickListener {
            navigateTo(HomeFragmentDirections.actionNavigationHomeToNavigationAddIncome())
        }

        binding.goToAddExpenseButton.setOnClickListener {
            navigateTo(HomeFragmentDirections.actionNavigationHomeToNavigationAddExpense())
        }

        binding.goToHistoryButton.setOnClickListener {
            navigateTo(HomeFragmentDirections.actionNavigationHomeToNavigationHistory())
        }

        binding.goToAnalyzeButton.setOnClickListener {
            navigateTo(HomeFragmentDirections.actionNavigationHomeToAnalyzeFragment())
        }

        binding.addTransferButton.setOnClickListener {
            navigateTo(HomeFragmentDirections.actionNavigationHomeToTransferFragment())
        }
    }

    private fun navigateTo(action: NavDirections) {
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
