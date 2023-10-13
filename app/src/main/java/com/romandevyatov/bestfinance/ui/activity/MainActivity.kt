package com.romandevyatov.bestfinance.ui.activity

import android.Manifest.permission.RECORD_AUDIO
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.ActivityMainBinding
import com.romandevyatov.bestfinance.ui.fragments.add.history.AddIncomeHistoryFragment
import com.romandevyatov.bestfinance.ui.fragments.add.transfer.AddTransferFragment
import com.romandevyatov.bestfinance.utils.ThemeHelper
import com.romandevyatov.bestfinance.viewmodels.shared.SharedModifiedViewModel
import com.romandevyatov.bestfinance.viewmodels.shared.models.AddTransactionForm
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

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

        applySavedTheme()

    }

    fun applySavedTheme() {
        val isDarkModeEnabled = ThemeHelper.isDarkModeEnabled(this)
        val nightMode = if (isDarkModeEnabled) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)
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

//        val navHostFragment =
//            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
//        navController = navHostFragment.navController
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
            R.id.update_income_sub_group_fragment,
            R.id.settings_fragment
        )

        if (bottomNavViewExcludedArray.contains(destinationId)) {
            bottomNavigationView.visibility = View.GONE
        } else {
            bottomNavigationView.visibility = View.VISIBLE
        }
    }

    private var showSettingsActionIcon = false
    private var showVoiceActionIcon = false

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.action_settings)?.isVisible = showSettingsActionIcon
        menu?.findItem(R.id.action_voice)?.isVisible = showVoiceActionIcon

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_settings -> {
            navController.navigate(R.id.settings_fragment)
            true
        }

        R.id.action_voice -> {
            startVoiceRecognition()
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun startVoiceRecognition() {
        if (ContextCompat.checkSelfPermission(this, RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(RECORD_AUDIO), 1)
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val fragment = navHostFragment.childFragmentManager.fragments.first()
        if (fragment is AddIncomeHistoryFragment) {
            fragment.setIntentGlob(intent)
            fragment.startAddingTransaction("Start adding transaction.")
        } else if (fragment is AddTransferFragment) {
            fragment.setIntentGlob(intent)
            fragment.startAddingTransaction("Start adding transaction.")
        }
    }

    private fun setVisabilityOfSettingsAction(destinationId: Int) {
        showSettingsActionIcon = destinationId == R.id.more_fragment

        showVoiceActionIcon = when (destinationId) {
            R.id.add_income_fragment -> true
            R.id.add_transfer_fragment -> true
            R.id.add_expense_fragment -> true
            else -> {
                false
            }
        }

        invalidateOptionsMenu()
    }
}
