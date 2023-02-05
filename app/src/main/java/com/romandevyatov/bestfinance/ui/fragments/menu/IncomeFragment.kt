package com.romandevyatov.bestfinance.ui.fragments.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.romandevyatov.bestfinance.databinding.FragmentIncomeBinding
import com.romandevyatov.bestfinance.db.entities.IncomeGroup
import com.romandevyatov.bestfinance.ui.adapters.IncomeGroupAdapter
import com.romandevyatov.bestfinance.ui.adapters.ItemClickListener
import com.romandevyatov.bestfinance.viewmodels.IncomeGroupViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class IncomeFragment : Fragment(), ItemClickListener<IncomeGroup> {

    private lateinit var binding: FragmentIncomeBinding

    private val incomeGroupViewModel: IncomeGroupViewModel by viewModels()
    private lateinit var incomeGroupAdapter: IncomeGroupAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentIncomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentIncomeBinding.bind(view)

        binding.addIncomeGroupButton.setOnClickListener {
            val newIncomeGroupName = binding.newIncomeGroupName.text.toString()
            incomeGroupViewModel.insertIncomeGroup(IncomeGroup(name = newIncomeGroupName))

            Snackbar.make(binding.root, "Group $newIncomeGroupName was added", Snackbar.LENGTH_SHORT).show()
        }

        incomeGroupViewModel.incomeGroupsLiveData.observe(viewLifecycleOwner) {
            incomeGroupAdapter.submitList(it)
        }

        initRecyclerView()
    }

    private fun initRecyclerView() {
        incomeGroupAdapter = IncomeGroupAdapter(this)
        binding.incomeGroupRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.incomeGroupRecyclerView.adapter = incomeGroupAdapter
    }

    override fun deleteItem(item: IncomeGroup) {
        incomeGroupViewModel.deleteIncomeGroup(item)
    }

}
