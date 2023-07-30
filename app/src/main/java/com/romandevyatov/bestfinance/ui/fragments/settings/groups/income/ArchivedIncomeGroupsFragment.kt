package com.romandevyatov.bestfinance.ui.fragments.settings.groups.income

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.romandevyatov.bestfinance.databinding.FragmentArchivedIncomeGroupsBinding
import com.romandevyatov.bestfinance.ui.adapters.settings.group.ArchivedGroupsAdapter
import com.romandevyatov.bestfinance.ui.adapters.settings.group.model.GroupItem
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.ArchivedIncomeGroupsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArchivedIncomeGroupsFragment : Fragment() {

    private var _binding: FragmentArchivedIncomeGroupsBinding? = null
    private val binding get() = _binding!!

    private val archivedIncomeGroupsViewModel: ArchivedIncomeGroupsViewModel by viewModels()

    private lateinit var archivedGroupsAdapter: ArchivedGroupsAdapter

    private var groupItemMutableList: MutableList<GroupItem> = mutableListOf()

    private val listener = object : ArchivedGroupsAdapter.OnSubGroupCheckedChangeListener {
        override fun onSubgroupChecked(groupItem: GroupItem) {
            val index = groupItemMutableList.indexOf(groupItem)
            if (index != -1) {
                groupItemMutableList[index] = groupItem
                (binding.recyclerView.adapter as ArchivedGroupsAdapter).submitList(groupItemMutableList)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArchivedIncomeGroupsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        archivedIncomeGroupsViewModel.allIncomeGroupsArchivedLiveData.observe(viewLifecycleOwner) { allIncomeGroupsArchived ->
            archivedGroupsAdapter = ArchivedGroupsAdapter(listener)
            binding.recyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = archivedGroupsAdapter
            }
            groupItemMutableList = allIncomeGroupsArchived.map {
                GroupItem(it.id, it.name, false)
            }.toMutableList()

            archivedGroupsAdapter.submitList(groupItemMutableList)
        }

        binding.unarchiveButton.setOnClickListener {
            unarchiveSelectedGroups()
        }

        binding.deleteButton.setOnClickListener {
            deleteSelectedGroups()
        }
    }

    private fun unarchiveSelectedGroups() {
        val selectedItems = archivedGroupsAdapter.getSelectedGroups()

        selectedItems.forEach { groupItem ->
            archivedIncomeGroupsViewModel.getIncomeGroupsArchivedByNameLiveData(groupItem.name)?.observe(viewLifecycleOwner) { group ->
                if (group != null) {
                    val isIncludeSubGroups = binding.checkBox.isChecked
                    archivedIncomeGroupsViewModel.unarchiveIncomeGroup(group, isIncludeSubGroups)
                }
            }
        }
    }

    private fun deleteSelectedGroups() {
        val selectedItems = archivedGroupsAdapter.getSelectedGroups()

        selectedItems.forEach { groupItem ->
            archivedIncomeGroupsViewModel.deleteIncomeGroupByName(groupItem.id)
        }
    }
}
