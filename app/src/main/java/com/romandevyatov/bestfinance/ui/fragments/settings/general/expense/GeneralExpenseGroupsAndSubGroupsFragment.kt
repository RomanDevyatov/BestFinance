package com.romandevyatov.bestfinance.ui.fragments.settings.general.expense

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.romandevyatov.bestfinance.databinding.FragmentArchivedExpenseSubGroupsBinding
import com.romandevyatov.bestfinance.databinding.FragmentGeneralExpenseGroupsAndSubGroupsBinding
import com.romandevyatov.bestfinance.ui.adapters.settings.group.ArchivedGroupsAdapter
import com.romandevyatov.bestfinance.ui.adapters.settings.group.model.GroupItem
import com.romandevyatov.bestfinance.ui.adapters.settings.subgroup.recyclerviewapproach.twoadapters.GroupWithSubGroups
import com.romandevyatov.bestfinance.ui.adapters.settings.subgroup.recyclerviewapproach.twoadapters.GroupWithSubgroupsAdapter
import com.romandevyatov.bestfinance.ui.adapters.settings.subgroup.recyclerviewapproach.twoadapters.SubGroup
import com.romandevyatov.bestfinance.ui.adapters.settings.subgroup.recyclerviewapproach.twoadapters.SubGroupsAdapter
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.ArchivedExpenseGroupsViewModel
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.ArchivedExpenseSubGroupsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GeneralExpenseGroupsAndSubGroupsFragment : Fragment() {

    private var _binding: FragmentGeneralExpenseGroupsAndSubGroupsBinding? = null
    private val binding get() = _binding!!

    private val archivedExpenseGroupsViewModel: ArchivedExpenseSubGroupsViewModel by viewModels()

    val selectedSubgroups: MutableList<SubGroup> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGeneralExpenseGroupsAndSubGroupsBinding.inflate(inflater, container, false)

        val onSubGroupCheckedImpl = object : SubGroupsAdapter.OnSubGroupCheckedChangeListener {
            override fun onSubgroupChecked(subgroup: SubGroup, isChecked: Boolean) {
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
                val groups: MutableList<GroupWithSubGroups> = mutableListOf()

                for (groupWithSubGroup in allGroupsWithSubGroups) {
                    val subGroups =
                        groupWithSubGroup.expenseSubGroups.filter { it.archivedDate != null }

                    val subGroupsForAdapter = subGroups.map { SubGroup(it.id, it.name) }.toList()

                    if (subGroups.isNotEmpty()) {
                        groups.add(
                            GroupWithSubGroups(
                                groupWithSubGroup.expenseGroup.name,
                                subGroupsForAdapter
                            )
                        )
                    }
                }

                val adapter = GroupWithSubgroupsAdapter(groups, onSubGroupCheckedImpl)

                binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
                binding.recyclerView.adapter = adapter
            }
        }

//        binding.unarchiveButton.setOnClickListener {
//            processSelectedSubgroups(selectedSubgroups)
//        }
//
//        binding.deleteButton.setOnClickListener {
//            deleteSelectedSubgroups(selectedSubgroups)
//        }

        return binding.root
    }

    private fun deleteSelectedSubgroups(selectedSubgroups: List<SubGroup>) {
        selectedSubgroups.forEach { subGroup ->
            archivedExpenseGroupsViewModel.deleteExpenseSubGroupById(subGroup.id)
        }
    }

    private fun processSelectedSubgroups(selectedSubgroups: List<SubGroup>) {
        selectedSubgroups.forEach { subGroup ->
            archivedExpenseGroupsViewModel.unarchiveExpenseSubGroupById(subGroup.id)
        }
    }
}
