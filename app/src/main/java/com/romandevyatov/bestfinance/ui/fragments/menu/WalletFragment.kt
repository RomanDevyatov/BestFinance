package com.romandevyatov.bestfinance.ui.fragments.menu

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.romandevyatov.bestfinance.databinding.FragmentAddWalletBinding
import com.romandevyatov.bestfinance.ui.adapters.WalletAdapter
import com.romandevyatov.bestfinance.viewmodels.WalletViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class WalletFragment : Fragment() {

    private lateinit var binding: FragmentAddWalletBinding

    private val walletViewModel: WalletViewModel by viewModels()
    private lateinit var walletAdapter: WalletAdapter

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