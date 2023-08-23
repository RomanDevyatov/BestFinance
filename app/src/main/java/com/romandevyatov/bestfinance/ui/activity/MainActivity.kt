package com.romandevyatov.bestfinance.ui.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
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
class MainActivity() : AppCompatActivity(), OnExitAppListener {

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
        setOnDestinationChangedListener()
    }

    var showActionIcon = false

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.action_settings)?.isVisible = showActionIcon
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> Toast.makeText(this, "go to settings", Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onExitApp() {
        finish()
    }

    // for default app bar, navigate up fixing
    override fun onSupportNavigateUp(): Boolean {
        return when(navController.currentDestination?.id) {
            R.id.add_income_fragment,
            R.id.add_expense_fragment,
            R.id.add_transfer_fragment,
            R.id.history_fragment -> {
                sharedModViewModel.set(null)
                navController.navigate(R.id.home_fragment)
                true
            }
            R.id.groups_and_sub_groups_settings_fragment, R.id.wallets_settings_fragment -> {
                navController.navigate(R.id.more_fragment)
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
                R.id.home_fragment,
                R.id.wallet_fragment,
                R.id.more_fragment
            )
        )
        setupActionBarWithNavController(this, navController, appBarConfiguration)
    }

    private fun setNavigationBottomBar() {
        bottomNavigationView = binding.bottomNavigationView

        bottomNavigationView.setupWithNavController(navController)
    }

    private fun setOnDestinationChangedListener() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            setVisabilityOfBottomNavigationBar(destination.id)

            setVisabilityOfSettingsAction(destination.id)
        }
    }

    private fun setVisabilityOfBottomNavigationBar(destinationId: Int) {
        val bottomNavViewExcludedArray = arrayOf(
            R.id.add_income_fragment,
            R.id.add_income_group_fragment,
            R.id.add_income_sub_group_fragment,
            R.id.add_expense_fragment,
            R.id.add_expense_group_fragment,
            R.id.add_expense_sub_group_fragment,
            R.id.history_fragment,
            R.id.add_transfer_fragment,
            R.id.analyze_fragment,
            R.id.add_wallet_fragment,
            R.id.wallets_settings_fragment,
            R.id.update_wallet_fragment,
            R.id.groups_and_sub_groups_settings_fragment,
            R.id.update_expense_group_fragment,
            R.id.update_income_group_fragment,
            R.id.update_expense_sub_group_fragment,
            R.id.update_income_sub_group_fragment
        )

        if (bottomNavViewExcludedArray.contains(destinationId)) {
            bottomNavigationView.visibility = View.GONE
        } else {
            bottomNavigationView.visibility = View.VISIBLE
        }
    }

    private fun setVisabilityOfSettingsAction(destinationId: Int) {
        showActionIcon = destinationId == R.id.more_fragment
        invalidateOptionsMenu()
    }
}
