package com.romandevyatov.bestfinance.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity() : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setNavigationTopBar()
        setNavigationBottomBar()
    }

    private fun setNavigationTopBar() {
        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        toolbar.setupWithNavController(navController, appBarConfiguration)
        toolbar.setNavigationOnClickListener {
            if (navController.currentDestination?.id == R.id.navigation_add_income) {
                navController.navigate(R.id.navigation_home)
            } else {
                navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
            }
        }
    }

    lateinit var navController: NavController

// default app bar
//    override fun onSupportNavigateUp(): Boolean {
//        return when(navController.currentDestination?.id) {
//            R.id.navigation_add_income -> {
//                navController.navigate(R.id.navigation_home)
//                true
//            }
//            else -> navController.navigateUp()
//        }
//    }

    private fun setNavigationBottomBar() {
        val bottomNavigationView: BottomNavigationView = binding.bottomNavigationView

        val navController: NavController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_income,
                R.id.navigation_expense,
                R.id.navigation_wallet
            )
        )

        setupActionBarWithNavController(this, navController, appBarConfiguration)

        val bottomNavViewExcludedArray = arrayOf(
            R.id.navigation_add_income,
            R.id.navigation_add_expense,
            R.id.navigation_history
        )

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (bottomNavViewExcludedArray.contains(destination.id)) {
                bottomNavigationView.visibility = View.GONE
            } else {
                bottomNavigationView.visibility = View.VISIBLE
            }
        }


        bottomNavigationView.setupWithNavController(navController)
    }

}