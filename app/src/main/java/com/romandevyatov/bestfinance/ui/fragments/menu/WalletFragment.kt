package com.romandevyatov.bestfinance.ui.fragments.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.romandevyatov.bestfinance.databinding.FragmentWalletBinding
import com.romandevyatov.bestfinance.db.entities.Wallet
import com.romandevyatov.bestfinance.ui.adapters.ItemClickListener
import com.romandevyatov.bestfinance.ui.adapters.WalletAdapter
import com.romandevyatov.bestfinance.viewmodels.WalletViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class WalletFragment : Fragment(), ItemClickListener<Wallet> {

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

//        binding.addWalletButton.setOnClickListener {
//            val nameOfNewWallet = binding.walletNameEditText.text.toString()
//            val balanceOfNewWallet = binding.walletBalanceEditText.text.toString()
//            walletViewModel.insertWallet(
//                Wallet(
//                    name = nameOfNewWallet,
//                    balance = balanceOfNewWallet.toDouble()
//                )
//            )
//
//            Snackbar.make(binding.root, "Wallet $nameOfNewWallet was added", Snackbar.LENGTH_SHORT).show()
//        }

        walletViewModel.walletsLiveData.observe(viewLifecycleOwner) {
            walletAdapter.submitList(it)
        }

        initRecyclerView()
    }

    private fun initRecyclerView() {
        walletAdapter = WalletAdapter(this)
        binding.walletRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.walletRecyclerView.adapter = walletAdapter
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