package com.romandevyatov.bestfinance.ui.fragments.more.selectcurrency

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.FragmentSettingsSelectCurrencyBinding
import com.romandevyatov.bestfinance.ui.adapters.currency.CurrencyAdapter
import com.romandevyatov.bestfinance.ui.adapters.currency.CurrencyItem
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.SelectCurrencyViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectCurrencyFragment : Fragment() {

    private var _binding: FragmentSettingsSelectCurrencyBinding? = null
    private val binding get() = _binding!!

    private lateinit var currencyAdapter: CurrencyAdapter

    private val selectCurrencyViewModel: SelectCurrencyViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsSelectCurrencyBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOnBackPressedHandler()

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
    }

    private fun setOnBackPressedHandler() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.home_fragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun initCurrencyRecyclerView() {
        val clickOnWalletListener = object : CurrencyAdapter.ItemClickListener {

            override fun onClick(currencyItem: CurrencyItem) {
                selectCurrencyViewModel.setDefaultCurrencyCode(currencyItem.code)
                val action = SelectCurrencyFragmentDirections.actionSelectCurrencyFragmentToMoreFragment()
                findNavController().navigate(action)
            }
        }

        currencyAdapter = CurrencyAdapter(clickOnWalletListener)

        binding.selectCurrencyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.selectCurrencyRecyclerView.adapter = currencyAdapter
    }
}