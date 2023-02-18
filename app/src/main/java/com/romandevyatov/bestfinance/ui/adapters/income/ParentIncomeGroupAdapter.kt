package com.romandevyatov.bestfinance.ui.adapters.income


import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.google.android.material.snackbar.Snackbar
import com.romandevyatov.bestfinance.databinding.ItemRowParentBinding
import com.romandevyatov.bestfinance.db.entities.IncomeGroup
import com.romandevyatov.bestfinance.db.entities.IncomeHistory
import com.romandevyatov.bestfinance.db.entities.IncomeSubGroup
import com.romandevyatov.bestfinance.db.entities.relations.IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories
import com.romandevyatov.bestfinance.db.entities.relations.IncomeSubGroupWithIncomeHistories
import com.romandevyatov.bestfinance.ui.adapters.utilities.AddItemClickListener


class ParentIncomeGroupAdapter(
    private val onClickListener: AddItemClickListener<IncomeGroup>
) : RecyclerView.Adapter<ParentIncomeGroupAdapter.IncomeGroupItemViewHolder>() {

    private val differentCallback = object: DiffUtil.ItemCallback<IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories>() {

        override fun areItemsTheSame(oldItem: IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories, newItem: IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories): Boolean {
            return oldItem.incomeGroup == newItem.incomeGroup
        }

        override fun areContentsTheSame(oldItem: IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories, newItem: IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories): Boolean {
            return oldItem == newItem
        }
    }

    val incomeGroupWithIncomeSubGroupsIncludingIncomeHistoriesNotArchivedDiffer = AsyncListDiffer(this, differentCallback)

    private val viewPool = RecyclerView.RecycledViewPool()

    inner class IncomeGroupItemViewHolder(
        private val binding: ItemRowParentBinding,
        private val onClickListener: AddItemClickListener<IncomeGroup>
        ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(notArchivedIncomeGroupWithIncomeSubGroupsIncludingIncomeHistories: IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories) {
            binding.groupNameTextView.text = notArchivedIncomeGroupWithIncomeSubGroupsIncludingIncomeHistories.incomeGroup.name

            val childAdapter = ChildIncomeSubGroupsAdapter(notArchivedIncomeGroupWithIncomeSubGroupsIncludingIncomeHistories.incomeSubGroupWithIncomeHistories)
            val lm = LinearLayoutManager(binding.childRecyclerView.context, LinearLayoutManager.VERTICAL, false)
            lm.initialPrefetchItemCount = notArchivedIncomeGroupWithIncomeSubGroupsIncludingIncomeHistories.incomeSubGroupWithIncomeHistories.size

            binding.childRecyclerView.layoutManager = lm
            binding.childRecyclerView.adapter = childAdapter
            binding.childRecyclerView.setRecycledViewPool(viewPool)

            binding.addNewIncomeOfSelectedGroupIcon .setOnClickListener {
                onClickListener.addItem(notArchivedIncomeGroupWithIncomeSubGroupsIncludingIncomeHistories.incomeGroup)
            }

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

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val pos = viewHolder.adapterPosition

                val incomeSubGroupWithIncomeHistories = childAdapter.notArchivedIncomeSubGroupWithIncomeHistoriesDiffer.currentList[pos]

                val updatedIncomeSubGroupWithIncomeHistories = getUpdatedIncomeSubGroupWithIncomeHistories(incomeSubGroupWithIncomeHistories)

                updateIncomeSubGroupWithIncomeHistories(updatedIncomeSubGroupWithIncomeHistories)

                    Snackbar.make(itemView, "Income sub group archived", Snackbar.LENGTH_LONG).apply {
                        setAction("UNDO") {
                            updateIncomeSubGroupWithIncomeHistories(incomeSubGroupWithIncomeHistories)
                        }
                        show()
                    }
                }
            }


            ItemTouchHelper(itemTouchHelperCallback).apply {
                attachToRecyclerView(binding.childRecyclerView)
            }



        }
    }


    private fun updateIncomeSubGroupWithIncomeHistories(updatedIncomeSubGroupWithIncomeHistories: IncomeSubGroupWithIncomeHistories) {
//        incomeSubGroupViewModel.updateIncomeSubGroup(updatedIncomeSubGroupWithIncomeHistories.incomeSubGroup)
//        updatedIncomeSubGroupWithIncomeHistories.incomeHistories.forEach {
//            incomeHistoryViewModel.updateIncomeHistory(it)
//        }
    }


    fun getUpdatedIncomeSubGroupWithIncomeHistories(groupWithSubGroupsIncludingHistories: IncomeSubGroupWithIncomeHistories): IncomeSubGroupWithIncomeHistories {
        val updatedSubGroup = IncomeSubGroup(
            id = groupWithSubGroupsIncludingHistories.incomeSubGroup.id,
            incomeGroupId = groupWithSubGroupsIncludingHistories.incomeSubGroup.incomeGroupId,
            name = groupWithSubGroupsIncludingHistories.incomeSubGroup.name,
            isArchived = 1
        )

        val updatedHistories: ArrayList<IncomeHistory> = ArrayList()

        groupWithSubGroupsIncludingHistories.incomeHistories.forEach {
            updatedHistories.add(
                IncomeHistory(
                    id = it.id,
                    incomeSubGroupId = it.incomeSubGroupId,
                    amount = it.amount,
                    date = it.date,
                    comment = it.comment,
                    walletId = it.walletId,
                    isArchived = 1
                )
            )
        }

        val updatedIncomeSubGroupWithIncomeHistories = IncomeSubGroupWithIncomeHistories(
            incomeSubGroup = updatedSubGroup,
            incomeHistories = updatedHistories
        )

        return updatedIncomeSubGroupWithIncomeHistories
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomeGroupItemViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = ItemRowParentBinding.inflate(from, parent, false)
        return IncomeGroupItemViewHolder(binding, onClickListener)
    }

    override fun onBindViewHolder(holder: IncomeGroupItemViewHolder, position: Int) {
        val groupWithIncomeSubGroupsIncludingIncomeHistoriesNotArchived = incomeGroupWithIncomeSubGroupsIncludingIncomeHistoriesNotArchivedDiffer.currentList[position]
        holder.bind(groupWithIncomeSubGroupsIncludingIncomeHistoriesNotArchived)
    }

    override fun getItemCount(): Int {
        return incomeGroupWithIncomeSubGroupsIncludingIncomeHistoriesNotArchivedDiffer.currentList.size
    }

    fun submitList(incomeSubGroups: List<IncomeGroupWithIncomeSubGroupsIncludingIncomeHistories>) {
        incomeGroupWithIncomeSubGroupsIncludingIncomeHistoriesNotArchivedDiffer.submitList(incomeSubGroups)
    }

}
