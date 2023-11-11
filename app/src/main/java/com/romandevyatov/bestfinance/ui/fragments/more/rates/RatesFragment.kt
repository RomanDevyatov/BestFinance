package com.romandevyatov.bestfinance.ui.fragments.more.rates

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.romandevyatov.bestfinance.data.entities.BaseCurrencyRate
import com.romandevyatov.bestfinance.databinding.FragmentRatesBinding
import com.romandevyatov.bestfinance.ui.adapters.rates.CurrencyExchangeListAdapter
import com.romandevyatov.bestfinance.ui.adapters.rates.CurrencyExchangeRateItem
import com.romandevyatov.bestfinance.utils.BackStackLogger
import com.romandevyatov.bestfinance.viewmodels.ExchangeRatesViewModel
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.RatesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RatesFragment : Fragment() {

    private var _binding: FragmentRatesBinding? = null
    private val binding get() = _binding!!

    private val exchangeRatesViewModel: ExchangeRatesViewModel by viewModels()
    private val ratesViewModel: RatesViewModel by viewModels()

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
            exchangeRatesViewModel.fetchExchangeRates()
        }

        setRefreshRatesObservable()

        setSavedRatesObservable()

        return binding.root
    }

    private fun setSavedRatesObservable() {
        ratesViewModel.allBaseCurrencyRate.observe(viewLifecycleOwner) { savedBaseCurrencyRatesList ->
            savedBaseCurrencyRatesList.let {
                val currencyExchangeRateList = mapToCurrencyExchangeRateItemList(it)
                currencyRateAdapter.submitList(currencyExchangeRateList)
            }
        }
    }

    private fun setRefreshRatesObservable() {
        exchangeRatesViewModel.exchangeRates.observe(viewLifecycleOwner) { ratesMap ->
            ratesMap.let { rates ->
                val ratesToSave = mapToBaseCurrencyExchangeRates(rates)

                ratesViewModel.deleteAll()
                ratesViewModel.insertAllBaseCurrencyRates(ratesToSave)
            }
        }
    }

    private fun mapToBaseCurrencyExchangeRates(exchangeRates: Map<String, Double>?): MutableList<BaseCurrencyRate> {
        val currencyExchangeRates = mutableListOf<BaseCurrencyRate>()

        val defaultCurrencySymbol = exchangeRatesViewModel.getDefaultCurrencyCode()

        exchangeRates?.forEach { (currencyCode, exchangeRate) ->
            val currencyExchangeRate = BaseCurrencyRate(
                pairName = defaultCurrencySymbol + currencyCode,
                value = exchangeRate
            )
            currencyExchangeRates.add(currencyExchangeRate)
        }

        return currencyExchangeRates
    }

    private fun mapToCurrencyExchangeRateItemList(exchangeRates: List<BaseCurrencyRate>?): MutableList<CurrencyExchangeRateItem> {
        val currencyExchangeRateItems = mutableListOf<CurrencyExchangeRateItem>()

        exchangeRates?.forEach { it ->
            val currencyExchangeRateItem = CurrencyExchangeRateItem(it.pairName, it.value)
            currencyExchangeRateItems.add(currencyExchangeRateItem)
        }

        return currencyExchangeRateItems
    }

}
