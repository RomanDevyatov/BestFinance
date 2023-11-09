package com.romandevyatov.bestfinance.ui.fragments.more.selectcurrency

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.FragmentSettingsSelectCurrencyBinding
import com.romandevyatov.bestfinance.ui.adapters.currency.CurrencyAdapter
import com.romandevyatov.bestfinance.ui.adapters.currency.CurrencyItem
import com.romandevyatov.bestfinance.utils.BackStackLogger
import com.romandevyatov.bestfinance.utils.Constants
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.SelectCurrencyViewModel
import com.romandevyatov.bestfinance.viewmodels.shared.SharedModifiedAddWalletFormViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectCurrencyFragment : Fragment() {

    private var _binding: FragmentSettingsSelectCurrencyBinding? = null
    private val binding get() = _binding!!

    private lateinit var currencyAdapter: CurrencyAdapter

    private val args: SelectCurrencyFragmentArgs by navArgs()

    private val selectCurrencyViewModel: SelectCurrencyViewModel by viewModels()
    private val sharedModifiedAddWalletFormViewModel: SharedModifiedAddWalletFormViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsSelectCurrencyBinding.inflate(inflater, container, false)

        BackStackLogger.logBackStack(findNavController())

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
                performNavigation(args.source, null)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun initCurrencyRecyclerView() {
        val clickOnWalletListener = object : CurrencyAdapter.ItemClickListener {

            override fun onClick(currencyItem: CurrencyItem) {
                performNavigation(args.source, currencyItem)
            }
        }

        currencyAdapter = CurrencyAdapter(clickOnWalletListener)

        binding.selectCurrencyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.selectCurrencyRecyclerView.adapter = currencyAdapter
    }

    private fun performNavigation(prevFragmentString: String?, currencyItem: CurrencyItem?) {
//        val navController = findNavController()
//        if (navController.popBackStack()) {
//            val previousBackStackEntry = navController.previousBackStackEntry
//            // Now you can access information about the previous destination
//            val previousDestinationId = previousBackStackEntry?.destination?.id
//            if (previousDestinationId != null) {
//                navController.navigate(previousDestinationId)
//            }
//        }

        when (prevFragmentString) {
            Constants.ADD_WALLET_FRAGMENT -> {
                val mod = sharedModifiedAddWalletFormViewModel.modelForm
                val addWalletForm = mod?.copy(
                    currencyCode = currencyItem?.code
                )
                sharedModifiedAddWalletFormViewModel.modelForm = addWalletForm

                findNavController().popBackStack(R.id.add_wallet_fragment, false)
            }
            else -> {
                if (currencyItem != null) {
                    selectCurrencyViewModel.setDefaultCurrencyCode(currencyItem.code)
                }
                findNavController().popBackStack(R.id.more_fragment, false)
            }
        }
    }

}
