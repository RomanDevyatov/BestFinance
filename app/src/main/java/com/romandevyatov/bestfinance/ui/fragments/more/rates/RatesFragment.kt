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
import com.romandevyatov.bestfinance.ui.adapters.rates.CurrencyExchangeRate
import com.romandevyatov.bestfinance.utils.BackStackLogger
import com.romandevyatov.bestfinance.viewmodels.ExchangeRatesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RatesFragment : Fragment() {

    private var _binding: FragmentRatesBinding? = null
    private val binding get() = _binding!!

    private val exchangeRatesViewModel: ExchangeRatesViewModel by viewModels()

    private val currencyRateAdapter = CurrencyExchangeListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRatesBinding.inflate(inflater, container, false)

        BackStackLogger.logBackStack(findNavController())

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = currencyRateAdapter

        binding.fetchRatesButton.setOnClickListener {
            // Fetch exchange rates when the button is clicked
            exchangeRatesViewModel.fetchExchangeRates()
        }

        exchangeRatesViewModel.exchangeRates.observe(viewLifecycleOwner) { ratesMap ->
            ratesMap.let { rates ->
                val currencyExchangeRates = mapToCurrencyExchangeRates(rates)
                currencyRateAdapter.submitList(currencyExchangeRates)
            }
        }

        return binding.root
    }

    private fun mapToCurrencyExchangeRates(exchangeRates: Map<String, Double>?): MutableList<CurrencyExchangeRate> {
        val currencyExchangeRates = mutableListOf<CurrencyExchangeRate>()

        exchangeRates?.forEach { (currencyCode, exchangeRate) ->
            val currencyExchangeRate = CurrencyExchangeRate(currencyCode, exchangeRate)
            currencyExchangeRates.add(currencyExchangeRate)
        }

        return currencyExchangeRates
    }


}