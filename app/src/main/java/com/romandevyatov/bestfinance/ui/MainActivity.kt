package com.romandevyatov.bestfinance.ui

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.ActivityMainBinding
import com.romandevyatov.bestfinance.viewmodels.shared.SharedModifiedViewModel
import com.romandevyatov.bestfinance.viewmodels.shared.models.AddTransactionForm
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity() : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView

    private val sharedModViewModel: SharedModifiedViewModel<AddTransactionForm> by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setNavigationTopBar()
        setNavigationBottomBar()
    }

    // for default app bar, navigate up fixing
    override fun onSupportNavigateUp(): Boolean {
        return when(navController.currentDestination?.id) {
            R.id.navigation_add_income, R.id.navigation_add_expense, R.id.navigation_add_transfer -> {
                sharedModViewModel.set(null)
                navController.navigate(R.id.navigation_home)
                true
            }
            R.id.navigation_add_income_group -> {
                navController.navigate(R.id.navigation_add_income)
                true
            }
            R.id.navigation_add_income_sub_group -> {
                navController.navigate(R.id.navigation_add_income)
                true
            }
            R.id.navigation_add_expense_group -> {
                navController.navigate(R.id.navigation_add_expense)
                true
            }
            R.id.navigation_add_expense_sub_group -> {
                navController.navigate(R.id.navigation_add_expense)
                true
            }
            else -> navController.navigateUp()
        }
    }

    private fun setNavigationTopBar() {
        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        navController = findNavController(R.id.nav_host_fragment_activity_main)
//        val appBarConfiguration = AppBarConfiguration(navController.graph)
//        toolbar.setupWithNavController(navController, appBarConfiguration)
//        toolbar.setNavigationOnClickListener {
//            if (navController.currentDestination?.id == R.id.navigation_add_income) {
//                navController.navigate(R.id.navigation_home)
//            } else {
//                navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
//            }
//        }

        // for default app bar
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_wallet,
                R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(this, navController, appBarConfiguration)
    }

    private fun setNavigationBottomBar() {
        bottomNavigationView = binding.bottomNavigationView

        bottomNavigationView.setupWithNavController(navController)

        hideBottomNavigationBar()
    }

    private fun hideBottomNavigationBar() {
        val bottomNavViewExcludedArray = arrayOf(
            R.id.navigation_add_income,
            R.id.navigation_add_income_group,
            R.id.navigation_add_income_sub_group,
            R.id.navigation_add_expense,
            R.id.navigation_add_expense_group,
            R.id.navigation_add_expense_sub_group,
            R.id.navigation_history,
            R.id.navigation_add_transfer,
            R.id.navigation_analyze
        )

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (bottomNavViewExcludedArray.contains(destination.id)) {
                bottomNavigationView.visibility = View.GONE
            } else {
                bottomNavigationView.visibility = View.VISIBLE
            }
        }
    }
}
