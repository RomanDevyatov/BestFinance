package com.romandevyatov.bestfinance.ui.fragments.settings.deprecated.subgroups.expense

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.romandevyatov.bestfinance.databinding.FragmentArchivedExpenseSubGroupsBinding
import com.romandevyatov.bestfinance.ui.adapters.settings.deprecated.subgroup.recyclerviewapproach.twoadapters.GroupWithSubGroups3
import com.romandevyatov.bestfinance.ui.adapters.settings.deprecated.subgroup.recyclerviewapproach.twoadapters.GroupWithSubgroupsAdapter3
import com.romandevyatov.bestfinance.ui.adapters.settings.deprecated.subgroup.recyclerviewapproach.twoadapters.SubGroup3
import com.romandevyatov.bestfinance.ui.adapters.settings.deprecated.subgroup.recyclerviewapproach.twoadapters.SubGroupsAdapter3
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.deprecated.ArchivedExpenseSubGroupsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArchivedExpenseSubGroupsFragment : Fragment() {

    private var _binding: FragmentArchivedExpenseSubGroupsBinding? = null
    private val binding get() = _binding!!

    private val archivedExpenseGroupsViewModel: ArchivedExpenseSubGroupsViewModel by viewModels()

    val selectedSubgroups: MutableList<SubGroup3> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArchivedExpenseSubGroupsBinding.inflate(inflater, container, false)

        val onSubGroupCheckedImpl = object : SubGroupsAdapter3.OnSubGroupCheckedChangeListener {
            override fun onSubgroupChecked(subgroup: SubGroup3, isChecked: Boolean) {
                if (isChecked) {
                    selectedSubgroups.add(subgroup)
                } else {
                    selectedSubgroups.remove(subgroup)
                }
            }
        }

        archivedExpenseGroupsViewModel.allExpenseGroupsWithExpenseSubGroupsLiveData?.observe(
            viewLifecycleOwner
        ) { allGroupsWithSubGroups ->
            if (allGroupsWithSubGroups != null) {
                val groups: MutableList<GroupWithSubGroups3> = mutableListOf()

                for (groupWithSubGroup in allGroupsWithSubGroups) {
                    val subGroups =
                        groupWithSubGroup.expenseSubGroups.filter { it.archivedDate != null }

                    val subGroupsForAdapter3 = subGroups.map { SubGroup3(it.id, it.name) }.toList()

                    if (subGroups.isNotEmpty()) {
                        groups.add(
                            GroupWithSubGroups3(
                                groupWithSubGroup.expenseGroup.name,
                                subGroupsForAdapter3
                            )
                        )
                    }
                }

                val adapter = GroupWithSubgroupsAdapter3(groups, onSubGroupCheckedImpl)

                binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
                binding.recyclerView.adapter = adapter
            }
        }

        binding.unarchiveButton.setOnClickListener {
            processSelectedSubgroups(selectedSubgroups)
        }

        binding.deleteButton.setOnClickListener {
            deleteSelectedSubgroups(selectedSubgroups)
        }

        return binding.root
    }

    private fun deleteSelectedSubgroups(selectedSubgroups: List<SubGroup3>) {
        selectedSubgroups.forEach { subGroup ->
            archivedExpenseGroupsViewModel.deleteExpenseSubGroupById(subGroup.id)
        }
    }

    private fun processSelectedSubgroups(selectedSubgroups: List<SubGroup3>) {
        selectedSubgroups.forEach { subGroup ->
            archivedExpenseGroupsViewModel.unarchiveExpenseSubGroupById(subGroup.id)
        }
    }
}
