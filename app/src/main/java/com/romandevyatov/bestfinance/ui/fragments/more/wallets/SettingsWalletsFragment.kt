package com.romandevyatov.bestfinance.ui.fragments.more.wallets

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.FragmentSettingsWalletsBinding
import com.romandevyatov.bestfinance.ui.adapters.more.settings.settingswallets.SettingsWalletsAdapter
import com.romandevyatov.bestfinance.ui.adapters.more.settings.settingswallets.models.SettingsWalletItem
import com.romandevyatov.bestfinance.utils.BackStackLogger
import com.romandevyatov.bestfinance.utils.Constants
import com.romandevyatov.bestfinance.utils.TextFormatter.removeTrailingZeros
import com.romandevyatov.bestfinance.utils.WindowUtil
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.SettingsWalletsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsWalletsFragment : Fragment() {

    private var _binding: FragmentSettingsWalletsBinding? = null
    private val binding get() = _binding!!

    private val settingsWalletsViewModel: SettingsWalletsViewModel by viewModels()

    private lateinit var settingsWalletsAdapter: SettingsWalletsAdapter
    private val settingsWalletItemMutableList = mutableListOf<SettingsWalletItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsWalletsBinding.inflate(inflater, container, false)

        setupRecyclerView()
        observeWallets()

        BackStackLogger.logBackStack(findNavController())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOnBackPressedHandler()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        settingsWalletsAdapter = SettingsWalletsAdapter(
            walletItemCheckedChangeListener,
            walletItemDeleteListener,
            walletItemClickedListener)
        binding.walletRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.walletRecyclerView.adapter = settingsWalletsAdapter
    }

    private fun observeWallets() {
        settingsWalletsViewModel.allWalletsLiveData.observe(viewLifecycleOwner) { allWallets ->
            allWallets?.let { wallets ->

                settingsWalletItemMutableList.clear()
                settingsWalletItemMutableList.addAll(
                    wallets.map {
                        SettingsWalletItem(
                            it.id,
                            it.name,
                            removeTrailingZeros(it.balance.toString()) + settingsWalletsViewModel.currentDefaultCurrencySymbol,
                            it.archivedDate == null)
                    }.toMutableList()
                )

                settingsWalletsAdapter.submitList(settingsWalletItemMutableList.toList())
            }
        }
    }

    private val walletItemCheckedChangeListener = object : SettingsWalletsAdapter.OnWalletItemCheckedChangeListener {

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onWalletChecked(settingsWalletItem: SettingsWalletItem, isChecked: Boolean) {
            updateWalletChecked(settingsWalletItem, isChecked)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateWalletChecked(settingsWalletItem: SettingsWalletItem, isChecked: Boolean) {
        val position = settingsWalletItemMutableList.indexOf(settingsWalletItem)
        if (position != -1) {
            settingsWalletItemMutableList[position] = settingsWalletItem.copy(isExist = isChecked)
            settingsWalletsAdapter.submitList(settingsWalletItemMutableList.toList())
            handleWalletCheckedChange(settingsWalletItem, isChecked)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleWalletCheckedChange(settingsWalletItem: SettingsWalletItem, isChecked: Boolean) {
        if (isChecked) {
            settingsWalletsViewModel.unarchiveWalletById(settingsWalletItem.id)
        } else {
            settingsWalletsViewModel.archiveWalletById(settingsWalletItem.id!!)
        }
    }

    private val walletItemDeleteListener = object : SettingsWalletsAdapter.OnWalletItemDeleteListener {
        override fun onWalletItemDelete(settingsWalletItem: SettingsWalletItem) {
            settingsWalletItem.id?.let { id ->
                WindowUtil.showDeleteDialog(
                    context = requireContext(),
                    viewModel = settingsWalletsViewModel,
                    message = getString(R.string.delete_confirmation_warning_message, settingsWalletItem.name),
                    isCountdown = true,
                    itemId = id,
                    rootView = binding.root
                ) { }
            }
        }
    }

    private val walletItemClickedListener = object : SettingsWalletsAdapter.OnWalletItemClickedListener {
        override fun navigateToUpdateWallet(wallet: SettingsWalletItem) {
            val action =
                SettingsWalletsFragmentDirections.actionWalletsSettingsFragmentToUpdateWalletFragment()
            action.source = Constants.WALLETS_SETTINGS_FRAGMENT
            action.walletName = wallet.name
            findNavController().navigate(action)
        }
    }

    private fun setOnBackPressedHandler() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.more_fragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
}
