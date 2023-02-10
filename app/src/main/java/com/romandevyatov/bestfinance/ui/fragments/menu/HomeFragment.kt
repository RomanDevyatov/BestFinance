package com.romandevyatov.bestfinance.ui.fragments.menu

import android.os.Bundle
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
import com.romandevyatov.bestfinance.viewmodels.WalletViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val walletViewModel: WalletViewModel by viewModels()

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

        val passiveIncomeValue = null
        binding.passiveIncomeValueTextView.text = passiveIncomeValue

        val totalIncomeValue = null
        binding.totalIncomeValueTextView.text = totalIncomeValue

        val totalExpensesValue = null
        binding.totalExpensesValueTextView.text = totalExpensesValue

        val moneyFlowValue = null
        binding.moneyFlowValueTextView.text = moneyFlowValue


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


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}