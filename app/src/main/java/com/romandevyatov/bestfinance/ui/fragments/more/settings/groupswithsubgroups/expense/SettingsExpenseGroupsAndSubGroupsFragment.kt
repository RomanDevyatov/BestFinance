package com.romandevyatov.bestfinance.ui.fragments.more.settings.groupswithsubgroups.expense

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
import com.romandevyatov.bestfinance.databinding.FragmentSettingsExpenseGroupsAndSubGroupsBinding
import com.romandevyatov.bestfinance.ui.adapters.more.settings.settingsgroupswithsubgroups.tabs.SettingsGroupWithSubgroupsAdapter
import com.romandevyatov.bestfinance.ui.adapters.more.settings.settingsgroupswithsubgroups.tabs.SettingsSubGroupsAdapter
import com.romandevyatov.bestfinance.ui.adapters.more.settings.settingsgroupswithsubgroups.tabs.models.SettingsGroupWithSubGroupsItem
import com.romandevyatov.bestfinance.ui.adapters.more.settings.settingsgroupswithsubgroups.tabs.models.SettingsSubGroupItem
import com.romandevyatov.bestfinance.ui.fragments.more.settings.groupswithsubgroups.SettingsGroupsAndSubGroupsFragmentDirections
import com.romandevyatov.bestfinance.utils.WindowUtil
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.ExpenseGroupsAndSubGroupsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsExpenseGroupsAndSubGroupsFragment : Fragment() {

    private var _binding: FragmentSettingsExpenseGroupsAndSubGroupsBinding? = null
    private val binding get() = _binding!!

    private val generalGroupsAndSubGroupsViewModel: ExpenseGroupsAndSubGroupsViewModel by viewModels()

    private val settingsGroupWithSubgroupsAdapter: SettingsGroupWithSubgroupsAdapter by lazy {
        SettingsGroupWithSubgroupsAdapter(onGroupCheckedImpl, onSubGroupCheckedImpl)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsExpenseGroupsAndSubGroupsBinding.inflate(inflater, container, false)

        setupGroupWithSubgroupsAdapter()

        setOnBackPressedHandler()

        generalGroupsAndSubGroupsViewModel.allExpenseGroupsWithExpenseSubGroupsLiveData
            .observe(viewLifecycleOwner) { allGroupsWithSubGroups ->
                val settingsGroupWithSubGroupsItems = allGroupsWithSubGroups?.map { groups ->
                    val settingsSubGroupItems = groups.expenseSubGroups.map {
                        SettingsSubGroupItem(
                            it.id!!,
                            it.name,
                            it.expenseGroupId,
                            it.archivedDate == null
                        )
                    }.toMutableList()

                    SettingsGroupWithSubGroupsItem(
                        groups.expenseGroup.id,
                        groups.expenseGroup.name,
                        groups.expenseGroup.archivedDate == null,
                        settingsSubGroupItems
                    )
                } ?: emptyList()

                settingsGroupWithSubgroupsAdapter.submitList(settingsGroupWithSubGroupsItems)
            }

        return binding.root
    }

    private val onSubGroupCheckedImpl = object : SettingsSubGroupsAdapter.OnSubGroupListener {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onSubgroupChecked(settingsSubGroupItem: SettingsSubGroupItem, isChecked: Boolean) {
            if (isChecked) {
                generalGroupsAndSubGroupsViewModel.unarchiveExpenseSubGroupById(settingsSubGroupItem.id)
            } else {
                generalGroupsAndSubGroupsViewModel.archiveExpenseSubGroup(settingsSubGroupItem.name)
            }
        }

        override fun onSubGroupDelete(settingsSubGroupItem: SettingsSubGroupItem) {
            settingsSubGroupItem.id.let { id ->
                WindowUtil.showDeleteDialog(
                    context = requireContext(),
                    viewModel = generalGroupsAndSubGroupsViewModel,
                    message = getString(R.string.delete_confirmation_warning_message, settingsSubGroupItem.name),
                    isCountdown = true,
                    itemId = id,
                    rootView = binding.root,
                    groupOrSubGroup = false
                ) { }
            }
        }

        override fun navigateToUpdateSubGroup(id: Long) {
            val action =
                SettingsGroupsAndSubGroupsFragmentDirections.actionGroupsAndSubGroupsSettingsFragmentToUpdateExpenseSubGroupFragment()
            action.expenseSubGroupId = id
            findNavController().navigate(action)
        }
    }

    private val onGroupCheckedImpl = object : SettingsGroupWithSubgroupsAdapter.OnGroupCheckedChangeListener {

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onGroupChecked(settingsGroupWithSubGroupsItem: SettingsGroupWithSubGroupsItem, isChecked: Boolean) {
            lifecycleScope.launch(Dispatchers.IO) {
                if (isChecked) {
                    generalGroupsAndSubGroupsViewModel.unarchiveExpenseGroupById(
                        settingsGroupWithSubGroupsItem.id
                    )
                } else {
                    generalGroupsAndSubGroupsViewModel.archiveExpenseGroupByIdSpecific(
                        settingsGroupWithSubGroupsItem.id
                    )
                }
            }
        }

        override fun onGroupDelete(settingsGroupWithSubGroupsItem: SettingsGroupWithSubGroupsItem) {
            settingsGroupWithSubGroupsItem.id?.let { id ->
                WindowUtil.showDeleteDialog(
                    context = requireContext(),
                    viewModel = generalGroupsAndSubGroupsViewModel,
                    message = getString(R.string.delete_confirmation_warning_message, settingsGroupWithSubGroupsItem.name),
                    isCountdown = true,
                    itemId = id,
                    rootView = binding.root,
                    groupOrSubGroup = true
                ) { }
            }
        }

        override fun navigateToUpdateGroup(name: String) {
            val action =
                SettingsGroupsAndSubGroupsFragmentDirections.actionGroupsAndSubGroupsSettingsFragmentToUpdateExpenseGroupFragment()
            action.expenseGroupName = name
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
        binding.recyclerView.adapter = settingsGroupWithSubgroupsAdapter
    }

}
