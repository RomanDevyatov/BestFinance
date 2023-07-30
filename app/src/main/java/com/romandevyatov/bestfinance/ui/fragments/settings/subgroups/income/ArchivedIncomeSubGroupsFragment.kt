package com.romandevyatov.bestfinance.ui.fragments.settings.subgroups.income

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.romandevyatov.bestfinance.databinding.FragmentArchivedIncomeSubGroupsBinding
import com.romandevyatov.bestfinance.ui.adapters.settings.subgroup.recyclerviewapproach.twoadapters.GroupWithSubGroups
import com.romandevyatov.bestfinance.ui.adapters.settings.subgroup.recyclerviewapproach.twoadapters.GroupWithSubgroupsAdapter
import com.romandevyatov.bestfinance.ui.adapters.settings.subgroup.recyclerviewapproach.twoadapters.SubGroup
import com.romandevyatov.bestfinance.ui.adapters.settings.subgroup.recyclerviewapproach.twoadapters.SubGroupsAdapter
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.ArchivedIncomeSubGroupsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArchivedIncomeSubGroupsFragment : Fragment() {

    private var _binding: FragmentArchivedIncomeSubGroupsBinding? = null
    private val binding get() = _binding!!

    private val archivedIncomeGroupsViewModel: ArchivedIncomeSubGroupsViewModel by viewModels()

    val selectedSubgroups: MutableList<SubGroup> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArchivedIncomeSubGroupsBinding.inflate(inflater, container, false)

        val onSubGroupCheckedImpl = object : SubGroupsAdapter.OnSubGroupCheckedChangeListener {
            override fun onSubgroupChecked(subgroup: SubGroup, isChecked: Boolean) {
                if (isChecked) {
                    selectedSubgroups.add(subgroup)
                } else {
                    selectedSubgroups.remove(subgroup)
                }
            }
        }

        archivedIncomeGroupsViewModel.allIncomeGroupsWithIncomeSubGroupsLiveData?.observe(viewLifecycleOwner) { allGroupsWithSubGroups ->
            if (allGroupsWithSubGroups != null) {
                val groups: MutableList<GroupWithSubGroups> = mutableListOf()

                for (groupWithSubGroup in allGroupsWithSubGroups) {
                    val subGroups = groupWithSubGroup.incomeSubGroups.filter { it.archivedDate != null }

                    val subGroupsForAdapter = subGroups.map { SubGroup(it.id, it.name) }.toList()

                    if (subGroups.isNotEmpty()) {
                        groups.add(
                            GroupWithSubGroups(groupWithSubGroup.incomeGroup.name, subGroupsForAdapter)
                        )
                    }
                }

                val adapter = GroupWithSubgroupsAdapter(groups, onSubGroupCheckedImpl)

                binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
                binding.recyclerView.adapter = adapter
            }
        }

        binding.unarchiveButton.setOnClickListener {
            processSelectedSubgroups(selectedSubgroups)
        }

        binding.deleteButton.setOnClickListener {
            processSelectedSubgroups(selectedSubgroups)
        }

        return binding.root
    }

    private fun processSelectedSubgroups(selectedSubgroups: List<SubGroup>) {
        selectedSubgroups.forEach { subGroup ->
            archivedIncomeGroupsViewModel.unarchiveIncomeSubGroupById(subGroup.id)
        }
    }
}