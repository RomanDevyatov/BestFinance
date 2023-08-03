package com.romandevyatov.bestfinance.ui.fragments.settings.general.income

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
import com.romandevyatov.bestfinance.databinding.FragmentGeneralIncomeGroupsAndSubGroupsBinding
import com.romandevyatov.bestfinance.ui.adapters.settings.generalcategories.recyclerviewapproach.twoadapters.models.GroupWithSubGroups
import com.romandevyatov.bestfinance.ui.adapters.settings.generalcategories.recyclerviewapproach.twoadapters.GroupWithSubgroupsAdapter
import com.romandevyatov.bestfinance.ui.adapters.settings.generalcategories.recyclerviewapproach.twoadapters.models.SubGroup
import com.romandevyatov.bestfinance.ui.adapters.settings.generalcategories.recyclerviewapproach.twoadapters.SubGroupsAdapter
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.GeneralIncomeGroupsAndSubGroupsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GeneralIncomeGroupsAndSubGroupsFragment : Fragment() {

    private var _binding: FragmentGeneralIncomeGroupsAndSubGroupsBinding? = null
    private val binding get() = _binding!!

    private val generalGroupsAndSubGroupsViewModel: GeneralIncomeGroupsAndSubGroupsViewModel by viewModels()

    private var groupWithSubGroupsMutableList: MutableList<GroupWithSubGroups> = mutableListOf()

    private var adapter: GroupWithSubgroupsAdapter? = null

    private val onSubGroupCheckedImpl = object : SubGroupsAdapter.OnSubGroupCheckedChangeListener {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onSubgroupChecked(subgroup: SubGroup, isChecked: Boolean) {

            val updatedGroupWithSubGroupsMutableList = groupWithSubGroupsMutableList.map { groupWithSubGroups ->
                if (groupWithSubGroups.subgroups.contains(subgroup)) {
                    val updatedSubgroup = subgroup.copy(isChecked = isChecked)

                    val subGroupsMutableList = groupWithSubGroups.subgroups.toMutableList()

                    val index = subGroupsMutableList.indexOf(subgroup)

                    if (index != -1) {
                        subGroupsMutableList[index] = updatedSubgroup
                    }

                    groupWithSubGroups.copy(subgroups = subGroupsMutableList)
                } else {
                    groupWithSubGroups
                }
            }

            groupWithSubGroupsMutableList.clear()
            groupWithSubGroupsMutableList.addAll(updatedGroupWithSubGroupsMutableList)
            adapter?.updateGroups(groupWithSubGroupsMutableList)

            if (isChecked) {
                 generalGroupsAndSubGroupsViewModel.unarchiveIncomeSubGroupById(subgroup.id)
            } else {
                 generalGroupsAndSubGroupsViewModel.archiveIncomeSubGroup(subgroup.name)
            }
        }

        override fun onSubGroupDelete(subGroup: SubGroup) {
            groupWithSubGroupsMutableList.map { groupWithSubGroups ->

                if (groupWithSubGroups.subgroups.contains(subGroup)) {
                    val subGroupsMutableList = groupWithSubGroups.subgroups.toMutableList()

                    val index = subGroupsMutableList.indexOf(subGroup)

                    if (index != -1) {
                        subGroupsMutableList.removeAt(index)
                    }
                }
            }

            generalGroupsAndSubGroupsViewModel.deleteIncomeSubGroupById(subGroup.id)
        }
    }


    private val onGroupCheckedImpl = object : GroupWithSubgroupsAdapter.OnGroupCheckedChangeListener {

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onGroupChecked(groupWithSubGroups: GroupWithSubGroups, isChecked: Boolean) {
            val updatedGroups = groupWithSubGroupsMutableList.toMutableList()

            val index = updatedGroups.indexOf(groupWithSubGroups)
            if (index != -1) {
                val updatedGroup = groupWithSubGroups.copy(isExist = isChecked)
                updatedGroups[index] = updatedGroup

                adapter?.updateGroups(updatedGroups)

                if (isChecked) {
                    generalGroupsAndSubGroupsViewModel.unarchiveIncomeGroupById(groupWithSubGroups.id)
                } else {
                     generalGroupsAndSubGroupsViewModel.archiveIncomeGroupById(groupWithSubGroups.id!!)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGeneralIncomeGroupsAndSubGroupsBinding.inflate(inflater, container, false)

        setupRecyclerView()

        generalGroupsAndSubGroupsViewModel.allIncomeGroupsWithIncomeSubGroupsLiveData?.observe(viewLifecycleOwner) { allGroupsWithSubGroups ->
            allGroupsWithSubGroups?.let { groupList ->
                updateGroupWithSubGroupsList(groupList)
                adapter?.updateGroups(groupWithSubGroupsMutableList)
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
        groupWithSubGroupsMutableList.clear()

        for (groupWithSubGroup in groupsWithSubGroups) {
            val subGroupsForAdapter = groupWithSubGroup.incomeSubGroups.map {
                SubGroup(it.id, it.name, false, it.archivedDate == null)
            }

            if (subGroupsForAdapter.isNotEmpty()) {
                groupWithSubGroupsMutableList.add(
                    GroupWithSubGroups(
                        groupWithSubGroup.incomeGroup.id,
                        groupWithSubGroup.incomeGroup.name,
                        groupWithSubGroup.incomeGroup.archivedDate == null,
                        subGroupsForAdapter
                    )
                )
            }
        }
    }
}
