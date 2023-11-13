package com.romandevyatov.bestfinance.ui.activity

import android.Manifest.permission.RECORD_AUDIO
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.romandevyatov.bestfinance.R
import com.romandevyatov.bestfinance.databinding.ActivityMainBinding
import com.romandevyatov.bestfinance.ui.activity.base.BaseActivity
import com.romandevyatov.bestfinance.ui.fragments.add.group.AddExpenseGroupFragment
import com.romandevyatov.bestfinance.ui.fragments.add.group.AddIncomeGroupFragment
import com.romandevyatov.bestfinance.ui.fragments.add.history.AddExpenseHistoryFragment
import com.romandevyatov.bestfinance.ui.fragments.add.history.AddIncomeHistoryFragment
import com.romandevyatov.bestfinance.ui.fragments.add.subgroup.AddExpenseSubGroupFragment
import com.romandevyatov.bestfinance.ui.fragments.add.subgroup.AddIncomeSubGroupFragment
import com.romandevyatov.bestfinance.ui.fragments.add.history.transfer.AddTransferFragment
import com.romandevyatov.bestfinance.ui.fragments.add.wallet.AddWalletFragment
import com.romandevyatov.bestfinance.ui.fragments.update.history.UpdateExpenseHistoryFragment
import com.romandevyatov.bestfinance.ui.fragments.update.history.UpdateIncomeHistoryFragment
import com.romandevyatov.bestfinance.ui.fragments.update.history.transfer.UpdateTransferHistoryFragment
import com.romandevyatov.bestfinance.ui.fragments.update.wallet.UpdateWalletFragment
import com.romandevyatov.bestfinance.utils.localization.LocaleUtil
import com.romandevyatov.bestfinance.utils.sharedpreferences.Storage
import com.romandevyatov.bestfinance.utils.theme.ThemeHelper
import com.romandevyatov.bestfinance.viewmodels.shared.SharedModifiedViewModel
import com.romandevyatov.bestfinance.viewmodels.shared.models.AddTransactionForm
import com.romandevyatov.bestfinance.viewmodels.shared.models.AddTransferForm
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity(), OnExitAppListener {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView

    private val sharedAddTransactionFromModViewModel: SharedModifiedViewModel<AddTransactionForm> by viewModels()
    private val sharedAddTransferFormModViewModel: SharedModifiedViewModel<AddTransferForm> by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setNavigationTopBar()
        setNavigationBottomBar()
        setOnDestinationChangedListener()

        applySavedTheme()

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    fun getProtectedStorage(): Storage {
        return storage
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

    fun updateAppLocale(locale: String) {
        storage.setPreferredLocale(locale)
        LocaleUtil.applyLocalizedContext(applicationContext, locale)
        recreate()
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
                sharedAddTransactionFromModViewModel.set(null)
                sharedAddTransferFormModViewModel.set(null)
                navController.popBackStack(R.id.home_fragment, false)
                true
            }
            R.id.groups_and_sub_groups_settings_fragment,
            R.id.wallets_settings_fragment -> {
                navController.popBackStack(R.id.more_fragment, false)
                true
            }
//            R.id.add_wallet_fragment -> {
//                sharedModifiedAddWalletFormViewModel.set(null)
//                navController.navigateUp()
//                true
//            }
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
            setVisibilityOfBottomNavigationBar(destination.id)

            setVisibilityOfDeleteAction(destination.id)
            setVisibilityOfVoiceAction(destination.id)
            setVisibilityOfSettingsAction(destination.id)

            invalidateOptionsMenu()
        }
    }

    private fun setVisibilityOfBottomNavigationBar(destinationId: Int) {
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
            R.id.update_income_history_fragment,
            R.id.update_expense_history_fragment,
            R.id.settings_fragment,
            R.id.select_currency_fragment,
            R.id.rates_fragment
        )

        if (bottomNavViewExcludedArray.contains(destinationId)) {
            bottomNavigationView.visibility = View.GONE
        } else {
            bottomNavigationView.visibility = View.VISIBLE
        }
    }

    private var showSettingsActionIcon = false
    private var showDeleteActionIcon = false
    private var showVoiceActionIcon = false

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.action_settings)?.isVisible = showSettingsActionIcon
        menu?.findItem(R.id.action_voice)?.isVisible = showVoiceActionIcon
        menu?.findItem(R.id.action_delete)?.isVisible = showDeleteActionIcon

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
            if (hasRecordAudioPermission()) {
                startVoiceRecognition()
            } else {
                requestRecordAudioPermission()
            }
            true
        }
        R.id.action_delete -> {
            when (val fragment = getCurrentFragment()) {
                is UpdateIncomeHistoryFragment -> {
                    fragment.deleteRecord()
                }
                is UpdateExpenseHistoryFragment -> {
                    fragment.deleteRecord()
                }
                is UpdateTransferHistoryFragment -> {
                    fragment.deleteRecord()
                }
                is UpdateWalletFragment -> {
                    fragment.deleteRecord()
                }
            }
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun hasRecordAudioPermission(): Boolean {
        val permission = RECORD_AUDIO
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestRecordAudioPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(RECORD_AUDIO), 1)
    }

    private fun startVoiceRecognition() {
        when (val fragment = getCurrentFragment()) {
            is AddIncomeHistoryFragment -> {
                fragment.startAddingTransaction(getString(R.string.adding_income_initial))
            }
            is AddExpenseHistoryFragment -> {
                fragment.startAddingTransaction(getString(R.string.adding_expense_initial))
            }
            is AddTransferFragment -> {
                fragment.startAddingTransaction(getString(R.string.adding_transfer_initial))
            }
            is AddWalletFragment -> {
                fragment.startAddingTransaction(getString(R.string.adding_wallet_initial))
            }
            is AddIncomeGroupFragment -> {
                fragment.startAddingTransaction(getString(R.string.adding_income_group_initial))
            }
            is AddExpenseGroupFragment -> {
                fragment.startAddingTransaction(getString(R.string.adding_income_group_initial))
            }
            is AddIncomeSubGroupFragment -> {
                fragment.startAddingTransaction(getString(R.string.adding_income_sub_group_initial))
            }
            is AddExpenseSubGroupFragment -> {
                fragment.startAddingTransaction(getString(R.string.adding_expense_sub_group_initial))
            }
        }
    }

    private fun getCurrentFragment(): Fragment? {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        return navHostFragment.childFragmentManager.fragments.firstOrNull()
    }

    private fun setVisibilityOfVoiceAction(destinationId: Int) {
        showVoiceActionIcon = when (destinationId) {
            R.id.add_income_fragment -> true
            R.id.add_transfer_fragment -> true
            R.id.add_expense_fragment -> true
            R.id.add_wallet_fragment -> true
            R.id.add_income_group_fragment -> true
            R.id.add_expense_group_fragment -> true
            R.id.add_income_sub_group_fragment -> true
            R.id.add_expense_sub_group_fragment -> true
            else -> {
                false
            }
        }
    }

    private fun setVisibilityOfSettingsAction(destinationId: Int) {
        showSettingsActionIcon = destinationId == R.id.more_fragment
    }

    private fun setVisibilityOfDeleteAction(destinationId: Int) {
        showDeleteActionIcon = when (destinationId) {
            R.id.update_income_history_fragment -> true
            R.id.update_expense_history_fragment -> true
            R.id.update_transfer_history_fragment -> true
            R.id.update_wallet_fragment -> true
            else -> {
                false
            }
        }
    }
}
