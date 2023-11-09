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
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.FragmentBottomMenuWalletsBinding
import com.romandevyatov.bestfinance.ui.adapters.menu.wallet.WalletMenuAdapter
import com.romandevyatov.bestfinance.ui.adapters.menu.wallet.model.WalletItem
import com.romandevyatov.bestfinance.utils.Constants
import com.romandevyatov.bestfinance.utils.TextFormatter.removeTrailingZeros
import com.romandevyatov.bestfinance.viewmodels.foreachmodel.WalletViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime

@AndroidEntryPoint
class WalletFragment : Fragment() {

    private var _binding: FragmentBottomMenuWalletsBinding? = null
    private val binding get() = _binding!!

    private val walletViewModel: WalletViewModel by viewModels()
    private lateinit var walletMenuAdapter: WalletMenuAdapter

    private val addNewWalletString: String by lazy {
        getString(R.string.add_new_wallet)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBottomMenuWalletsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOnBackPressedHandler()

        walletViewModel.allWalletsNotArchivedLiveData.observe(viewLifecycleOwner) { walletList ->
            walletList?.map { WalletItem(it.id, it.name, removeTrailingZeros(it.balance.toString())) }?.toMutableList()?.let { walletItems ->
                val spinnerWalletItems: MutableList<WalletItem> = mutableListOf()

                walletItems.let {
                    spinnerWalletItems.addAll(walletItems)
                }

                spinnerWalletItems.add(WalletItem(null, addNewWalletString, null))

                walletMenuAdapter.submitList(spinnerWalletItems)
            }

        }

        initWalletRecyclerView()
    }

    private fun setOnBackPressedHandler() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.home_fragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
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
        val clickOnWalletListener = object : WalletMenuAdapter.ItemClickListener {

            override fun navigate(name: String) {
                val action = WalletFragmentDirections.actionNavigationWalletToUpdateWallet()
                action.walletName = name
                action.source = Constants.MENU_WALLET_FRAGMENT
                findNavController().navigate(action)
            }

            override fun navigateToAddNewWallet() {
                val action = WalletFragmentDirections.actionNavigationWalletToNavigationAddWallet()
                action.source = Constants.WALLETS_FRAGMENT
                findNavController().navigate(action)
            }
        }

        walletMenuAdapter = WalletMenuAdapter(clickOnWalletListener, addNewWalletString)

        binding.walletRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.walletRecyclerView.adapter = walletMenuAdapter

        val itemTouchHelperCallback = object: ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val position = viewHolder.adapterPosition

                val selectedWalletItem = walletMenuAdapter.walletDiffer.currentList[position]

                if (selectedWalletItem.name == addNewWalletString) {
                    return makeMovementFlags(0, 0)
                }

                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END

                return makeMovementFlags(dragFlags, swipeFlags)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                val selectedWalletItem = walletMenuAdapter.walletDiffer.currentList[position]

                walletViewModel.archiveWalletById(selectedWalletItem.id, LocalDateTime.now())

                Snackbar.make(viewHolder.itemView, getString(R.string.wallet_is_archived, selectedWalletItem.name), Snackbar.LENGTH_LONG).apply {
                    setAction("UNDO") {
                        walletViewModel.unarchiveWalletById(selectedWalletItem.id)
                    }
                    show()
                }

            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.walletRecyclerView)
        }
    }
}
