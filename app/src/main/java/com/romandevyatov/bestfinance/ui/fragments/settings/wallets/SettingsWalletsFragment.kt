package com.romandevyatov.bestfinance.ui.fragments.settings.wallets

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.romandevyatov.bestfinance.data.entities.Wallet
import com.romandevyatov.bestfinance.databinding.FragmentSettingsWalletsBinding
import com.romandevyatov.bestfinance.ui.adapters.settings.wallets.WalletsAdapter
import com.romandevyatov.bestfinance.ui.adapters.settings.wallets.models.WalletItem
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.SettingsWalletsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsWalletsFragment : Fragment() {

    private var _binding: FragmentSettingsWalletsBinding? = null
    private val binding get() = _binding!!

    private val settingsWalletsViewModel: SettingsWalletsViewModel by viewModels()

    private lateinit var walletsAdapter: WalletsAdapter
    private val walletItemMutableList = mutableListOf<WalletItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsWalletsBinding.inflate(inflater, container, false)

        setupRecyclerView()
        observeWallets()

        return binding.root
    }

    private fun setupRecyclerView() {
        walletsAdapter = WalletsAdapter(walletItemCheckedChangeListener, walletItemDeleteListener)

        binding.walletRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.walletRecyclerView.adapter = walletsAdapter
    }

    private fun observeWallets() {
        settingsWalletsViewModel.allWalletsLiveData?.observe(viewLifecycleOwner) { allWallets ->
            allWallets?.let { wallets ->
                walletItemMutableList.clear()
                walletItemMutableList.addAll(
                    wallets.map {
                        WalletItem(it.id, it.name, it.archivedDate == null)
                    }.toMutableList()
                )

                walletsAdapter.submitList(walletItemMutableList)
            }
        }
    }

    private val walletItemCheckedChangeListener = object : WalletsAdapter.OnWalletItemCheckedChangeListener {

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onWalletChecked(walletItem: WalletItem, isChecked: Boolean) {
            updateWalletChecked(walletItem, isChecked)
            handleWalletCheckedChange(walletItem, isChecked)
        }
    }

    private fun updateWalletChecked(walletItem: WalletItem, isChecked: Boolean) {
        val updatedWallets = walletItemMutableList.map {
            if (it == walletItem) {
                walletItem.copy(isExist = isChecked)
            } else {
                it
            }
        }.toMutableList()

        walletItemMutableList.clear()
        walletItemMutableList.addAll(updatedWallets)
        walletsAdapter.submitList(updatedWallets)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleWalletCheckedChange(walletItem: WalletItem, isChecked: Boolean) {
        if (isChecked) {
            settingsWalletsViewModel.unarchiveWalletById(walletItem.id)
        } else {
            settingsWalletsViewModel.archiveWalletById(walletItem.id!!)
        }
    }

    private val walletItemDeleteListener = object : WalletsAdapter.OnWalletItemDeleteListener {
        override fun onWalletItemDelete(walletItem: WalletItem) {
            walletItemMutableList.remove(walletItem)
            walletsAdapter.submitList(walletItemMutableList)
            settingsWalletsViewModel.deleteWalletById(walletItem.id)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
