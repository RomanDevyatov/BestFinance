package com.romandevyatov.bestfinance.ui.fragments.more.settings.groupswithsubgroups.income

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.FragmentSettingsIncomeGroupsAndSubGroupsBinding
import com.romandevyatov.bestfinance.ui.adapters.more.settings.groupswithsubgroups.tabs.GroupWithSubgroupsAdapter
import com.romandevyatov.bestfinance.ui.adapters.more.settings.groupswithsubgroups.tabs.SubGroupsAdapter
import com.romandevyatov.bestfinance.ui.adapters.more.settings.groupswithsubgroups.tabs.models.GroupWithSubGroupsItem
import com.romandevyatov.bestfinance.ui.adapters.more.settings.groupswithsubgroups.tabs.models.SubGroupItem
import com.romandevyatov.bestfinance.ui.fragments.more.settings.groupswithsubgroups.SettingsGroupsAndSubGroupsFragmentDirections
import com.romandevyatov.bestfinance.utils.WindowUtil
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.IncomeGroupsAndSubGroupsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsIncomeGroupsAndSubGroupsFragment : Fragment() {

    private var _binding: FragmentSettingsIncomeGroupsAndSubGroupsBinding? = null
    private val binding get() = _binding!!

    private val incomeGroupsAndSubGroupsViewModel: IncomeGroupsAndSubGroupsViewModel by viewModels()

    private val groupWithSubgroupsAdapter: GroupWithSubgroupsAdapter by lazy {
        GroupWithSubgroupsAdapter(onGroupCheckedImpl, onSubGroupCheckedImpl)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsIncomeGroupsAndSubGroupsBinding.inflate(inflater, container, false)

        setupGroupWithSubgroupsAdapter()

        setOnBackPressedHandler()

        incomeGroupsAndSubGroupsViewModel.allIncomeGroupsWithIncomeSubGroupsLiveData
            .observe(viewLifecycleOwner) { allGroupsWithSubGroups ->
                val groupWithSubGroupsItems = allGroupsWithSubGroups?.map { groups ->
                    val subGroupItems = groups.incomeSubGroups.map {
                        SubGroupItem(
                            it.id!!,
                            it.name,
                            it.incomeGroupId,
                            it.archivedDate == null
                        )
                    }.toMutableList()

                    GroupWithSubGroupsItem(
                        groups.incomeGroup.id,
                        groups.incomeGroup.name,
                        groups.incomeGroup.archivedDate == null,
                        subGroupItems
                    )
                } ?: emptyList()

                groupWithSubgroupsAdapter.submitList(groupWithSubGroupsItems)
            }

        return binding.root
    }

    private val onSubGroupCheckedImpl = object : SubGroupsAdapter.OnSubGroupListener {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onSubgroupChecked(subGroupItem: SubGroupItem, isChecked: Boolean) {
            if (isChecked) {
                incomeGroupsAndSubGroupsViewModel.unarchiveIncomeSubGroupById(subGroupItem.id)
            } else {
                incomeGroupsAndSubGroupsViewModel.archiveIncomeSubGroupByIdSpecific(
                    subGroupItem.id
                )
            }
        }

        override fun onSubGroupDelete(subGroupItem: SubGroupItem) {
            subGroupItem.id.let { id ->
                WindowUtil.showDeleteDialog(
                    context = requireContext(),
                    viewModel = incomeGroupsAndSubGroupsViewModel,
                    message = getString(R.string.delete_confirmation_warning_message, subGroupItem.name),
                    isCountdown = true,
                    itemId = id,
                    rootView = binding.root,
                    groupOrSubGroup = false
                ) { }
            }
        }

        override fun navigateToUpdateSubGroup(id: Long) {
            val action =
                SettingsGroupsAndSubGroupsFragmentDirections.actionGroupsAndSubGroupsSettingsFragmentToUpdateIncomeSubGroupFragment()
            action.incomeSubGroupId = id
            findNavController().navigate(action)
        }
    }

    private val onGroupCheckedImpl = object : GroupWithSubgroupsAdapter.OnGroupCheckedChangeListener {

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onGroupChecked(groupWithSubGroupsItem: GroupWithSubGroupsItem, isChecked: Boolean) {
            lifecycleScope.launch(Dispatchers.IO) {
                if (isChecked) {
                    incomeGroupsAndSubGroupsViewModel.unarchiveIncomeGroupByIdSpecific(
                        groupWithSubGroupsItem.id
                    )
                } else {
                    incomeGroupsAndSubGroupsViewModel.archiveIncomeGroupByIdSpecific(
                        groupWithSubGroupsItem.id
                    )
                }
            }
        }

        override fun onGroupDelete(groupWithSubGroupsItem: GroupWithSubGroupsItem) {
            groupWithSubGroupsItem.id.let { id ->
                if (id != null) {
                    WindowUtil.showDeleteDialog(
                        context = requireContext(),
                        viewModel = incomeGroupsAndSubGroupsViewModel,
                        message = getString(R.string.delete_confirmation_warning_message, groupWithSubGroupsItem.name),
                        isCountdown = true,
                        itemId = id,
                        rootView = binding.root,
                        groupOrSubGroup = true
                    ) { }
                }
            }
        }

        override fun navigateToUpdateGroup(name: String) {
            val action =
                SettingsGroupsAndSubGroupsFragmentDirections.actionGroupsAndSubGroupsSettingsFragmentToUpdateIncomeGroupFragment()
            action.incomeGroupName = name
            findNavController().navigate(action)
        }
    }

    private fun setOnBackPressedHandler() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.more_fragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun setupGroupWithSubgroupsAdapter() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = groupWithSubgroupsAdapter
    }

}
