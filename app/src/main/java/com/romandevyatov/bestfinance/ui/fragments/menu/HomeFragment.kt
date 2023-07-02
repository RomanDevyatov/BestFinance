package com.romandevyatov.bestfinance.ui.fragments.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.FragmentHomeBinding
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.ExpenseHistoryViewModel
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.IncomeHistoryViewModel
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.WalletViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val walletViewModel: WalletViewModel by viewModels()
    private val incomeHistoryViewModel: IncomeHistoryViewModel by viewModels()
    private val expenseHistoryViewModel: ExpenseHistoryViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
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

        binding.goToAnalyzeButton.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionNavigationHomeToAnalyzeFragment())
        }

        binding.addTransferButton.setOnClickListener {
            val action = HomeFragmentDirections.actionNavigationHomeToTransferFragment()
            findNavController().navigate(action)
        }

        walletViewModel.notArchivedWalletsLiveData.observe(viewLifecycleOwner) { walletList ->
            binding.capitalTextView.text = walletList.sumOf { it.balance }.toString()
        }

        incomeHistoryViewModel.incomeHistoryLiveData.observe(viewLifecycleOwner) { history ->
            passiveIncomeValue = (history.filter { it.incomeSubGroupId == 3L}).sumOf { it.amount }
            binding.passiveIncomeValueTextView.text = passiveIncomeValue.toString()

            totalIncomeValue = history.sumOf { it.amount }
            binding.totalIncomeValueTextView.text = totalIncomeValue.toString()

            expenseHistoryViewModel.expenseHistoryLiveData.observe(viewLifecycleOwner) { expenseHistory ->
                totalExpensesValue = expenseHistory.sumOf { it.amount }
                binding.totalExpensesValueTextView.text = totalExpensesValue.toString()

                moneyFlowValue = totalIncomeValue!!.minus(totalExpensesValue!!)
                binding.moneyFlowValueTextView.text = moneyFlowValue.toString()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
