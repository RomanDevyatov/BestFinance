package com.romandevyatov.bestfinance.ui.fragments.settings.groupswithsubgroups.income

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.romandevyatov.bestfinance.data.entities.relations.IncomeGroupWithIncomeSubGroups
import com.romandevyatov.bestfinance.databinding.SettingsFragmentIncomeGroupsAndSubGroupsBinding
import com.romandevyatov.bestfinance.ui.adapters.settings.groupswithsubgroups.twoadapters.GroupWithSubgroupsAdapter
import com.romandevyatov.bestfinance.ui.adapters.settings.groupswithsubgroups.twoadapters.SubGroupsAdapter
import com.romandevyatov.bestfinance.ui.adapters.settings.groupswithsubgroups.twoadapters.models.GroupWithSubGroupsItem
import com.romandevyatov.bestfinance.ui.adapters.settings.groupswithsubgroups.twoadapters.models.SubGroupItem
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.GeneralIncomeGroupsAndSubGroupsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IncomeGroupsAndSubGroupsFragment : Fragment() {

    private var _binding: SettingsFragmentIncomeGroupsAndSubGroupsBinding? = null
    private val binding get() = _binding!!

    private val generalGroupsAndSubGroupsViewModel: GeneralIncomeGroupsAndSubGroupsViewModel by viewModels()

    private var groupWithSubGroupsItemMutableList: MutableList<GroupWithSubGroupsItem> = mutableListOf()

    private var adapter: GroupWithSubgroupsAdapter? = null

    private val onSubGroupCheckedImpl = object : SubGroupsAdapter.OnSubGroupCheckedChangeListener {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onSubgroupChecked(subGroupItem: SubGroupItem, isChecked: Boolean) {
            val updatedGroupWithSubGroupsMutableList = groupWithSubGroupsItemMutableList.map { groupWithSubGroups ->
                if (groupWithSubGroups.subgroups.contains(subGroupItem)) {
                    val updatedSubGroup = subGroupItem.copy(isChecked = isChecked)

                    val subGroupsMutableList = groupWithSubGroups.subgroups

                    val index = subGroupsMutableList.indexOf(subGroupItem)

                    if (index != -1) {
                        subGroupsMutableList[index] = updatedSubGroup
                    }

                    groupWithSubGroups.copy(subgroups = subGroupsMutableList)
                } else {
                    groupWithSubGroups
                }
            }.toMutableList()

            groupWithSubGroupsItemMutableList.clear()
            groupWithSubGroupsItemMutableList.addAll(updatedGroupWithSubGroupsMutableList)
            adapter?.submitList(groupWithSubGroupsItemMutableList)

            if (isChecked) {
                 generalGroupsAndSubGroupsViewModel.unarchiveIncomeSubGroupById(subGroupItem.id)
            } else {
                 generalGroupsAndSubGroupsViewModel.archiveIncomeSubGroup(subGroupItem.name)
            }
        }

        override fun onSubGroupDelete(subGroupItem: SubGroupItem) {
            groupWithSubGroupsItemMutableList.map { groupWithSubGroups ->

                if (groupWithSubGroups.subgroups.contains(subGroupItem)) {
                    val subGroupsMutableList = groupWithSubGroups.subgroups.toMutableList()

                    val index = subGroupsMutableList.indexOf(subGroupItem)

                    if (index != -1) {
                        subGroupsMutableList.removeAt(index)
                        adapter?.removeSubItem(subGroupItem)
                    }
                }
            }

            generalGroupsAndSubGroupsViewModel.deleteIncomeSubGroupById(subGroupItem.id)
        }
    }

    private val onGroupCheckedImpl = object : GroupWithSubgroupsAdapter.OnGroupCheckedChangeListener {

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onGroupChecked(groupWithSubGroupsItem: GroupWithSubGroupsItem, isChecked: Boolean) {
            val index = groupWithSubGroupsItemMutableList.indexOf(groupWithSubGroupsItem)
            if (index != -1) {
                val updatedGroup = groupWithSubGroupsItem.copy(isArchived = isChecked)
                groupWithSubGroupsItemMutableList[index] = updatedGroup

                adapter?.submitList(groupWithSubGroupsItemMutableList)

                if (isChecked) {
                    generalGroupsAndSubGroupsViewModel.unarchiveIncomeGroupById(groupWithSubGroupsItem.id)
                } else {
                     generalGroupsAndSubGroupsViewModel.archiveIncomeGroupById(groupWithSubGroupsItem.id!!)
                }
            }
        }

        override fun onGroupDelete(groupWithSubGroupsItem: GroupWithSubGroupsItem) {
            val index = groupWithSubGroupsItemMutableList.indexOf(groupWithSubGroupsItem)

            if (index != -1) {
                groupWithSubGroupsItemMutableList.removeAt(index)
                adapter?.removeItem(groupWithSubGroupsItem)

                generalGroupsAndSubGroupsViewModel.deleteIncomeGroupById(groupWithSubGroupsItem.id)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SettingsFragmentIncomeGroupsAndSubGroupsBinding.inflate(inflater, container, false)

        setupRecyclerView()

        generalGroupsAndSubGroupsViewModel.allIncomeGroupsWithIncomeSubGroupsLiveData?.observe(viewLifecycleOwner) { allGroupsWithSubGroups ->
            allGroupsWithSubGroups?.let { groupList ->
                updateGroupWithSubGroupsList(groupList)
                adapter?.submitList(groupWithSubGroupsItemMutableList)
            }
        }

        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = GroupWithSubgroupsAdapter(onGroupCheckedImpl, onSubGroupCheckedImpl)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun updateGroupWithSubGroupsList(groupsWithSubGroups: List<IncomeGroupWithIncomeSubGroups>) {
        groupWithSubGroupsItemMutableList.clear()

        for (groupWithSubGroup in groupsWithSubGroups) {
            val subGroupsForAdapterItem = groupWithSubGroup.incomeSubGroups.map {
                SubGroupItem(it.id, it.name, false, it.archivedDate == null)
            }.toMutableList()

            if (subGroupsForAdapterItem.isNotEmpty()) {
                groupWithSubGroupsItemMutableList.add(
                    GroupWithSubGroupsItem(
                        groupWithSubGroup.incomeGroup.id,
                        groupWithSubGroup.incomeGroup.name,
                        groupWithSubGroup.incomeGroup.archivedDate == null,
                        subGroupsForAdapterItem
                    )
                )
            }
        }
    }
}
