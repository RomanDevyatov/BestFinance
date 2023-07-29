package com.romandevyatov.bestfinance.ui.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.romandevyatov.bestfinance.data.entities.IncomeGroup
import com.romandevyatov.bestfinance.databinding.FragmentArchivedIncomeGroupsBinding
import com.romandevyatov.bestfinance.ui.adapters.history.IncomeHistoryAdapter
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.ArchiveGroupsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArchivedIncomeGroupsFragment : Fragment() {

    private var _binding: FragmentArchivedIncomeGroupsBinding? = null
    private val binding get() = _binding!!

    private val archiveGroupsViewModel: ArchiveGroupsViewModel by viewModels()

    private val archivedGroupsAdapter: ArchivedGroupsAdapter = ArchivedGroupsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArchivedIncomeGroupsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()

        archivedGroupsAdapter.submitList(createGroupData())

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
        // Process the selected items here
        // For example, you can show a Toast with the selected items' texts
        val selectedTexts = selectedItems.joinToString(", ") { it.name }
        val toastText = "Selected elements: $selectedTexts"
        Toast.makeText(requireContext(), toastText, Toast.LENGTH_SHORT).show()
    }

    private fun deleteSelectedGroups() {
        val selectedGroups = archivedGroupsAdapter.getSelectedGroups()
        // Perform the desired action with the selected groups
        // For example, you can show a toast with the selected groups' names
        val selectedGroupNames = selectedGroups.joinToString(", ") { it.name }
        Toast.makeText(requireContext(), "Selected Groups: $selectedGroupNames", Toast.LENGTH_SHORT).show()
    }

    private fun createGroupData(): List<GroupItem> {
        // Replace this with your actual list of groups or fetch it from your data source
        return listOf(
            GroupItem("Group 1"),
            GroupItem("Group 2"),
            GroupItem("Group 3"),
            // Add more groups here as needed
        )
    }


}
