package com.romandevyatov.bestfinance.ui.fragments.settings.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.romandevyatov.bestfinance.databinding.FragmentArchivedExpenseGroupsBinding
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.ArchivedExpenseGroupsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArchivedExpenseGroupsFragment : Fragment() {

    private var _binding: FragmentArchivedExpenseGroupsBinding? = null
    private val binding get() = _binding!!

    private val archiveExpenseGroupsViewModel: ArchivedExpenseGroupsViewModel by viewModels()

    private val archivedGroupsAdapter: ArchivedGroupsAdapter = ArchivedGroupsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArchivedExpenseGroupsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()

        archiveExpenseGroupsViewModel.allExpenseGroupsArchivedLiveData?.observe(viewLifecycleOwner) { groupsArchived ->
            archivedGroupsAdapter.submitList(groupsArchived.map { GroupItem(it.name) }.toList())
        }

        binding.unarchiveButton.setOnClickListener {
            unarchiveSelectedGroups()
        }

        binding.deleteButton.setOnClickListener {
            deleteSelectedGroups()
        }
    }

    private fun initRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = archivedGroupsAdapter
        }
    }

    private fun unarchiveSelectedGroups() {
        val selectedItems = archivedGroupsAdapter.getSelectedGroups()

        selectedItems.forEach { selectedItem ->
            archiveExpenseGroupsViewModel.getExpenseGroupsArchivedByNameLiveData(selectedItem.name)?.observe(viewLifecycleOwner) { group ->
                if (group != null) {
                    archiveExpenseGroupsViewModel.unarchiveExpenseGroup(group)
                }
            }
        }
    }

    private fun deleteSelectedGroups() {
        val selectedGroups = archivedGroupsAdapter.getSelectedGroups()
        // Perform the desired action with the selected groups
        // For example, you can show a toast with the selected groups' names
        val selectedGroupNames = selectedGroups.joinToString(", ") { it.name }
        Toast.makeText(requireContext(), "Selected Groups: $selectedGroupNames", Toast.LENGTH_SHORT).show()
    }
}
