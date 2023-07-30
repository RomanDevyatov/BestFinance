package com.romandevyatov.bestfinance.ui.fragments.settings.groups.income

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.romandevyatov.bestfinance.databinding.FragmentArchivedIncomeGroupsBinding
import com.romandevyatov.bestfinance.ui.adapters.settings.group.ArchivedGroupsAdapter
import com.romandevyatov.bestfinance.ui.adapters.settings.group.model.GroupItem
import com.romandevyatov.bestfinance.viewmodels.foreachfragment.ArchivedIncomeGroupsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArchivedIncomeGroupsFragment : Fragment() {

    private var _binding: FragmentArchivedIncomeGroupsBinding? = null
    private val binding get() = _binding!!

    private val archivedIncomeGroupsViewModel: ArchivedIncomeGroupsViewModel by viewModels()

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

        archivedIncomeGroupsViewModel.allIncomeGroupsArchivedLiveData.observe(viewLifecycleOwner) { allIncomeGroupsArchived ->
            archivedGroupsAdapter.submitList(allIncomeGroupsArchived.map { GroupItem(it.name) }.toList())
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
            archivedIncomeGroupsViewModel.getIncomeGroupsArchivedByNameLiveData(selectedItem.name)?.observe(viewLifecycleOwner) { group ->
                if (group != null) {
                    val isIncludeSubGroups = binding.checkBox.isChecked
                    archivedIncomeGroupsViewModel.unarchiveIncomeGroup(group, isIncludeSubGroups)
                }
            }
        }

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

}
