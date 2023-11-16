package com.romandevyatov.bestfinance.ui.fragments.more.selectcurrency

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.data.entities.BaseCurrencyRateEntity
import com.romandevyatov.bestfinance.databinding.FragmentSettingsSelectCurrencyBinding
import com.romandevyatov.bestfinance.ui.adapters.currency.CurrencyAdapter
import com.romandevyatov.bestfinance.ui.adapters.currency.CurrencyItem
import com.romandevyatov.bestfinance.utils.BackStackLogger
import com.romandevyatov.bestfinance.utils.Constants.ADD_WALLET_FRAGMENT
import com.romandevyatov.bestfinance.utils.Constants.MORE_FRAGMENT
import com.romandevyatov.bestfinance.viewmodels.ExchangeRatesViewModel
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.BaseCurrencyRatesViewModel
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.SelectCurrencyViewModel
import com.romandevyatov.bestfinance.viewmodels.shared.SharedModifiedAddWalletFormViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SelectCurrencyFragment : Fragment() {

    private var _binding: FragmentSettingsSelectCurrencyBinding? = null
    private val binding get() = _binding!!

    private lateinit var currencyAdapter: CurrencyAdapter

    private val args: SelectCurrencyFragmentArgs by navArgs()

    private val selectCurrencyViewModel: SelectCurrencyViewModel by viewModels()
    private val baseCurrencyRatesViewModel: BaseCurrencyRatesViewModel by viewModels()
    private val exchangeRatesViewModel: ExchangeRatesViewModel by viewModels()
    private val sharedModifiedAddWalletFormViewModel: SharedModifiedAddWalletFormViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsSelectCurrencyBinding.inflate(inflater, container, false)


        BackStackLogger.logBackStack(findNavController())

        selectCurrencyViewModel.exchangeRates.observe(viewLifecycleOwner) { ratesMap ->
            ratesMap.let { rates ->
                val ratesToSave = mapToBaseCurrencyExchangeRates(rates)

                baseCurrencyRatesViewModel.deleteAll()
                baseCurrencyRatesViewModel.insertAllBaseCurrencyRates(ratesToSave)
            }
        }

        selectCurrencyViewModel.allCurrenciesLiveData.observe(viewLifecycleOwner) { currencyList ->
            currencyList?.map { CurrencyItem(it.code, it.name) }?.toMutableList()?.let { currencyItems ->
                val spinnerCurrencyItems: MutableList<CurrencyItem> = mutableListOf()

                currencyItems.let {
                    spinnerCurrencyItems.addAll(currencyItems)
                }

                currencyAdapter.submitList(spinnerCurrencyItems)
            }
        }

        initCurrencyRecyclerView()

        selectCurrencyViewModel.circleProgress.observe(viewLifecycleOwner) { progress ->
            binding.loadingProgressCircle.progress = progress
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOnBackPressedHandler()
    }

    private fun setOnBackPressedHandler() {
        val callback = object : OnBackPressedCallback(true) {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun handleOnBackPressed() {
                performNavigation(args.source, null)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun initCurrencyRecyclerView() {
        val clickOnWalletListener = object : CurrencyAdapter.ItemClickListener {

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onClick(currencyItem: CurrencyItem) {
                performNavigation(args.source, currencyItem)
            }
        }

        currencyAdapter = CurrencyAdapter(clickOnWalletListener)

        binding.selectCurrencyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.selectCurrencyRecyclerView.adapter = currencyAdapter
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun performNavigation(prevFragmentString: String?, currencyItem: CurrencyItem?) {
        when (prevFragmentString) {
            ADD_WALLET_FRAGMENT -> {
                val mod = sharedModifiedAddWalletFormViewModel.modelForm
                val addWalletForm = mod?.copy(
                    currencyCode = currencyItem?.code
                )
                sharedModifiedAddWalletFormViewModel.modelForm = addWalletForm

                findNavController().popBackStack(R.id.add_wallet_fragment, false)
            }
            MORE_FRAGMENT -> {
                lifecycleScope.launch {
                    try {
                        if (currencyItem != null) {
                            binding.selectCurrencyRecyclerView.visibility = View.GONE
                            binding.loadingProgressCircle.visibility = View.VISIBLE
                            selectCurrencyViewModel.recalculateBaseAmountForHistory(currencyItem.code)
                            binding.loadingProgressCircle.visibility = View.GONE
                        }
                        findNavController().popBackStack(R.id.more_fragment, false)
                    } catch (e: Exception) {
                        Log.e("tag", "Error during recalculation: $e")
                        // Handle the error here or display a message to the user
                    }

                }
            }
        }
    }

    private fun mapToBaseCurrencyExchangeRates(exchangeRates: Map<String, Double>?): MutableList<BaseCurrencyRateEntity> {
        val currencyExchangeRates = mutableListOf<BaseCurrencyRateEntity>()

        val defaultCurrencySymbol = exchangeRatesViewModel.getDefaultCurrencyCode()

        exchangeRates?.forEach { (currencyCode, exchangeRate) ->
            val currencyExchangeRate = BaseCurrencyRateEntity(
                pairName = defaultCurrencySymbol + currencyCode,
                value = exchangeRate
            )
            currencyExchangeRates.add(currencyExchangeRate)
        }

        return currencyExchangeRates
    }

}
