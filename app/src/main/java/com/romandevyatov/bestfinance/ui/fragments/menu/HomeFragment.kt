package com.romandevyatov.bestfinance.ui.fragments.menu

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.FragmentBottomMenuHomeBinding
import com.romandevyatov.bestfinance.ui.activity.OnExitAppListener
import com.romandevyatov.bestfinance.utils.BackStackLogger
import com.romandevyatov.bestfinance.utils.Constants
import com.romandevyatov.bestfinance.utils.TextFormatter.removeTrailingZeros
import com.romandevyatov.bestfinance.utils.TextFormatter.roundDoubleToTwoDecimalPlaces
import com.romandevyatov.bestfinance.viewmodels.ExchangeRatesViewModel
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.HomeViewModel
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.BaseCurrencyRatesViewModel
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.ExpenseHistoryViewModel
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.IncomeHistoryViewModel
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.WalletViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.absoluteValue

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentBottomMenuHomeBinding? = null
    private val binding get() = _binding!!

    private val walletViewModel: WalletViewModel by viewModels()
    private val incomeHistoryViewModel: IncomeHistoryViewModel by viewModels()
    private val expenseHistoryViewModel: ExpenseHistoryViewModel by viewModels()
    private val exchangeRatesViewModel: ExchangeRatesViewModel by viewModels()
    private val baseCurrencyRatesViewModel: BaseCurrencyRatesViewModel by viewModels()

    private val homeViewModel: HomeViewModel by viewModels()

    private var isSingleBackClicked = false

    private var exitAppListener: OnExitAppListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnExitAppListener) {
            exitAppListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        exitAppListener = null
    }

    private fun exitApp() {
        exitAppListener?.onExitApp()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomMenuHomeBinding.inflate(inflater, container, false)

        setOnBackPressedHandler()

        if (homeViewModel.getIsFirstLaunch()) {
            homeViewModel.initializeCurrencyData()
            homeViewModel.setIsFirstLaunch(false)
            exchangeRatesViewModel.fetchExchangeRates()
        }

        exchangeRatesViewModel.exchangeRates.observe(viewLifecycleOwner) { ratesMap ->
            ratesMap.let { rates ->
                val ratesToSave = baseCurrencyRatesViewModel.mapToBaseCurrencyExchangeRates(rates)

                baseCurrencyRatesViewModel.deleteAll()
                baseCurrencyRatesViewModel.insertAllBaseCurrencyRates(ratesToSave)
            }
        }

        walletViewModel.allWalletsNotArchivedLiveData.observe(viewLifecycleOwner) { walletList ->
            var balanceValue = walletList.sumOf { wallet ->
                val pairName = "${homeViewModel.getDefaultCurrencyCode()}${wallet.currencyCode}"

                val baseRate = homeViewModel.getBaseCurrencyRatesByPairName(pairName)
                    if (baseRate != null) {
                        wallet.balance / baseRate.value
                    }
                    else {
                        0.0
                    }
                }



            balanceValue = roundDoubleToTwoDecimalPlaces(balanceValue)

            val totalCapitalText = removeTrailingZeros(balanceValue.toString()) + homeViewModel.getDefaultCurrencySymbol()
            Log.d("HomeFragment", "totalCapitalText: $totalCapitalText")
            binding.totalCapitalTextView.text = totalCapitalText

            incomeHistoryViewModel.allIncomeHistoryWithIncomeSubGroupAndWalletLiveData.observe(viewLifecycleOwner) { incomeHistoryWithIncomeSubGroupAndWallets ->
                homeViewModel.incomeGroupsLiveDataEntity.observe(viewLifecycleOwner) { incomeGroups ->
                    val passiveIncomeValue = incomeHistoryWithIncomeSubGroupAndWallets
                        .filter { historyWithSubGroupAndWallets ->
                            incomeGroups.find {
                                it.id == historyWithSubGroupAndWallets.incomeSubGroup?.incomeGroupId
                            }?.isPassive ?: false
                        }
                        .sumOf { it.incomeHistoryEntity.amountBase }

                    val passiveIncomeText =
                        removeTrailingZeros(passiveIncomeValue.toString()).plus(homeViewModel.getDefaultCurrencySymbol())
                    Log.d("HomeFragment", "passiveIncomeText: $passiveIncomeText")
                    binding.passiveIncomeValueTextView.text = passiveIncomeText
                }

                val totalIncomeValue = roundDoubleToTwoDecimalPlaces(incomeHistoryWithIncomeSubGroupAndWallets.sumOf { it.incomeHistoryEntity.amountBase })
                val totalIncomeText = removeTrailingZeros(totalIncomeValue.toString()) + homeViewModel.getDefaultCurrencySymbol()
                Log.d("HomeFragment", "totalIncomeText: $totalIncomeText")
                binding.totalIncomeValueTextView.text = totalIncomeText

                expenseHistoryViewModel.expenseHistoryEntityListLiveData.observe(viewLifecycleOwner) { expenseHistoryList ->
                    expenseHistoryList?.let { histories ->
                        val totalExpensesValue = roundDoubleToTwoDecimalPlaces(histories.sumOf { it.amountBase })
                        val totalExpensesText = removeTrailingZeros(totalExpensesValue.toString()) + homeViewModel.getDefaultCurrencySymbol()
                        Log.d("HomeFragment", "totalExpensesText: $totalExpensesText")
                        binding.totalExpensesValueTextView.text = totalExpensesText

                        val moneyFlowValue = roundDoubleToTwoDecimalPlaces(totalIncomeValue - totalExpensesValue.absoluteValue)
                        val moneyFlowText = removeTrailingZeros(moneyFlowValue.toString()) + homeViewModel.getDefaultCurrencySymbol()

                        Log.d("HomeFragment", "totalIncomeValue: $totalIncomeValue")
                        Log.d("HomeFragment", "totalExpensesValue: $totalExpensesValue")
                        Log.d("HomeFragment", "moneyFlowValue: $moneyFlowValue")

                        Log.d("HomeFragment", "moneyFlowText: $moneyFlowText")

                        binding.moneyFlowValueTextView.text = moneyFlowText
                    }
                }
            }
        }

        BackStackLogger.logBackStack(findNavController())

        return binding.root
    }

    private fun setOnBackPressedHandler() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isSingleBackClicked) {
                    return
                }

                isSingleBackClicked = true
                Toast.makeText(requireContext(), R.string.double_back_to_exit, Toast.LENGTH_SHORT).show()

                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    exitApp()
                    isSingleBackClicked = false
                }, Constants.CLICK_DELAY_MS)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setButtonListeners()

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
