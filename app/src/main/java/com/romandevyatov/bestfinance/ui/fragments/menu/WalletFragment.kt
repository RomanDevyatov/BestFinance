package com.romandevyatov.bestfinance.ui.fragments.menu


import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.romandevyatov.bestfinance.databinding.FragmentWalletBinding
import com.romandevyatov.bestfinance.db.entities.Wallet
import com.romandevyatov.bestfinance.ui.adapters.menu.income.DeleteItemClickListener
import com.romandevyatov.bestfinance.ui.adapters.WalletAdapter
import com.romandevyatov.bestfinance.viewmodels.WalletViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class WalletFragment : Fragment(), DeleteItemClickListener<Wallet> {

    private lateinit var binding: FragmentWalletBinding

    private val walletViewModel: WalletViewModel by viewModels()
    private lateinit var walletAdapter: WalletAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentWalletBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentWalletBinding.bind(view)

        binding.addExpenseButton.setOnClickListener {
            val nameOfNewWallet = binding.walletNameEditText.text.toString()
            val balanceOfNewWallet = binding.amountEditText.text.toString()
            walletViewModel.insertWallet(
                Wallet(
                    name = nameOfNewWallet,
                    balance = balanceOfNewWallet.toDouble()
                )
            )
        }

        walletViewModel.notArchivedWalletsLiveData.observe(viewLifecycleOwner) {
            walletAdapter.submitList(it)
        }

        initWalletRecyclerView()
    }

    private fun initWalletRecyclerView() {
        walletAdapter = WalletAdapter()
        binding.walletRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.walletRecyclerView.adapter = walletAdapter

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

                val notArchivedWallet = walletAdapter.walletDiffer.currentList[pos]

                val updatedWallet = Wallet(
                    id = notArchivedWallet.id,
                    name = notArchivedWallet.name,
                    balance = notArchivedWallet.balance,
                    isArchived = 1
                )

                walletViewModel.updateWallet(updatedWallet)

                Snackbar.make(viewHolder.itemView, "Wallet archived", Snackbar.LENGTH_LONG).apply {
                    setAction("UNDO") {
                        walletViewModel.updateWallet(notArchivedWallet)
                    }
                    show()
                }

            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.walletRecyclerView)
        }
    }

    override fun deleteItem(item: Wallet) {
        walletViewModel.deleteWallet(item)
    }

//    override fun deleteIncomeGroup(incomeGroup: IncomeGroup) {
//        incomeGroupViewModel.deleteIncomeGroup(incomeGroup)
//    }

//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        addMenuProvider(object : MenuProvider {
//            override fun onPrepareMenu(menu: Menu) {
//                MenuInflater.inflate(R.menu.option_menu, menu)
//            }
//
//            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
//                menuInflater.inflate(R.menu.option_menu, menu)
//            }
//
//            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
//                return menuItem.onNavDestinationSelected(navController)
//            }
//        })
//        return super.onCreateView(inflater, container, savedInstanceState)
//    }
}