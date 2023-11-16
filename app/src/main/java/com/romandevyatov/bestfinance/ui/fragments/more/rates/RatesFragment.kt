package com.romandevyatov.bestfinance.ui.fragments.more.rates

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.romandevyatov.bestfinance.databinding.FragmentRatesBinding
import com.romandevyatov.bestfinance.ui.adapters.rates.CurrencyExchangeListAdapter
import com.romandevyatov.bestfinance.utils.BackStackLogger
import com.romandevyatov.bestfinance.viewmodels.ExchangeRatesViewModel
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.BaseCurrencyRatesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RatesFragment : Fragment() {

    private var _binding: FragmentRatesBinding? = null
    private val binding get() = _binding!!

    private val exchangeRatesViewModel: ExchangeRatesViewModel by viewModels()
    private val baseCurrencyRatesViewModel: BaseCurrencyRatesViewModel by viewModels()

    private val currencyRateAdapter = CurrencyExchangeListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRatesBinding.inflate(inflater, container, false)

        BackStackLogger.logBackStack(findNavController())

        setupRecyclerView()

        binding.fetchRatesButton.setOnClickListener {
            exchangeRatesViewModel.fetchExchangeRates()
        }

        observeSavedRates()

        observeRefreshRates()

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = currencyRateAdapter
    }

    private fun observeSavedRates() {
        baseCurrencyRatesViewModel.allBaseCurrencyRate.observe(viewLifecycleOwner) { savedBaseCurrencyRatesList ->
            savedBaseCurrencyRatesList?.let {
                val currencyExchangeRateList = baseCurrencyRatesViewModel.mapToCurrencyExchangeRateItemList(it)
                currencyRateAdapter.submitList(currencyExchangeRateList)
            }
        }
    }

    private fun observeRefreshRates() {
        exchangeRatesViewModel.exchangeRates.observe(viewLifecycleOwner) { ratesMap ->
            ratesMap?.let { rates ->
                val ratesToSave = baseCurrencyRatesViewModel.mapToBaseCurrencyExchangeRates(rates)
                baseCurrencyRatesViewModel.deleteAllAndInsert(ratesToSave)
            }
        }
    }
}
