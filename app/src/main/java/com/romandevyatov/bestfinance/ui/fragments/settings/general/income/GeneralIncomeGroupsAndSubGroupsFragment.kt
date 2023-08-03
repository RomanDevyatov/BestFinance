package com.romandevyatov.bestfinance.ui.fragments.settings.general.income

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.romandevyatov.bestfinance.databinding.FragmentGeneralIncomeGroupsAndSubGroupsBinding
import com.romandevyatov.bestfinance.ui.adapters.settings.generalcategories.recyclerviewapproach.twoadapters.GroupWithSubGroups2
import com.romandevyatov.bestfinance.ui.adapters.settings.generalcategories.recyclerviewapproach.twoadapters.GroupWithSubgroupsAdapter2
import com.romandevyatov.bestfinance.ui.adapters.settings.generalcategories.recyclerviewapproach.twoadapters.SubGroup2
import com.romandevyatov.bestfinance.ui.adapters.settings.generalcategories.recyclerviewapproach.twoadapters.SubGroupsAdapter2
import com.romandevyatov.bestfinance.ui.adapters.settings.subgroup.recyclerviewapproach.twoadapters.SubGroup
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.ArchivedIncomeSubGroupsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GeneralIncomeGroupsAndSubGroupsFragment : Fragment() {

    private var _binding: FragmentGeneralIncomeGroupsAndSubGroupsBinding? = null
    private val binding get() = _binding!!

    private val archivedIncomeSubGroupsViewModel: ArchivedIncomeSubGroupsViewModel by viewModels()

    val selectedSubgroups: MutableList<SubGroup2> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGeneralIncomeGroupsAndSubGroupsBinding.inflate(inflater, container, false)

        val onSubGroupCheckedImpl = object : SubGroupsAdapter2.OnSubGroupCheckedChangeListener {
            override fun onSubgroupChecked(subgroup: SubGroup2, isChecked: Boolean) {
                if (isChecked) {
                    // archive sub group here
                    selectedSubgroups.add(subgroup)
                } else {
                    // unarchive sub group here
                    selectedSubgroups.remove(subgroup)
                }
            }
        }

        archivedIncomeSubGroupsViewModel.allIncomeGroupsWithIncomeSubGroupsLiveData?.observe(viewLifecycleOwner) { allGroupsWithSubGroups ->
            if (allGroupsWithSubGroups != null) {
                val groups: MutableList<GroupWithSubGroups2> = mutableListOf()

                for (groupWithSubGroup in allGroupsWithSubGroups) {
                    val subGroups = groupWithSubGroup.incomeSubGroups

                    val subGroupsForAdapter2 = subGroups.map { SubGroup2(it.id, it.name) }.toList()

                    if (subGroups.isNotEmpty()) {
                        groups.add(
                            GroupWithSubGroups2(groupWithSubGroup.incomeGroup.name, groupWithSubGroup.incomeGroup.archivedDate == null, subGroupsForAdapter2)
                        )
                    }
                }

                val adapter = GroupWithSubgroupsAdapter2(groups, onSubGroupCheckedImpl)

                binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
                binding.recyclerView.adapter = adapter
            }
        }

        return binding.root
    }

    private fun deleteSelectedSubgroups(selectedSubgroups: MutableList<SubGroup>) {
        selectedSubgroups.forEach { subGroup ->
            archivedIncomeSubGroupsViewModel.deleteIncomeSubGroupById(subGroup.id)
        }
    }

    private fun processSelectedSubgroups(selectedSubgroups: List<SubGroup>) {
        selectedSubgroups.forEach { subGroup ->
            archivedIncomeSubGroupsViewModel.unarchiveIncomeSubGroupById(subGroup.id)
        }
    }
}