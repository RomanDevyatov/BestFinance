package com.romandevyatov.bestfinance.ui.fragments.settings.wallets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.romandevyatov.bestfinance.databinding.FragmentArchivedWalletsBinding
import com.romandevyatov.bestfinance.ui.adapters.settings.deprecated.group.ArchivedGroupsAdapter
import com.romandevyatov.bestfinance.ui.adapters.settings.deprecated.group.model.GroupItem
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.ArchivedWalletsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArchivedWalletsFragment : Fragment() {

    private var _binding: FragmentArchivedWalletsBinding? = null
    private val binding get() = _binding!!

    private val archivedWalletsViewModel: ArchivedWalletsViewModel by viewModels()

    private lateinit var archivedWalletsAdapter: ArchivedGroupsAdapter

    private var walletItemMutableList: MutableList<GroupItem> = mutableListOf()

    private val listener = object : ArchivedGroupsAdapter.OnSubGroupCheckedChangeListener {
        override fun onSubgroupChecked(groupItem: GroupItem) {
            val index = walletItemMutableList.indexOf(groupItem)
            if (index != -1) {
                walletItemMutableList[index] = groupItem
                archivedWalletsAdapter.submitList(walletItemMutableList)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArchivedWalletsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        archivedWalletsViewModel.allWalletsArchivedLiveData?.observe(viewLifecycleOwner) { allWalletsArchived ->
            archivedWalletsAdapter = ArchivedGroupsAdapter(listener)
            binding.walletRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = archivedWalletsAdapter
            }

            walletItemMutableList = allWalletsArchived.map {
                GroupItem(it.id, it.name, false)
            }.toMutableList()

            archivedWalletsAdapter.submitList(walletItemMutableList)
        }

        binding.unarchiveButton.setOnClickListener {
            unarchiveSelectedWallets()
        }

        binding.deleteButton.setOnClickListener {
            deleteSelectedGroups()
        }
    }

    private fun unarchiveSelectedWallets() {
        val selectedItems = archivedWalletsAdapter.getSelectedGroups()

        selectedItems.forEach { selectedItem ->
            archivedWalletsViewModel.getWalletArchivedByNameLiveData(selectedItem.name)?.observe(viewLifecycleOwner) { wallet ->
                if (wallet != null) {
                    archivedWalletsViewModel.unarchiveWallet(wallet)
                }
            }
        }
    }

    private fun deleteSelectedGroups() {
        val selectedItems = archivedWalletsAdapter.getSelectedGroups()

        selectedItems.forEach { group ->
            archivedWalletsViewModel.deleteWalletById(group.id)
        }
    }
}