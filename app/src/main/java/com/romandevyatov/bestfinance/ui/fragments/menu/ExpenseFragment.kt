package com.romandevyatov.bestfinance.ui.fragments.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.romandevyatov.bestfinance.databinding.FragmentExpenseBinding
import com.romandevyatov.bestfinance.databinding.FragmentHomeBinding
import com.romandevyatov.bestfinance.ui.adapters.ParentExpenseGroupAdapter
import com.romandevyatov.bestfinance.viewmodels.ExpenseGroupWithExpenseSubGroupViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ExpenseFragment : Fragment() {

    private lateinit var binding: FragmentExpenseBinding

    private val expenseViewModel: ExpenseGroupWithExpenseSubGroupViewModel by viewModels()
    private lateinit var parentExpenseGroupAdapter: ParentExpenseGroupAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentExpenseBinding.bind(view)

        initRecyclerView()

        expenseViewModel.expenseGroupsLiveData.observe(viewLifecycleOwner) {
            parentExpenseGroupAdapter.submitList(it)
        }
    }

    private fun initRecyclerView() {
        binding.parentRecyclerView.layoutManager = LinearLayoutManager(requireContext(),
                    LinearLayoutManager.VERTICAL, false)
        parentExpenseGroupAdapter = ParentExpenseGroupAdapter()
        binding.parentRecyclerView.adapter = parentExpenseGroupAdapter
    }
}
