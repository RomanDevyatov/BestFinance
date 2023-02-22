package com.romandevyatov.bestfinance.ui.fragments.menu

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.romandevyatov.bestfinance.databinding.FragmentIncomeBinding
import com.romandevyatov.bestfinance.db.entities.IncomeGroup
import com.romandevyatov.bestfinance.db.entities.IncomeHistory
import com.romandevyatov.bestfinance.db.entities.IncomeSubGroup
import com.romandevyatov.bestfinance.db.entities.relations.IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories
import com.romandevyatov.bestfinance.db.entities.relations.IncomeSubGroupWithIncomeHistories
import com.romandevyatov.bestfinance.ui.adapters.menu.income.ParentIncomeGroupAdapter
import com.romandevyatov.bestfinance.ui.adapters.utilities.AddItemClickListener
import com.romandevyatov.bestfinance.viewmodels.IncomeGroupViewModel
import com.romandevyatov.bestfinance.viewmodels.IncomeHistoryViewModel
import com.romandevyatov.bestfinance.viewmodels.IncomeSubGroupViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class IncomeFragment : Fragment(), AddItemClickListener<IncomeGroup> {

    private lateinit var binding: FragmentIncomeBinding

    private val incomeGroupViewModel: IncomeGroupViewModel by viewModels()
    private val incomeSubGroupViewModel: IncomeSubGroupViewModel by viewModels()
    private val incomeHistoryViewModel: IncomeHistoryViewModel by viewModels()

    private val parentIncomeGroupAdapter: ParentIncomeGroupAdapter = ParentIncomeGroupAdapter(this)


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentIncomeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentIncomeBinding.bind(view)

//        setGroupSwipeListener(view)

        initRecyclerView()

        incomeGroupViewModel.allNotArchivedIncomeGroupWithIncomeSubGroupsIncludingIncomeHistoryAndLiveData.observe(viewLifecycleOwner) {
            parentIncomeGroupAdapter.submitList(it)
        }

        incomeHistoryViewModel.incomeHistoryLiveData.observe(viewLifecycleOwner) { expenseHistories ->
            val sumOfExpenseHistoryAmount = expenseHistories.sumOf { it.amount }
            binding.sumOfIncomeHistoryAmountTextView.text = sumOfExpenseHistoryAmount.toString()
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getUpdatedIncomeGroupWithIncomeSubGroupIncludingIncomeHistories(incomeGroupWithIncomeSubGroupsIncludingIncomeHistories: IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories): IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories {
        val updatedIncomeGroup = IncomeGroup(
            id = incomeGroupWithIncomeSubGroupsIncludingIncomeHistories.incomeGroup.id,
            name = incomeGroupWithIncomeSubGroupsIncludingIncomeHistories.incomeGroup.name,
            isArchived = 1
        )

        val updatedIncomeSubGroupWithIncomeHistoriesList: ArrayList<IncomeSubGroupWithIncomeHistories> = ArrayList()

        for (groupWithSubGroupsIncludingHistories in incomeGroupWithIncomeSubGroupsIncludingIncomeHistories.incomeSubGroupWithIncomeHistories) {
            val updatedSubGroup = IncomeSubGroup(
                id = groupWithSubGroupsIncludingHistories.incomeSubGroup.id,
                incomeGroupId = groupWithSubGroupsIncludingHistories.incomeSubGroup.incomeGroupId,
                name = groupWithSubGroupsIncludingHistories.incomeSubGroup.name,
                isArchived = 1
            )

            val historyList: ArrayList<IncomeHistory> = ArrayList()
            for (history in groupWithSubGroupsIncludingHistories.incomeHistories) {
                historyList.add(
                    IncomeHistory(
                        id = history.id,
                        incomeSubGroupId = history.incomeSubGroupId,
                        amount = history.amount,
                        date = history.date,
                        comment = history.comment,
                        walletId = history.walletId,
                        isArchived = 1
                    )
                )
            }

            updatedIncomeSubGroupWithIncomeHistoriesList.add(
                IncomeSubGroupWithIncomeHistories(
                    incomeSubGroup = updatedSubGroup,
                    incomeHistories = historyList
                )
            )
        }

        val updatedIncomeGroupWithIncomeSubGroupsWithIncomeHistories = IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories(
            incomeGroup = updatedIncomeGroup,
            incomeSubGroupWithIncomeHistories = updatedIncomeSubGroupWithIncomeHistoriesList
        )

        return updatedIncomeGroupWithIncomeSubGroupsWithIncomeHistories
    }


    private fun setGroupSwipeListener(view: View) {
        val itemTouchHelperCallback = object: ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.adapterPosition

                val incomeGroupWithIncomeSubGroupsWithIncomeHistories = parentIncomeGroupAdapter.incomeGroupWithIncomeSubGroupsIncludingIncomeHistoriesNotArchivedDiffer.currentList[pos]

                val updatedIncomeGroupWithIncomeSubGroupsWithIncomeHistories = getUpdatedIncomeGroupWithIncomeSubGroupIncludingIncomeHistories(incomeGroupWithIncomeSubGroupsWithIncomeHistories)

                updateIncomeGroupWithIncomeSubGroupsIncludingIncomeHistories(updatedIncomeGroupWithIncomeSubGroupsWithIncomeHistories)

                Snackbar.make(view, "Income group archived", Snackbar.LENGTH_LONG).apply {
                    setAction("UNDO") {
                        updateIncomeGroupWithIncomeSubGroupsIncludingIncomeHistories(incomeGroupWithIncomeSubGroupsWithIncomeHistories)
                    }
                    show()
                }

            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.parentRecyclerView)
        }
    }

    private fun updateIncomeGroupWithIncomeSubGroupsIncludingIncomeHistories(updatedIncomeGroupWithIncomeSubGroupsIncludingIncomeHistories: IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories) {
        incomeGroupViewModel.updateIncomeGroup(updatedIncomeGroupWithIncomeSubGroupsIncludingIncomeHistories.incomeGroup)
        updatedIncomeGroupWithIncomeSubGroupsIncludingIncomeHistories.incomeSubGroupWithIncomeHistories.forEach { incomeSubGroupWithIncomeHistories ->
            incomeSubGroupViewModel.updateIncomeSubGroup(incomeSubGroupWithIncomeHistories.incomeSubGroup)
            incomeSubGroupWithIncomeHistories.incomeHistories.forEach {
                incomeHistoryViewModel.updateIncomeHistory(it)
            }
        }
    }


    private fun initRecyclerView() {
        binding.parentRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.parentRecyclerView.adapter = parentIncomeGroupAdapter
    }

    override fun addItem(item: IncomeGroup) {
        val action = IncomeFragmentDirections.actionNavigationIncomeToNavigationAddIncome()
        action.incomeGroupName = item.name
        findNavController().navigate(action)
    }






}
