package com.romandevyatov.bestfinance.ui.fragments.settings.groupswithsubgroups.expense

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.romandevyatov.bestfinance.data.entities.relations.ExpenseGroupWithExpenseSubGroups
import com.romandevyatov.bestfinance.databinding.SettingsFragmentExpenseGroupsAndSubGroupsBinding
import com.romandevyatov.bestfinance.ui.adapters.settings.groupswithsubgroups.twoadapters.GroupWithSubgroupsAdapter
import com.romandevyatov.bestfinance.ui.adapters.settings.groupswithsubgroups.twoadapters.SubGroupsAdapter
import com.romandevyatov.bestfinance.ui.adapters.settings.groupswithsubgroups.twoadapters.models.GroupWithSubGroupsItem
import com.romandevyatov.bestfinance.ui.adapters.settings.groupswithsubgroups.twoadapters.models.SubGroupItem
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.GeneralExpenseGroupsAndSubGroupsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExpenseGroupsAndSubGroupsFragment : Fragment() {

    private var _binding: SettingsFragmentExpenseGroupsAndSubGroupsBinding? = null
    private val binding get() = _binding!!

    private val generalGroupsAndSubGroupsViewModel: GeneralExpenseGroupsAndSubGroupsViewModel by viewModels()

    private var groupWithSubGroupsItemMutableList: MutableList<GroupWithSubGroupsItem> = mutableListOf()

    private var adapter: GroupWithSubgroupsAdapter? = null

    private val onSubGroupCheckedImpl = object : SubGroupsAdapter.OnSubGroupCheckedChangeListener {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onSubgroupChecked(subGroupItem: SubGroupItem, isChecked: Boolean) {

            val updatedGroupWithSubGroupsMutableList = groupWithSubGroupsItemMutableList.map { groupWithSubGroups ->
                if (groupWithSubGroups.subgroups.contains(subGroupItem)) {
                    val updatedSubgroup = subGroupItem.copy(isChecked = isChecked)

                    val subGroupsMutableList = groupWithSubGroups.subgroups.toMutableList()

                    val index = subGroupsMutableList.indexOf(subGroupItem)

                    if (index != -1) {
                        subGroupsMutableList[index] = updatedSubgroup
                    }

                    groupWithSubGroups.copy(subgroups = subGroupsMutableList)
                } else {
                    groupWithSubGroups
                }
            }

            groupWithSubGroupsItemMutableList.clear()
            groupWithSubGroupsItemMutableList.addAll(updatedGroupWithSubGroupsMutableList)
            adapter?.submitList(groupWithSubGroupsItemMutableList)

            if (isChecked) {
                generalGroupsAndSubGroupsViewModel.unarchiveExpenseSubGroupById(subGroupItem.id)
            } else {
                generalGroupsAndSubGroupsViewModel.archiveExpenseSubGroup(subGroupItem.name)
            }
        }

        override fun onSubGroupDelete(subGroupItem: SubGroupItem) {
            groupWithSubGroupsItemMutableList.map { groupWithSubGroups ->

                if (groupWithSubGroups.subgroups.contains(subGroupItem)) {
                    val subGroupsMutableList = groupWithSubGroups.subgroups.toMutableList()

                    val index = subGroupsMutableList.indexOf(subGroupItem)

                    if (index != -1) {
                        subGroupsMutableList.removeAt(index)
                        adapter?.removeItem(groupWithSubGroups)
                    }
                }
            }

            generalGroupsAndSubGroupsViewModel.deleteExpenseSubGroupById(subGroupItem.id)
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
                    generalGroupsAndSubGroupsViewModel.unarchiveExpenseGroupById(groupWithSubGroupsItem.id)
                } else {
                    generalGroupsAndSubGroupsViewModel.archiveExpenseGroupById(groupWithSubGroupsItem.id!!)
                }
            }
        }

        override fun onGroupDelete(groupWithSubGroupsItem: GroupWithSubGroupsItem) {
            val index = groupWithSubGroupsItemMutableList.indexOf(groupWithSubGroupsItem)

            if (index != -1) {
                groupWithSubGroupsItemMutableList.removeAt(index)
                adapter?.removeItem(groupWithSubGroupsItem)

                generalGroupsAndSubGroupsViewModel.deleteExpenseGroupById(groupWithSubGroupsItem.id)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SettingsFragmentExpenseGroupsAndSubGroupsBinding.inflate(inflater, container, false)

        setupRecyclerView()

        generalGroupsAndSubGroupsViewModel.allExpenseGroupsWithExpenseSubGroupsLiveData?.observe(viewLifecycleOwner) { allGroupsWithSubGroups ->
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

    private fun updateGroupWithSubGroupsList(groupsWithSubGroups: List<ExpenseGroupWithExpenseSubGroups>) {
        groupWithSubGroupsItemMutableList.clear()

        for (groupWithSubGroup in groupsWithSubGroups) {
            val subGroupsForAdapterItem = groupWithSubGroup.expenseSubGroups.map {
                SubGroupItem(it.id, it.name, false, it.archivedDate == null)
            }.toMutableList()

            if (subGroupsForAdapterItem.isNotEmpty()) {
                groupWithSubGroupsItemMutableList.add(
                    GroupWithSubGroupsItem(
                        groupWithSubGroup.expenseGroup.id,
                        groupWithSubGroup.expenseGroup.name,
                        groupWithSubGroup.expenseGroup.archivedDate == null,
                        subGroupsForAdapterItem
                    )
                )
            }
        }
    }
}
