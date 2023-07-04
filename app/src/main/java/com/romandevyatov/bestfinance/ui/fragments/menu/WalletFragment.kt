package com.romandevyatov.bestfinance.ui.fragments.menu

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.romandevyatov.bestfinance.databinding.FragmentWalletBinding
import com.romandevyatov.bestfinance.db.entities.Wallet
import com.romandevyatov.bestfinance.ui.adapters.transactions_deprecated.income.DeleteItemClickListener
import com.romandevyatov.bestfinance.ui.adapters.wallet.WalletAdapter
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.WalletViewModel
import java.time.OffsetDateTime
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WalletFragment : Fragment(), DeleteItemClickListener<Wallet> {

    private lateinit var binding: FragmentWalletBinding

    private val walletViewModel: WalletViewModel by viewModels()
    private lateinit var walletAdapter: WalletAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentWalletBinding.inflate(inflater, container, false)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentWalletBinding.bind(view)

        binding.addExpenseButton.setOnClickListener {
            val action = WalletFragmentDirections.actionNavigationWalletToNavigationAddWallet()
            findNavController().navigate(action)
//            val nameOfNewWallet = binding.walletNameEditText.text.toString()
//            val balanceOfNewWallet = binding.amountEditText.text.toString()
//            walletViewModel.insertWallet(
//                Wallet(
//                    name = nameOfNewWallet,
//                    balance = balanceOfNewWallet.toDouble()
//                )
//            )
        }

        walletViewModel.notArchivedWalletsLiveData.observe(viewLifecycleOwner) {
            walletAdapter.submitList(it)
        }

        initWalletRecyclerView()
    }

    // алгоритмы требуют ли обучения илинет
    // хотим скажем вложить в акции - прогноз например
    // интерполяция доходов или расходов
    // как вариант задачи - сможет ли человек обслуживать не только кредит
    // но и машину(бензин(цену бензина), парковка, страховка, смена колес, замена запчастей, штрафы)
    // типо чат gpt - может ли потянуть конкретную модель, машину (сможете обслужить, советуем машину классом чуть ниже)
    // распознаватель речи - вставлять ли?
    // вариант - разговорник через переводчик на лету
    // начать с распознавания на самом телефоне и только потом думать о переносе в облако
    // перед глазами возникает саммари по
    // если нет категории (такой категории нет, добавить?)
    // облегчить насколько это возможно
    // идеально для человека, садящегося в машине, в автобусе
    // считывание истории карты - это дополнительно
    // посмотреть статьи с papers with code

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

                val selectedWallet = walletAdapter.walletDiffer.currentList[pos]

                val archivedWallet = Wallet(
                    id = selectedWallet.id,
                    name = selectedWallet.name,
                    balance = selectedWallet.balance,
                    archivedDate = OffsetDateTime.now(),
                    input = selectedWallet.input,
                    output = selectedWallet.output,
                    description = selectedWallet.description
                )

                walletViewModel.updateWallet(archivedWallet)

                Snackbar.make(viewHolder.itemView, "Wallet archived", Snackbar.LENGTH_LONG).apply {
                    setAction("UNDO") {
                        walletViewModel.updateWallet(selectedWallet)
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